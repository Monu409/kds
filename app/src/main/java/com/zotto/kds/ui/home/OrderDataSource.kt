package com.zotto.kds.ui.home

import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.zotto.kds.database.table.Order
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.restapi.RetroClient


class OrderDataSource(val context: Context,var homeRepository: HomeRepository) : PageKeyedDataSource<Int, Order>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Order>) {
      //  homeRepository.getAllIntialOrder(homeRepository.intianlOrderJsonMap()!!,params,callback)
        Log.e("allOrderJsonMap intial=","intial")
      //  homeRepository.getIntialOrdersFromLocal(params,callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Order>) {
      //  homeRepository.getAllOrder(homeRepository.allOrderJsonMap()!!,params,callback,0)
        Log.e("allOrderJsonMap before=","loadBefore")
      // homeRepository.getAllOrdersFromLocal(params,callback,0)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Order>) {

       // homeRepository.getAllOrder(homeRepository.allOrderJsonMap()!!,params,callback,1)
        Log.e("allOrderJsonMap after=","loadAfter")
       // homeRepository.getAllOrdersFromLocal(params,callback,1)
    }


}