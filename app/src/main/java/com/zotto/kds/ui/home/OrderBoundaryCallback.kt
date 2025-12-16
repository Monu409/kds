package com.zotto.kds.ui.home

import android.util.Log
import androidx.paging.PagedList
import com.zotto.kds.database.table.Order
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

class OrderBoundaryCallback(var homeRepository: HomeRepository) :
  PagedList.BoundaryCallback<Order>() {
  var PAGE_SIZE = 0
  private var FIRST_PAGE = 1
  override fun onZeroItemsLoaded() {

    getAllOrder(allOrderJsonMap(PAGE_SIZE, FIRST_PAGE)!!)
  }

  override fun onItemAtEndLoaded(itemAtEnd: Order) {
    getAllOrder(allOrderJsonMap(PAGE_SIZE, FIRST_PAGE)!!)
  }

  fun allOrderJsonMap(PAGE_SIZE: Int, FIRST_PAGE: Int): JSONObject? {

    val jsonObj = JSONObject()
    try {

      jsonObj.put("restId", SessionManager.getRestaurantId(homeRepository.context))
      jsonObj.put("today", Utility.getCurrentDate())
//      jsonObj.put("today", "2025-09-23")
      jsonObj.put("offSet", PAGE_SIZE)
      jsonObj.put("pageLimit", FIRST_PAGE)
      //print parameter
      Log.e(
        "MY gson.JSON:  ",
        "AS PARAMETER  $jsonObj" + "-token-" + SessionManager.getToken(homeRepository.context)
      )
    } catch (e: JSONException) {
      e.printStackTrace()
    }
    return jsonObj
  }

  fun getAllOrder(jsonObject: JSONObject) {
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      homeRepository.apiServices.getAllOrders(
        SessionManager.getToken(homeRepository.context),
        jsonObject.toString()
      )
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(
          { response -> onResponse(response) },
          { t -> onFailure(t) })
    )
  }

  private fun onResponse(response: GenericResponse<List<Order>>) {
    try {
      if (response.getStatus().equals("200")) {
        PAGE_SIZE = PAGE_SIZE + 1
        if (response.getData()!!.size > 0) {
          if (homeRepository.orderDao!!.isOrderExist(
              response.getData()!!.asReversed().get(0).order_id!!
            ) == null
          ) {
            homeRepository.orderDao!!.insertOrder(response.getData()!!.asReversed().get(0))
            homeRepository.productDao!!.insertProductList(response.getData()!!.asReversed().get(0).products!!)
          }
        }
      }

    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

//  private fun onResponse(response: GenericResponse<List<Order>>) {
//    try {
//      if (response.getStatus() == "200") {
//
//        val orders = response.getData().orEmpty()
//
//        if (orders.isNotEmpty()) {
//
//          // ✅ reverse list here
//          val reversedOrders = orders.asReversed()
//
//          PAGE_SIZE += 1
//
//          // use first item from reversed list
//          val firstOrder = reversedOrders[0]
//
//          if (homeRepository.orderDao!!
//              .isOrderExist(firstOrder.order_id!!) == null
//          ) {
//            homeRepository.orderDao!!.insertOrder(firstOrder)
//            homeRepository.productDao!!.insertProductList(firstOrder.products!!)
//          }
//
//          // ✅ send reversed list to UI
////          orderAdapter.submitList(reversedOrders)
//        }
//      }
//
//    } catch (e: Exception) {
//      e.printStackTrace()
//    }
//  }


  private fun onFailure(t: Throwable) {
    t.printStackTrace()
  }
}