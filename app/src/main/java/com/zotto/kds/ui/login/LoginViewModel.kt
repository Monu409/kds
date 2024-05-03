package com.zotto.kds.ui.login

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zotto.kds.database.table.Restaurant
import com.zotto.kds.repository.LoginRepository


class LoginViewModel(var loginRepository: LoginRepository): ViewModel() {
     var usernameMutableLiveData=MutableLiveData<String>()
     var passwoMutableLiveData=MutableLiveData<String>()
    var errorPassword = MutableLiveData<String>()
    var errorUsername = MutableLiveData<String>()
    var progressbarMutableLiveData = MutableLiveData<Int>()
    val progressbar:LiveData<Int> get() =loginRepository.progressbar
    init {
        usernameMutableLiveData.value=""
        passwoMutableLiveData.value=""
        progressbarMutableLiveData.value=4
        loginRepository.progressbarMutableLiveData=progressbarMutableLiveData
    }



    fun getRestaurant(): LiveData<List<Restaurant>?>? {
        return loginRepository.getRestaurant()
    }

    fun loginRestaurant(){
        try {
            Log.e("usernameData =",usernameMutableLiveData.toString() +"-passwoMutableLiveData-"+usernameMutableLiveData.value!!)
            if (TextUtils.isEmpty(usernameMutableLiveData.value.toString())){
                errorUsername.value="Enter username"
            }else if (TextUtils.isEmpty(passwoMutableLiveData.value.toString())){
                errorPassword.value="Enter password"
            }else{
                loginRepository.loginRestaurant(usernameMutableLiveData.value!!.toString(),passwoMutableLiveData.value!!.toString())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }catch (e:NullPointerException){
            e.printStackTrace()
        }

    }



}