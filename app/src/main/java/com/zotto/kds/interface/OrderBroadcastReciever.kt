package com.zotto.kds.`interface`

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.zotto.kds.database.table.Order
import com.zotto.kds.rabbitmq.Config
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.restapi.GenericResponse
import org.json.JSONObject
import java.util.Objects

class OrderBroadcastReciever(var homeRepository: HomeRepository) : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    try {
      if (intent!!.action == Config.ROBOT_NOTIFICATION) {
        val completeJson =
          JSONObject(Objects.requireNonNull(intent!!.getStringExtra("message")))
        val type = completeJson.getString("type")
        if (type == "remote_control") {
          val action = completeJson.getString("action")
          if (action == "table_data_res") {
            //  val type1 = object : TypeToken<List<TableFetchResponse?>?>() {}.type

          } else if (action == "robot_status") {
            val serial_number = completeJson.getString("serial_number")
            val current_status = completeJson.getString("current_status")

          }
        }
      } else if (intent!!.action == Config.ORDER_NOTIFICATION) {
        val completeJson = JSONObject(Objects.requireNonNull(intent!!.getStringExtra("message")))
        val newJson = completeJson.getJSONObject("message")
        Log.e(
          "completeJson =",
          completeJson.toString() + "-newJson-" + newJson.toString() + "--" + newJson.has("orderid")
        )
        if (completeJson.getString("type").equals("order")) {
          homeRepository.getSingleOrder(newJson.getString("orderid"))
        } else if (completeJson.getString("type").equals("order_notification")) {
          homeRepository.getSingleOrder(newJson.getString("orderId"))
        } else if (completeJson.getString("type").equals("endSession")) {
          homeRepository.deleteAllOrders()
        }

      } else if (intent!!.action == Config.APPUPDATE_NOTIFICATION) {
        val message = intent!!.getStringExtra("appupdate")!!

      } else if (intent.action == Config.LOCAL_IP_ORDER_NOTIFICATION) {
        val completeJson = JSONObject(Objects.requireNonNull(intent.getStringExtra("message")))

        Log.e("completeIpJson =", completeJson.toString())

        var gnOrder: GenericResponse<Order> = GenericResponse()

        var order: Order = Gson().fromJson(completeJson.toString(), Order::class.java)
        order.order_from="(IP)"
        gnOrder.apply {
          setData(order)
          setStatus("200")
          setMessage("Ip order received.")
        }
        homeRepository.onSingleOrderResponse(gnOrder)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}