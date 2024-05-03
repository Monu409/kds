package com.zotto.kds.utils

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.PROTOCOL
import com.rabbitmq.client.ConnectionFactory
import com.zotto.kds.rabbitmq.Config.Companion.HOSTNAME
import com.zotto.kds.rabbitmq.Config.Companion.ONLINE_EXCHANGE_NAME
import com.zotto.kds.rabbitmq.Config.Companion.PASSWORD
import com.zotto.kds.rabbitmq.Config.Companion.USERNAME
import org.json.JSONObject
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class SendResponse {

    constructor(jsonobject: JSONObject, id: String?) {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setupConnectionFactory(jsonobject, id)
    }

    private var factory: ConnectionFactory? = null
    private val queue: BlockingDeque<String> = LinkedBlockingDeque()

    fun publishToAMQP(id: String?) {
        //sleep and then try again
        val publishThread = Thread {
            while (true) {
                try {
                    val connection = factory!!.newConnection()
                    val ch = connection.createChannel()
                    ch.confirmSelect()
                    while (true) {
                        val message = queue.takeFirst()
                        try {
                            val map: MutableMap<String, Any?> =
                                HashMap()
                            map["x-match"] = "all"
                            map["Restid"] = id
                            map["Type"] = "Online"
                            val properties = AMQP.BasicProperties()
                                .builder().headers(map).build()
                            ch.basicPublish(ONLINE_EXCHANGE_NAME,
                                "",
                                properties,
                                message.toByteArray())
                            ch.waitForConfirmsOrDie()
                            Log.i("request123", message)
                            Utility.robotLog("rabbitRequest message="+message.toString())
                        } catch (e: Exception) {
                            Log.e("DataPayload:", e.message!!)
                            Utility.robotLog("rabbitRequest DataPayload="+e.message!!)
                            queue.putFirst(message)
                            throw e
                        }
                    }
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    Log.e("error", "Connection broken: " + e.javaClass.name)
                    try {
                        Thread.sleep(2000) //sleep and then try again
                    } catch (e1: InterruptedException) {
                        break
                    }
                }
            }
        }
        publishThread.start()
    }

    fun setupConnectionFactory(jsonobject: JSONObject, id: String?) {
        try {
            factory = ConnectionFactory()
            factory!!.username = USERNAME
            factory!!.password = PASSWORD
            factory!!.host = HOSTNAME
            factory!!.port = PROTOCOL.PORT
            publishToAMQP(id)
            publishMessage(jsonobject.toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun publishMessage(message: String) {
        Thread {
            try {
                Log.e("message", "[q] $message")
                queue.putLast(message)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }
}