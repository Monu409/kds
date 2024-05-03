package com.zotto.kds.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zotto.kds.LoginResultCallBacks

import com.zotto.kds.database.dao.RestaurantDao
import com.zotto.kds.database.table.Restaurant
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.utils.InternetConnection
import com.zotto.kds.utils.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import retrofit2.http.Body

class LoginRepository(var context: Context,var restaurantDao: RestaurantDao,var apiServices: ApiServices,val listener: LoginResultCallBacks) {
    val restaurMutableLiveData=MutableLiveData<Restaurant>()
    val restLiveData:LiveData<Restaurant> get() = restaurMutableLiveData
    var progressbarMutableLiveData = MutableLiveData<Int>()
    val progressbar:LiveData<Int> get() = progressbarMutableLiveData

    fun getRestaurant(): LiveData<List<Restaurant>?>? {
        return restaurantDao.getAllRestaurant()
    }

    fun insertRestaurant(restaurant: Restaurant){
        restaurantDao.insertRestaurant(restaurant)
    }

    fun loginRestaurant(username: String,password:String){
        login(loginApiJsonMap(username,password)!!,username,password)
    }
    private fun loginApiJsonMap(username: String, password: String): JSONObject? {
        val jsonObj = JSONObject()
        try {
            jsonObj.put("username", username)
            jsonObj.put("pass", password)
            //print parameter
            Log.e("MY gson.JSON:  ", "AS PARAMETER  $jsonObj")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObj
    }
    private fun login(jsonObject: JSONObject,username: String, password: String) {
        progressbarMutableLiveData.value=0
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(
                apiServices.kdsLogin(jsonObject.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            response -> onResponse(response,username,password)
                            Log.e("response",response.toString());
                        },
                        {
                            t -> onFailure(t)
                            var mToast = Toast.makeText(context,"Username or password is incorrect",Toast.LENGTH_LONG)
                            mToast.show()
                            Log.e("error",t.toString());
                        }
                    )
            )


    }
    private fun onResponse(response: GenericResponse<Restaurant>,username: String, password: String) {
        try {

            if (response.getStatus().equals("200")){
//                var authorizationToken="Basic "+Base64.encodeToString(("Zotto:"+"12345").toByteArray(charset("UTF-8")), Base64.NO_WRAP)
                var authorizationToken="Bearer "+response.getData()!!.token
                SessionManager.setToken(context,authorizationToken)
                SessionManager.setLogin(context,true)
                SessionManager.setRestaurantId(context,
                    response.getData()!!.restaurantid!!.toString())
                progressbarMutableLiveData.value=4
                listener.onSuccess(response.getMessage().toString())
                restaurantDao.deleteAll()
                restaurantDao.insertRestaurant(response.getData()!!)
            }else{
                progressbarMutableLiveData.value=4
                listener.onError(response.getMessage().toString())
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private  fun onFailure(t: Throwable) {
        progressbarMutableLiveData.value=4
        t.printStackTrace()
    }
}