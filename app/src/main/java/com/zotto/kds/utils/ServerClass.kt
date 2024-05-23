package com.zotto.kds.utils

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.zotto.kds.rabbitmq.Config
import com.zotto.kds.rabbitmq.RabbitmqService
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class ServerClass(var context: Context) :Thread(){

    lateinit var serverSocket: ServerSocket
    lateinit var inputStream: InputStream
    lateinit var  outputStream: OutputStream
    lateinit var socket: Socket
    private val TAG: String = ServerClass::class.java.getSimpleName()


    override fun run() {
        try {
            serverSocket = ServerSocket(8888)
            socket = serverSocket.accept()
            inputStream =socket.getInputStream()
            outputStream = socket.getOutputStream()
        }catch (ex: IOException){
            ex.printStackTrace()
        }

        val executors = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executors.execute(Runnable{
            kotlin.run {
                val buffer = ByteArray(1024)
                var byte:Int
                while (true){
                    try {
                        serverSocket = ServerSocket(8888)
                        socket = serverSocket.accept()
                        inputStream =socket.getInputStream()
                        outputStream = socket.getOutputStream()
                        byte =  inputStream.read(buffer)
                        if(byte > 0){
                            var finalByte = byte
                            handler.post(Runnable{
                                kotlin.run {
                                    var tmpMeassage = String(buffer,0,finalByte)
                                    val json = JSONObject(tmpMeassage)
                                    val type = json.getString("type")
                                    Log.i("Server class","$tmpMeassage")
                                    try {
                                        if (type == "remote_control") {
                                            val pushNotification = Intent(Config.ROBOT_NOTIFICATION)
                                            pushNotification.putExtra("message", json.toString())
                                            LocalBroadcastManager.getInstance(context!!).sendBroadcast(pushNotification)
                                        }else if (type == "endSession") {
                                            val pushNotification = Intent(Config.ORDER_NOTIFICATION)
                                            pushNotification.putExtra("message", json.toString())
                                            LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification)
                                        } else {
                                            val jsonObject1 = json.getJSONObject("message")
                                            if (jsonObject1.has("kds_active")) {
                                                val kdsActive = jsonObject1.getString("kds_active")
                                                if (kdsActive == "1") {
                                                    val pushNotification = Intent(Config.ORDER_NOTIFICATION)
                                                    pushNotification.putExtra("message", json.toString())
                                                    LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification)
                                                }
                                            } else {
                                                val pushNotification = Intent(Config.ORDER_NOTIFICATION)
                                                pushNotification.putExtra("message", json.toString())
                                                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification)
                                            }
                                        }
                                        if (Config.LOG_STATUS) Log.i(TAG + "rabbitdata=",
                                            json.toString())
                                    } catch (e: Exception) {
                                        if (Config.LOG_STATUS) Log.e(TAG + "rabbitdata=", "Exception: " + e.message)
                                    }
                                }
                            })

                        }
                    }catch (ex:IOException){
                        ex.printStackTrace()
                    }
                }
            }
        })
    }

    fun write(byteArray: ByteArray){
        try {
            Log.i("Server write","$byteArray sending")
            outputStream.write(byteArray)
        }catch (ex:IOException){
            ex.printStackTrace()
        }
    }
}