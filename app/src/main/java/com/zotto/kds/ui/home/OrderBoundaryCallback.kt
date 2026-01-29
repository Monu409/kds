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
//      jsonObj.put("today", "2026-01-28")
      jsonObj.put("offSet", PAGE_SIZE)
      jsonObj.put("pageLimit", 50)
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
            val columnWiseList = toColumnWise(response.getData()!!, 3)
            homeRepository.orderDao!!.insertOrder(columnWiseList.get(0))
            homeRepository.productDao!!.insertProductList(columnWiseList.get(0).products!!)
          }
        }
      }

    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun <T> toColumnWise(list: List<T>, spanCount: Int): List<T> {
    val result = mutableListOf<T>()

    val rows = Math.ceil(list.size / spanCount.toDouble()).toInt()

    for (col in 0 until spanCount) {
      for (row in 0 until rows) {
        val index = row * spanCount + col
        if (index < list.size) {
          result.add(list[index])
        }
      }
    }
    return result
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