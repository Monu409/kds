package com.zotto.kds.repository

import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.zotto.kds.adapter.OrderAdapter
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.ProductDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.localIP.SendTask
import com.zotto.kds.printing.HPRTPrinterPrinting
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.ui.home.HomeFragment
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.SessionManager.Companion.getRestaurantId
import com.zotto.kds.utils.Singleton
import com.zotto.kds.utils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar

class HomeRepository(
  var context: Context,
  var orderDao: OrderDao,
  var productDao: ProductDao,
  var apiServices: ApiServices,
  var orderAdapter: OrderAdapter,
  var fragment: HomeFragment,
  var fragmentManager: FragmentManager
) {

  fun getSingleOrder(orderid: String) {

    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices.getSingleOrder(
        SessionManager.getToken(context), getRestaurantId(context), orderid
      ).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({ response -> onSingleOrderResponse(response) },
          { t -> onSingleOrderFailure(t) })
    )
  }

  fun onSingleOrderResponse(response: GenericResponse<Order>) {
    Log.e("onSingleOrderResponse", response.toString()+"--"+response.getMessage());//Ip order received.
   // if(response.getMessage() != "Ip order received."){
      try {
        if (response.getStatus().equals("200")) {
          Log.e(
            "isorderexist=",
            orderDao.isOrderExist(response.getData()!!.order_id!!)
              .toString() + "--" + SessionManager.isAutoPrint(context)
          )
          if (orderDao!!.isOrderExist(response.getData()!!.order_id!!) == null && (response.getData()!!.order_status.equals(
              "Confirm", true
            ) || response.getData()!!.order_status.equals("New", true))
          ) {

            Log.e(
              "isorderexist=", "insertorder" + "-productsize-" + response.getData()!!.products!!.size
            )

//            orderDao!!.insertOrder(response.getData()!!)
//            productDao!!.insertProductList(response.getData()!!.products!!)
            orderDao.insertOrder(response.getData()!!)
//            var listProducts = Utility().convertJsonToList(context)
//            var ruleProducts: ArrayList<Product> = ArrayList<Product>()
//            for (product in response.getData()!!.products!!) {
//              listProducts.forEach { (key, value) ->
//                println("Key: $key, Values: $value")
//                for (mV in value){
//                  if(product.product_id.equals(mV)){
//                    ruleProducts.add(product)
//                  }
//                }
//                productDao!!.insertProductList(ruleProducts)
//              }
//            }
            Singleton.isactiveclicked = false
            var homeFragment = HomeFragment()
            homeFragment.startSpeech("Hi there! New order arrive. Please check.", "")
            if (SessionManager.isAutoPrint(context)) {
              var hprtPrinterPrinting = HPRTPrinterPrinting(context)
              hprtPrinterPrinting.kitchenReciept(
                response.getData()!!.products!!, response.getData()!!.order_id!!,context
              )
            }

          } else {
            orderDao.updateOrder(updateOrder(response.getData()!!))
            var listProducts = Utility().convertJsonToList(context)
            for (product in response.getData()!!.products!!) {
              listProducts.forEach { (key, value) ->
                println("Key: $key, Values: $value")
                for (mV in value){
                  if(product.product_id.equals(mV)){
                    productDao.updateProduct(product)
                  }
                }
              }
            }
            orderAdapter.notifyDataSetChanged()
            var homeFragment = HomeFragment()
            homeFragment.startSpeech(
              "Hi there! Please Check the update for order number ", response.getData()!!.order_id!!
            )
          }

        } else {
          Log.e("failed response=", response.getMessage().toString())
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
   // }

  }

  private fun onSingleOrderFailure(t: Throwable) {
    t.printStackTrace()
  }

  fun updateOrderJsonMap(
    date: String,
    comments: String,
    orderid: String,
    sequenceOrderId: String,
    status: String,
    time: String,
    prep_time: String,
    rname: String,
    ptime: String,
    orders: Order
  ): JSONObject? {

    val jsonObj = JSONObject()
    try {
      jsonObj.put("restId", SessionManager.getRestaurantId(context))
      jsonObj.put("date", date)
      jsonObj.put("comments", comments)
      jsonObj.put("orderid", orderid)
      jsonObj.put("sequence_order_id", sequenceOrderId)
      jsonObj.put("status", status)
      jsonObj.put("time", time)
      jsonObj.put("prep_time", prep_time)
      jsonObj.put("rname", rname)
      jsonObj.put("ptime", ptime)
      jsonObj.put("phone_number", orders.delivery_mobile)

      //print parameter
      Log.e(
        "MY gson.JSON:  ", "AS PARAMETER  $jsonObj" + "-token-" + SessionManager.getToken(context)
      )
    } catch (e: JSONException) {
      e.printStackTrace()
    }
    return jsonObj
  }

  fun updateRecallOrder(jsonObject: JSONObject, orderid: String, order: Order) {
    orderDao.updateOrder(orderid, "Confirm")
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices.updateOrder(SessionManager.getToken(context), jsonObject.toString())
        .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({ response -> onUpdateRecallOrderResponse(response, orderid) },
          { t -> onUpdateOrderFailure(t) })
    )
  }

  private fun onUpdateRecallOrderResponse(response: String, orderid: String) {
    try {
      var jsonObject = JSONObject(response)
      if (jsonObject.getString("RESULT").equals("success")) {
        //   orderDao.updateOrder(orderid,"Ready")
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun updateOrder(jsonObject: JSONObject, orderid: String,orders: Order) {
    orderDao.updateOrder(orderid, "Ready")
    sendOrderToLocalIp(orders,orderid)
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices.updateOrder(SessionManager.getToken(context), jsonObject.toString())
        .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({ response -> onUpdateOrderResponse(response, orderid) },
          { t -> onUpdateOrderFailure(t) })
    )
  }

  private fun sendOrderToLocalIp(orders: Order,orderid: String) {

    val fullJson = JSONObject()
    val messageJson = JSONObject()
    try {
      messageJson.put("restId", getRestaurantId(context))
      messageJson.put("orderid", orderid)
      messageJson.put("sequence_order_id", orders.sequence_order_id)
      messageJson.put("status", "Ready")
      messageJson.put("time", orders.delivery_time)
      messageJson.put("location", orders.order_location)
      messageJson.put("chain_id", "8")
      messageJson.put("chain_order_id", orders.chain_order_id)
      messageJson.put("table", orders.pos_table_name)
      messageJson.put("kds_active", orders.kds_active)
      messageJson.put("isreceieved", "1")
      fullJson.put("type", "orderUpdate")
      fullJson.put("message", messageJson)
      Log.e("responce", "" + fullJson)
      if(SessionManager.getSelectedPort(context)?.isEmpty() != true){
        var orderPort = SessionManager.getSelectedPort(context)?.toInt()
        var orderIp = SessionManager.getSelectedIp(context)
        val sendTask: SendTask = SendTask(context, fullJson.toString(), orderIp, orderPort!!)
        sendTask.execute()
      }
    } catch (e: JSONException) {
      e.printStackTrace()
    }

  }

  private fun onUpdateOrderResponse(response: String, orderid: String) {
    try {
      Log.e("onUpdateOrderResponse=", response)
      var jsonObject = JSONObject(response)
      if (jsonObject.getString("message").equals("success")) {
        var productList = kotlin.collections.ArrayList<Product>()
        productList.addAll(productDao!!.getProduct(orderid)!!)
        if (productList.size > 0) {
          for (product in productList) {
            var products = product
            products.update_status = "Ready"
            productDao!!.updateProduct(products)
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun onUpdateOrderFailure(t: Throwable) {
    t.printStackTrace()
  }

  fun updateproductJsonMap(
    product_id: String,
    serial_no: String,
    orderid: String,
    status: String,
    time: String,
    name: String,
    customer_name: String,
    table: String
  ): JSONObject? {

    val jsonObj = JSONObject()
    try {
      jsonObj.put("restId", SessionManager.getRestaurantId(context))
      jsonObj.put("product_id", product_id)
      jsonObj.put("serial_no", serial_no)
      jsonObj.put("orderid", orderid)
      jsonObj.put("status", status)
      jsonObj.put("time", time)
      jsonObj.put("name", name)
      jsonObj.put("customer_name", customer_name)
      jsonObj.put("table", table)
      //print parameter
      Log.e(
        "MY gson.JSON:  ", "AS PARAMETER  $jsonObj" + "-token-" + SessionManager.getToken(context)
      )
    } catch (e: JSONException) {
      e.printStackTrace()
    }
    return jsonObj
  }

  fun updateProduct(jsonObject: JSONObject, product: Product) {
    productDao!!.updateProduct(product)
    sendProductToLocalIp(jsonObject,product)
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices.updateProduct(SessionManager.getToken(context), jsonObject.toString())
        .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({ response -> onUpdateProductResponse(response) },
          { t -> onUpdateProductFailure(t) })
    )
  }

  private fun sendProductToLocalIp(jsonObject: JSONObject, product: Product) {
    try {
      if(SessionManager.getSelectedPort(context)?.isEmpty() != true){
        val fullJson = JSONObject()
        val messageJson = JSONObject()
        messageJson.put("restId", jsonObject.getString("restId"))
        messageJson.put("rname", orderDao.getOrder(product.order_id!!).rname)
        messageJson.put("chain_id", "8")
        messageJson.put("orderid", jsonObject.getString("orderid"))
        messageJson.put("sequence_order_id", jsonObject.getString("sequence_order_id"))
        messageJson.put("status", "Ready")
        messageJson.put("name", jsonObject.getString("name"))
        messageJson.put("ticket_id", product.ticket_id)
        messageJson.put("ticket_time", product.timestamp)
        messageJson.put("table", jsonObject.getString("table"))
        messageJson.put("time", jsonObject.getString("time"))
        messageJson.put("date", orderDao.getOrder(product.order_id!!).order_time)
        messageJson.put("customer_name", orderDao.getOrder(product.order_id!!).delivery_firstname)
        messageJson.put("order_product_id", product.order_product_id)
        messageJson.put("serial_no", product.serial_no)
        messageJson.put("order_location", orderDao.getOrder(product.order_id!!).order_location)
        messageJson.put("online", "0")
        fullJson.put("type", "DishReady")
        fullJson.put("message", messageJson)
        Log.e("responce", "" + fullJson)
        var orderPort = SessionManager.getSelectedPort(context)?.toInt()
        var orderIp = SessionManager.getSelectedIp(context)

        val sendTask: SendTask = SendTask(context, fullJson.toString(), orderIp, orderPort!!)
        sendTask.execute()
      }
    } catch (e: JSONException) {
      e.printStackTrace()
    }

  }

  private fun onUpdateProductResponse(response: String) {
    try {
      Log.e("updateProduct response=", response + "--")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun onUpdateProductFailure(t: Throwable) {
    t.printStackTrace()
  }

  fun updateproductTicketJsonMap(
    time: String,
    ticket_id: String,
    orderid: String,
    status: String,
    ticket_time: String,
    name: String,
    table: String
  ): JSONObject {

    val jsonObj = JSONObject()
    try {
      jsonObj.put("restId", SessionManager.getRestaurantId(context))
      jsonObj.put("time", time)
      jsonObj.put("ticket_id", ticket_id)
      jsonObj.put("orderid", orderid)
      jsonObj.put("status", status)
      jsonObj.put("ticket_time", ticket_time)
      jsonObj.put("name", name)
      jsonObj.put("table", table)
      //getDate(ticket_time!!.toLong(), "hh:mm:ss")
      //print parameter
      Log.e(
        "MY gson.JSON:  ", "AS PARAMETER  $jsonObj" + "-token-" + SessionManager.getToken(context)
      )
    } catch (e: JSONException) {
      e.printStackTrace()
    }
    return jsonObj
  }

  fun updateProductTicket(jsonObject: JSONObject) {
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices.updateProductTicket(SessionManager.getToken(context), jsonObject.toString())
        .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({ response -> onUpdateProductTicketResponse(response) },
          { t -> onUpdateProductTicketFailure(t) })
    )
  }

  private fun onUpdateProductTicketResponse(response: String) {
    try {

    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun onUpdateProductTicketFailure(t: Throwable) {
    t.printStackTrace()
  }

  fun getDate(milliSeconds: Long, dateFormat: String?): String? {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(dateFormat)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.setTimeInMillis(milliSeconds)
    return formatter.format(calendar.getTime())
  }

  fun updateOrder(orders: Order): Order {
    var order = orderDao.getOrder(orders.order_id!!)
    if (order == null) return orders
    order.order_time = orders.order_time
    order.order_status = orders.order_status
    order.paid = orders.paid
    order.pay_method = orders.pay_method
    order.pos_table_name = orders.pos_table_name
    order.tax = orders.tax
    order.grand_total = orders.grand_total
    order.comments = orders.comments
    order.general_discount = orders.general_discount
    order.delivery_charge = orders.delivery_charge
    order.delivery_address = orders.delivery_address
    order.delivery_city = orders.delivery_city
    order.delivery_firstname = orders.delivery_firstname
    order.delivery_country = orders.delivery_country
    order.delivery_mobile = orders.delivery_mobile
    order.booking_id = orders.booking_id
    order.order_location = orders.order_location
    order.products = orders.products
    return order
  }

  fun deleteAllOrders() {
    android.os.Handler(Looper.getMainLooper()).post {
      var orderList = kotlin.collections.ArrayList<Order>()
      orderList!!.addAll(orderDao.getAllOrder()!!)
      for (order in orderList) {
        orderDao!!.deleteOrder(order)
      }
      orderDao!!.deleteAllOrder()
      productDao!!.deleteAllProduct()
      var summaryRepository = SummaryRepository(context, orderDao)
      summaryRepository.deleteAllDataFromSummary()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        fragmentManager!!.beginTransaction().detach(fragment).commitNow()
        fragmentManager!!.beginTransaction().attach(fragment).commitNow()
      } else {
        fragmentManager!!.beginTransaction().detach(fragment).attach(HomeFragment()).commit();
      }
    }

  }
}