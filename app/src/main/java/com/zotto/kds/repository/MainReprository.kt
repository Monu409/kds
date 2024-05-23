package com.zotto.kds.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.utils.SendResponse
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

class MainReprository(var context: Context, var orderDao: OrderDao, var apiServices: ApiServices) {
    var progressbarMutableLiveData = MutableLiveData<Int>()
    val progressbar: LiveData<Int> get() = progressbarMutableLiveData

     fun rabbitRequest(type: String?, id: String?, serialNo: String?) {
        try {
            val jsonobject = JSONObject()
            jsonobject.put("type", "remote_control")
            when (type) {
                "tablefetch" -> jsonobject.put("action", "table_data")
                "callrobot" -> jsonobject.put("action", "product_point")
                "chargerobot" -> jsonobject.put("action", "charging_point")
                "deliverfood" -> {
                    jsonobject.put("action", "deliver")
                    jsonobject.put("serial_number", serialNo)
                }
            }
            Utility.robotLog("rabbitRequest ="+jsonobject.toString())
            SendResponse(jsonobject, id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}