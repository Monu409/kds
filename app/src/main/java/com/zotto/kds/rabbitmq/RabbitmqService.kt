package com.zotto.kds.rabbitmq

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rabbitmq.client.AMQP.PROTOCOL
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer
import com.zotto.kds.R
import com.zotto.kds.rabbitmq.Config.Companion.HOSTNAME
import com.zotto.kds.rabbitmq.Config.Companion.LOG_STATUS
import com.zotto.kds.rabbitmq.Config.Companion.NOTIFICATION_ID
import com.zotto.kds.rabbitmq.Config.Companion.ONLINE_EXCHANGE_NAME
import com.zotto.kds.rabbitmq.Config.Companion.PASSWORD
import com.zotto.kds.rabbitmq.Config.Companion.USERNAME
import com.zotto.kds.utils.ServerClass
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.Utility
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RabbitmqService:Service() {
    private var subscribeThread: Thread? = null
    private var factory: ConnectionFactory? = null
    private var incomingMessageHandler: Handler? = null
    private val TAG: String = RabbitmqService::class.java.getSimpleName()
    var sharedPreferences: SharedPreferences? = null
    var currentDate = ""
    var buildType:String? = ""
    var id:String? = null
    var selectedRest: List<String> = ArrayList()

    override fun onBind(p0: Intent?): IBinder? { return null}

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = resources.getString(R.string.app_name)
            val channel = NotificationChannel(CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            (Objects.requireNonNull(getSystemService(NOTIFICATION_SERVICE)) as NotificationManager).createNotificationChannel(
                channel)
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Kitchen Display Screen")
                .setContentText("Tab for more information...").setSmallIcon(R.mipmap.ic_launcher)
                .build()
            startForeground(NOTIFICATION_ID, notification)
        }
        sharedPreferences = getSharedPreferences("KitchenDisplayScreen", MODE_PRIVATE)
        id =SessionManager.getRestaurantId(this)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        currentDate = sdf.format(Date())

        incomingMessageHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                try {
                    val message = """ ${msg.data.getString("data")}""".trimIndent()
                    Log.d("request12", message)
                  //  writeLogCat(message)
                    val json = JSONObject(message)
                    val type = json.getString("type")
                    if (type == "remote_control") {
                        handleDataMessage(json, type)
                    } else {
                        val jsonObject = json.getJSONObject("message")
                        val resId = jsonObject.getString("restId")
                        val date = jsonObject.getString("date")
                        val chainId = jsonObject.getString("chain_id")
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        if (!json.isNull("type") && currentDate == date && resId == id)
                            handleDataMessage(json, type)
                    }
                } catch (e: Exception) {
                    if (LOG_STATUS)
                        Log.e(TAG + "Data_Payload: ", e.message.toString())

                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var serverClass=ServerClass(this)
        serverClass.start()
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = resources.getString(R.string.app_name)
            val channel = NotificationChannel(CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            (Objects.requireNonNull(getSystemService(NOTIFICATION_SERVICE)) as NotificationManager).createNotificationChannel(
                channel)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Kitchen Display Screen")
                .setContentText("Tab for more information...").build()
            startForeground(NOTIFICATION_ID, notification)
        }
        if (factory == null) {
            setupConnectionFactory()
            setupSubscription(incomingMessageHandler!!)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (subscribeThread != null) subscribeThread!!.interrupt()
        super.onDestroy()
    }

    fun notification(){
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = resources.getString(R.string.app_name)
            val channel = NotificationChannel(CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            (Objects.requireNonNull(getSystemService(NOTIFICATION_SERVICE)) as NotificationManager).createNotificationChannel(
                channel)
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Kitchen Display Screen")
                .setContentText("Tab for more information...").setSmallIcon(R.mipmap.ic_launcher)
                .build()
            startForeground(NOTIFICATION_ID, notification)
        }
    }
    fun setupConnectionFactory() {
        try {
            factory = ConnectionFactory()
            factory!!.setUsername(USERNAME)
            factory!!.setPassword(PASSWORD)
            factory!!.setHost(HOSTNAME)
            factory!!.setPort(PROTOCOL.PORT)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun setupSubscription(handler: Handler) {
        if (subscribeThread != null) {
            subscribeThread!!.interrupt()
            subscribeThread!!.start()
            return
        }
        subscribeThread = Thread {
            while (true) {
                try {
                    val connection = factory!!.newConnection()
                    val channel = connection.createChannel()
                    channel.exchangeDeclare(ONLINE_EXCHANGE_NAME, "headers", true)
                    channel.basicQos(1)
                    val args: MutableMap<String, Any> =
                        HashMap()
                    args["x-expires"] = 28800000
                    args["x-queue-mode"] = "lazy"
                    val map: MutableMap<String, Any> =
                        HashMap()
                    map["x-match"] = "all"
                        map["Restid"] = id!!
                    map["Type"] = "Online"
                    val map1: MutableMap<String, Any> =
                        HashMap()
                    map1["x-match"] = "all"
                        map1["Restid"] = id!!
                    map1["Type"] = "endSession"
                    val map2: MutableMap<String, Any> =
                        HashMap()
                    map2["action"] = "table_data"
                    map2["type"] = "remote_control"
                    map2["x-match"] = "all"
                    val queueName =
                        id + "_" + Utility.getImeiNumber(this) + "_" + "kdsnew"
                    val q = channel.queueDeclare(queueName, true, false, false, args)
                    channel.queueBind(queueName, ONLINE_EXCHANGE_NAME, "", map)
                    channel.queueBind(queueName, ONLINE_EXCHANGE_NAME, "", map1)
                    // channel.queueBind(queueName, ONLINE_EXCHANGE_NAME, "", map2);
                    val consumer = QueueingConsumer(channel)
                    channel.basicConsume(q.queue, true, consumer)
                    if (LOG_STATUS)
                        Log.i("queue: ", queueName)

                    //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Config.CONN_BROKE).putExtra("status", "true"));
                    while (true) {
                        val delivery = consumer.nextDelivery()
                        val message = String(delivery.body)
                        val msg = handler.obtainMessage()
                        val bundle = Bundle()
                        bundle.putString("data", message)
                        msg.data = bundle
                        handler.sendMessage(msg)
                        //Thread.sleep(1000);
                        // channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
                    }
                } catch (e: InterruptedException) {
                    Log.e("queue: ", "thread interrupted")
                    break
                } catch (e1: Exception) {
                    Log.d("queue: ", "Connection broken: " + e1.javaClass.name)
                    try {
                        Thread.sleep(2000) //sleep and then try again
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }
        subscribeThread!!.start()
    }

    private fun handleDataMessage(json: JSONObject, type: String) {
        try {
            if (type == "remote_control") {
                val pushNotification = Intent(Config.ROBOT_NOTIFICATION)
                pushNotification.putExtra("message", json.toString())
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            }else if (type == "endSession") {
                val pushNotification = Intent(Config.ORDER_NOTIFICATION)
                pushNotification.putExtra("message", json.toString())
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            } else {
                val jsonObject1 = json.getJSONObject("message")
                if (jsonObject1.has("kds_active")) {
                    val kdsActive = jsonObject1.getString("kds_active")
                    if (kdsActive == "1") {
                        val pushNotification = Intent(Config.ORDER_NOTIFICATION)
                        pushNotification.putExtra("message", json.toString())
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                    }
                } else {
                    val pushNotification = Intent(Config.ORDER_NOTIFICATION)
                    pushNotification.putExtra("message", json.toString())
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                }
            }
            if (LOG_STATUS) Log.i(TAG + "rabbitdata=",
                json.toString())
        } catch (e: Exception) {
            if (LOG_STATUS) Log.e(TAG + "rabbitdata=", "Exception: " + e.message)
        }
    }
    @JvmName("getSelectedRest1")
    private fun getSelectedRest(): List<String?>? {
        if (sharedPreferences != null) {
            val gson = Gson()
            val json = sharedPreferences!!.getString("SelectedRest", "")
            val type = object : TypeToken<List<String?>?>() {}.type
            var items = gson.fromJson<List<String?>>(json, type)
            if (items == null) items = ArrayList()
            return items
        }
        return null
    }
}