package com.zotto.kds.utils

import android.content.Context
import android.net.ConnectivityManager

class InternetConnection {
    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    companion object{
        fun checkConnection(context: Context): Boolean {
            return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
        }
    }
}