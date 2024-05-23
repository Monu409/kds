package com.zotto.kds.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zotto.kds.R
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Utility {
    companion object{
        fun databaseSynchingLog(text: String) {
            val logFile: File =
                File(Environment.getExternalStorageDirectory().toString() + "/databaseSynchingLog.txt")
            if (!logFile.exists()) {
                try {
                    logFile.getParentFile().mkdirs()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
            try {
                if (logFile.exists()) {
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                } else {
                    logFile.getParentFile().mkdirs()
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        fun frontScreenLog(text: String) {
            val logFile: File =
                File(Environment.getExternalStorageDirectory().toString() + "/frontscreenlog.txt")
            if (!logFile.exists()) {
                try {
                    logFile.getParentFile().mkdirs()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
            try {
                if (logFile.exists()) {
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                } else {
                    logFile.getParentFile().mkdirs()
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        fun scalingLog(text: String) {
            val logFile: File =
                File(Environment.getExternalStorageDirectory().toString() + "/scaling.txt")
            if (!logFile.exists()) {
                try {
                    logFile.getParentFile().mkdirs()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
            try {
                if (logFile.exists()) {
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                } else {
                    logFile.getParentFile().mkdirs()
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        fun robotLog(text: String) {
            val logFile: File =
                File(Environment.getExternalStorageDirectory().toString() + "/robotlog.txt")
            if (!logFile.exists()) {
                try {
                    logFile.getParentFile().mkdirs()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
            try {
                if (logFile.exists()) {
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                } else {
                    logFile.getParentFile().mkdirs()
                    val fw: FileWriter = FileWriter(logFile, true) // the true will
                    // append the new
                    // data
                    fw.write(text + "\n") // appends the string to the file
                    fw.close()
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }


        fun getImeiNumber(context: Context):String{
            var imeino=""
            var deviceId=""
            try {
                deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                } else {
                    val mTelephony = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
                            return ""
                        }
                    }
                    assert(mTelephony != null)
                    if (mTelephony!!.deviceId != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mTelephony!!.imei
                        } else {
                            mTelephony!!.deviceId
                        }
                    } else {
                        Settings.Secure.getString(context.contentResolver,
                            Settings.Secure.ANDROID_ID)
                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
            }catch (e:SecurityException){
                e.printStackTrace()
            }finally {
                // return imeino
                return deviceId!!
            }
        }

        @SuppressLint("NewApi")
        fun getCurrentDate():String{
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val currentDate: String = simpleDateFormat.format(Date())
            return currentDate
        }
    }
}