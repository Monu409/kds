package com.zotto.kds.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.utils.Singleton
import com.zotto.kds.utils.Utility

class HomeViewModel(var homeRepository: HomeRepository) : ViewModel() {

  var orderlivedata: LiveData<PagedList<Order>>? = MutableLiveData<PagedList<Order>>()
  var productListMutableLiveData: MutableLiveData<List<Product>>? = MutableLiveData<List<Product>>()
  val productlivedata: LiveData<List<Product>> get() = productListMutableLiveData!!

  init {
    val config = PagedList.Config.Builder()
      .setEnablePlaceholders(true)
      .setPrefetchDistance(0)
      //.setInitialLoadSizeHint(4)
      .setPageSize(100)
      .build()

    if (Singleton.ordertype.equals("active")) {
      val data = homeRepository.orderDao.getAllDatasourceActiveOrder()
      orderlivedata = LivePagedListBuilder(data, config)
        .setBoundaryCallback(OrderBoundaryCallback(homeRepository))
        .build()

    } else if (Singleton.ordertype.equals("completed")) {
      val data = homeRepository.orderDao.getAllDatasourceCompletedOrder()
      orderlivedata = LivePagedListBuilder(data, config)
        .setBoundaryCallback(OrderBoundaryCallback(homeRepository))
        .build()
    }

  }

  fun getAllOrderFromLocal(context: Context) {
    Log.e(
      "getProductsFromLocal=",
      Singleton.ordertype
    )
    if (Singleton.ordertype.equals("active")) {
      var allActiveOrder: List<Order>? = homeRepository.orderDao.getAllActiveOrderNew()!!.asReversed()
      if (!allActiveOrder.isNullOrEmpty()) {
        Singleton.isactiveclicked = false
        var products: ArrayList<Product>? = ArrayList()
        for (order in allActiveOrder) {
//          var listProducts = Utility().convertJsonToList(context)
//          for (product in order.products!!) {
//            listProducts.forEach { (key, value) ->
//              println("Key: $key, Values: $value")
//              for (mV in value){
//                if(product.product_id.equals(mV)){
//                  products!!.add(product)
//                }
//              }
//            }
//          }
          products!!.addAll(order.products!!)
//          }
          Log.e("getProducts data=", "$products")
          productListMutableLiveData!!.postValue(products)
        }
      } else if (allActiveOrder != null && allActiveOrder.isEmpty()) {
        productListMutableLiveData!!.postValue(emptyList())
      }
    }
    else if (Singleton.ordertype.equals("completed")) {
      var allActiveOrder: List<Order>? = homeRepository.orderDao.getAllCompletedOrder()
      if (!allActiveOrder.isNullOrEmpty()) {
        if (!Singleton.isactiveclicked) {
          Singleton.isactiveclicked = false
          productListMutableLiveData!!.postValue(
            allActiveOrder[0].products
          )
        }
      }
      else if (allActiveOrder != null && allActiveOrder.isEmpty()) {
        productListMutableLiveData!!.postValue(emptyList())
      }
    }

  }

//  fun getAllOrderFromLocal() {
//    Log.e(
//      "getAllOrderFromLocal=",
//      Singleton.ordertype + "--" + homeRepository.orderDao!!.getAllActiveOrder()!!.size
//    )
//    if (Singleton.ordertype.equals("active")) {
//      if (homeRepository.orderDao!!.getAllActiveOrder()!!.size > 0) {
//        if (!Singleton.isactiveclicked) {
//          Singleton.isactiveclicked = false
//          productListMutableLiveData!!.postValue(
//            homeRepository.orderDao.getAllActiveOrder()!!.get(0).products
//          )
//        }
//      }
//    } else if (Singleton.ordertype.equals("completed")) {
//      if (homeRepository.orderDao.getAllCompletedOrder()!!.size > 0) {
//        if (!Singleton.isactiveclicked) {
//          Singleton.isactiveclicked = false
//          productListMutableLiveData!!.postValue(
//            homeRepository.orderDao.getAllCompletedOrder()!!.get(0).products
//          )
//        }
//      }
//    }
//
//  }

  fun deleteAllDataFromSummary() {
    productListMutableLiveData!!.postValue(null)
  }

  fun updateOrder(
    date: String,
    comments: String,
    orderid: String,
    sequenceOrderId: String,
    status: String,
    time: String,
    prep_time: String,
    rname: String,
    ptime: String,
    order: Order
  ) {

    homeRepository.updateOrder(
      homeRepository.updateOrderJsonMap(
        date,
        comments,
        orderid,
        sequenceOrderId,
        status,
        time,
        prep_time,
        rname,
        ptime,
        order
      )!!, orderid, order
    )
  }

  fun updateProduct(context: Context,
    product_id: String, serial_no: String, orderid: String, status: String, time: String,
    name: String, customer_name: String, table: String, product: Product
  ) {
    homeRepository.updateProduct(
      homeRepository.updateproductJsonMap(
        product_id,
        serial_no,
        orderid,
        status,
        time,
        name,
        customer_name,
        table
      )!!, product
    )
    getAllOrderFromLocal(context)
  }

  fun updateProductTicket(
    time: String, ticket_id: String, orderid: String,
    status: String, ticket_time: String, name: String, table: String
  ) {
    homeRepository.updateProductTicket(
      homeRepository.updateproductTicketJsonMap(
        time,
        ticket_id,
        orderid,
        status,
        ticket_time,
        name,
        table
      )!!
    )
  }

}