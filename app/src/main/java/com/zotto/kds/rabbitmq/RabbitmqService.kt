package com.zotto.kds.rabbitmq

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
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
import com.zotto.kds.rabbitmq.Config.Companion.ROUTING_EXCHANGE_NAME
import com.zotto.kds.rabbitmq.Config.Companion.USERNAME
import com.zotto.kds.utils.ServerClass
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.Utility
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
//    var ruleMatchProductId: List<String> = ArrayList()
    var ruleMatchProductId: MutableList<String> = ArrayList()

    override fun onBind(p0: Intent?): IBinder? { return null}

    fun getProductIdsFromJson(jsonString: String): List<String> {
        val jsonObject = JSONObject(jsonString)
        val messageObject = jsonObject.getJSONObject("message")
        val productIdsString = messageObject.getString("productIds")

        // Remove the square brackets and split by comma to get individual product IDs
        val productIds = productIdsString.trim('[', ']').split(",")

        return productIds
    }

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
                    Log.e("request12", message);
                  //  writeLogCat(message)
                    val json = JSONObject(message)
//                    val json = JSONObject("{\"type\":\"order_notification\",\"message\":{\"restId\":\"43358486\",\"ruleId\":1,\"orderId\":\"1817154939152\",\"productIds\":\"[DC0BJF9YAKIIE]\"}}")
                    val type = json.getString("type")
                    if (type == "remote_control") {
                        handleDataMessage(json, type)
                    }
                    else if(type == "order_notification"){
//                        val jsonObject = json.getJSONObject("message")
                        val jsonObject = json.getJSONObject("message")
                        val ruleId = jsonObject.getString("ruleId")
                        val restId = jsonObject.getString("restId")

                        ruleMatchProductId = getStringListFromPreferences(this@RabbitmqService, "myListKey").toMutableList()
                        if(ruleMatchProductId==null){
                            ruleMatchProductId = ArrayList()
                        }
                        val productIdsArr = convertPosToJSONArray(jsonObject.getString("productIds"))
                        var selectedRuleId = SessionManager.getRuleId(this@RabbitmqService)
                        for (i in 0 until productIdsArr.length()) {
                            val item = productIdsArr.getString(i)
                            if(selectedRuleId.equals(ruleId)){
                                ruleMatchProductId.add(item)
                            }
                        }
                        val textList: List<String> = ArrayList(ruleMatchProductId)
                        saveStringListToPreferences(this@RabbitmqService,"myListKey",textList)
//                        val jsonText = gson.toJson(textList)
//                        SessionManager.setRuleProducts(this@RabbitmqService, jsonText)

//                        val deviceId = jsonObject.getString("deviceId")
//                        val productIds = jsonObject.getString("productIds")
//                        val productIds = jsonObject.getJSONArray("productIds")[0]
//                        var allIds = getProductIdsFromJson(jsonObject.getString("productIds"))
//                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

                        if (!json.isNull("type") && selectedRuleId.equals(ruleId))
                            handleDataMessage(json, type)
                    }
                    else {
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

    fun convertPosToJSONArray(productIds: String): JSONArray {
        // Remove the surrounding brackets and split the string into individual IDs
        val cleanedProductIds = productIds.removeSurrounding("[", "]")
        val productIdArray = cleanedProductIds.split(",")

        // Create a JSONArray and add each product ID
        val jsonArray = JSONArray()
        for (id in productIdArray) {
            jsonArray.put(id.trim()) // Trim to remove any extra spaces
        }

        return jsonArray
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
                    var selectedRuleId: String? = SessionManager.getRuleId(this@RabbitmqService)
                    val connection = factory!!.newConnection()
                    val channel = connection.createChannel()
                    channel.exchangeDeclare(ONLINE_EXCHANGE_NAME, "headers", true)
                    channel.exchangeDeclare(ROUTING_EXCHANGE_NAME, "headers", true)
                    channel.basicQos(1)
                    val args: MutableMap<String, Any> =
                        HashMap()
                    args["x-expires"] = 28800000
                    args["x-queue-mode"] = "lazy"
                    val map: MutableMap<String, Any> = HashMap()
                    map["x-match"] = "all"
                    map["Restid"] = id!!
                    map["Type"] = "Online"
                    val map1: MutableMap<String, Any> = HashMap()
                    map1["x-match"] = "all"
                    map1["Restid"] = id!!
                    map1["Type"] = "endSession"
                    val map2: MutableMap<String, Any> = HashMap()
                    map2["action"] = "table_data"
                    map2["type"] = "remote_control"
                    map2["x-match"] = "all"

                    val map3: MutableMap<String, Any> = HashMap()
                    map3["x-match"] = "any"
                    map3["Restid"] = id!!
                    map3["Ruleid"] = selectedRuleId!!
                    map3["Type"] = "order_update"
                    map3["Notification"] = true
                    Log.e("selectedRuleId",selectedRuleId)


                    val queueName = id + "_" + Utility.getImeiNumber(this) + "_" + "kdsnew"
//                    val queueNameForRoute = id + "_" + selectedRuleId+ "_" + Utility.getImeiNumber(this)+"_" + "kds_on_routing"
                    val q = channel.queueDeclare(queueName, true, false, false, args)
//                    val q2 = channel.queueDeclare(queueNameForRoute, true, false, false, args)
                    channel.queueBind(queueName, ONLINE_EXCHANGE_NAME, "", map)
                    channel.queueBind(queueName, ONLINE_EXCHANGE_NAME, "", map1)
//                    channel.queueBind(queueName, ONLINE_EXCHANGE_NAME, "", map2);
//                    channel.queueBind(queueNameForRoute, ROUTING_EXCHANGE_NAME, "", map3)
                    val consumer = QueueingConsumer(channel)
                    channel.basicConsume(q.queue, false, consumer)
//                    channel.basicConsume(q2.queue, false, consumer)
                    if (LOG_STATUS)
                        Log.e("queue: ", queueName)
//                        Log.e("queue2: ", queueNameForRoute)

                    //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Config.CONN_BROKE).putExtra("status", "true"));
                    while (true) {
                        val delivery = consumer.nextDelivery()
                        val message = String(delivery.body)
                        val msg = handler.obtainMessage()
                        val bundle = Bundle()
                        bundle.putString("data", message)
                        msg.data = bundle
                        Log.e("messageData",message);
                        handler.sendMessage(msg)
//                        Thread.sleep(1000);
                         channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
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

    fun saveStringListToPreferences(context: Context, key: String, list: List<String>) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert List to a single String and save
        val listString = list.joinToString(",")  // Join list with comma as separator
        editor.putString(key, listString)
        editor.apply()
    }

    fun getStringListFromPreferences(context: Context, key: String): List<String> {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Retrieve the String and convert back to List
        val listString = sharedPreferences.getString(key, "")
//        return listString?.split(",") ?: emptyList()
        return listString?.split(",")?: emptyList()
    }
}