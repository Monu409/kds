package com.zotto.kds.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.utils.Singleton

class SummaryRepository(var context: Context, var orderDao: OrderDao) {
    var orderListMutableLiveData: MutableLiveData<List<Order>>?=MutableLiveData<List<Order>>()
    val orderlivedata: LiveData<List<Order>> get() = orderListMutableLiveData!!
    var productListMutableLiveData: MutableLiveData<List<Product>>?=MutableLiveData<List<Product>>()
    val productlivedata: LiveData<List<Product>> get() = productListMutableLiveData!!
    var productList=ArrayList<Product>()
    fun getAllOrderFromLocal(){
         Log.e("SummaryRepository=",orderDao.getAllActiveOrder()!!.size.toString()+"-value-"
                 + orderListMutableLiveData!!.value+"-ordertype-"+Singleton.ordertype)
       if (Singleton.ordertype.equals("active")){
           orderListMutableLiveData!!.postValue(orderDao.getAllActiveOrder())
           if (orderDao.getAllActiveOrder()!!.size>0){
               Log.e("SummaryRepository =",orderDao.getAllActiveOrder()!!.get(0).products!!.size.toString()+"--"
                       +productList.size+"--"+Singleton.isactiveclicked)
               if (!Singleton.isactiveclicked){
                   Singleton.isactiveclicked=false
                   productListMutableLiveData!!.postValue(orderDao.getAllActiveOrder()!!.get(0).products)
               }
           }
       }else if (Singleton.ordertype.equals("completed")){
           orderListMutableLiveData!!.postValue(orderDao.getAllCompletedOrder())
           if (orderDao.getAllCompletedOrder()!!.size>0){
               Log.e("SummaryRepository =",orderDao.getAllCompletedOrder()!!.get(0).products!!.size.toString()+"--"
                       +productList.size+"--"+Singleton.isactiveclicked+"-ordertype-"+Singleton.ordertype)
               if (!Singleton.isactiveclicked){
                   Singleton.isactiveclicked=false
                   productListMutableLiveData!!.postValue(orderDao.getAllCompletedOrder()!!.get(0).products)
               }
           }
       }

    }
    fun deleteAllDataFromSummary(){
        orderListMutableLiveData!!.postValue(null)
        productListMutableLiveData!!.postValue(null)
    }
}