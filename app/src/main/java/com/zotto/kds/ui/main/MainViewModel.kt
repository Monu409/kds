package com.zotto.kds.ui.main

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zotto.kds.database.table.Order
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.repository.MainReprository

class MainViewModel(val mainReprository: MainReprository) : ViewModel() {
     var isAllFabsVisible=false

    init {
        isAllFabsVisible=false
    }
    fun allFabsVisible(fab:FloatingActionButton){
        fab.visibility=View.VISIBLE
    }


    fun mainfabOnclick(){
        if (!isAllFabsVisible){
            isAllFabsVisible=true
        }else{
            isAllFabsVisible=false
        }

        Log.e("isAllFabsVisible =",isAllFabsVisible.toString()+"--")

    }

}