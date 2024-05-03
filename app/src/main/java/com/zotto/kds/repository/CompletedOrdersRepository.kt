package com.zotto.kds.repository

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.zotto.kds.adapter.CompletedOrderAdapter
import com.zotto.kds.adapter.OrderAdapter
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.ProductDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.ui.completedorders.CompletedOrders
import com.zotto.kds.ui.home.HomeFragment
import com.zotto.kds.utils.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

class CompletedOrdersRepository(var context: Context, var orderDao: OrderDao, var productDao: ProductDao, var apiServices: ApiServices, var completedOrderAdapter: CompletedOrderAdapter,  var fragmentManager: FragmentManager) {
    fun updateOrderJsonMap(date:String,comments:String,orderid:String,status:String,time:String
                           ,prep_time:String,rname:String,ptime:String,orders: Order
    ): JSONObject? {

        val jsonObj = JSONObject()
        try {
            jsonObj.put("restId", SessionManager.getRestaurantId(context))
            jsonObj.put("date", date)
            jsonObj.put("comments", comments)
            jsonObj.put("orderid", orderid)
            jsonObj.put("status", status)
            jsonObj.put("time", time)
            jsonObj.put("prep_time", prep_time)
            jsonObj.put("rname", rname)
            jsonObj.put("ptime", ptime)
            jsonObj.put("phone_number", orders.delivery_mobile)

            //print parameter
            Log.e("MY gson.JSON:  ", "AS PARAMETER  $jsonObj"+"-token-"+SessionManager.getToken(context))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObj
    }
    fun updateRecallOrder(jsonObject: JSONObject, orderid: String) {
        orderDao.updateOrder(orderid,"Confirm")
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            apiServices.updateOrder(SessionManager.getToken(context),jsonObject.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response -> onUpdateRecallOrderResponse(response,orderid) },
                    { t -> onUpdateOrderFailure(t) })
        )
    }
    private fun onUpdateRecallOrderResponse(response:String,orderid: String) {
        try {
            var jsonObject= JSONObject(response)
            if (jsonObject.getString("RESULT").equals("success")){
                //orderDao.updateOrder(orderid,"Ready")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private  fun onUpdateOrderFailure(t: Throwable) {
        t.printStackTrace()
    }
}