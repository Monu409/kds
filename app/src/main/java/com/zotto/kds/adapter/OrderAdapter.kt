package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
//import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
//import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.*
import com.zotto.kds.R
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.printing.HPRTPrinterPrinting
import com.zotto.kds.printing.PrintingOther
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.home.HomeFragment
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale


class OrderAdapter(var context: Context, var orderOnClickListner: OrderOnClickListner,private var itemWidth: Int = 0) : PagedListAdapter<Order, OrderAdapter.MyViewHolder>(DiffUtil()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.orders_row, parent, false)
    if (itemWidth == 0) {
      val displayMetrics = parent.context.resources.displayMetrics
      itemWidth = displayMetrics.widthPixels / 4
    }

    view.layoutParams = view.layoutParams.apply {
      width = itemWidth
    }

    return MyViewHolder(view, context, orderOnClickListner)
  }

  class MyViewHolder(
    itemView: View,
    var context: Context,
    var orderOnClickListner: OrderOnClickListner
  ) : RecyclerView.ViewHolder(itemView),
    ProductAdpter.ProductOnClickListner {
    var table_name = itemView.findViewById<TextView>(R.id.table_name)
    var tableNumber = itemView.findViewById<TextView>(R.id.table_number)
    var order_id = itemView.findViewById<TextView>(R.id.order_id)
    var customer_name = itemView.findViewById<TextView>(R.id.customer_name)
    var preparing_time = itemView.findViewById<TextView>(R.id.preparing_time)
    var product_recyclerView = itemView.findViewById<RecyclerView>(R.id.product_recyclerView)
    var order_header = itemView.findViewById<ConstraintLayout>(R.id.order_header)
    var mComment = itemView.findViewById<TextView>(R.id.comment)
    var mPrintOrder = itemView.findViewById<ImageView>(R.id.print_order_img)

    //    var mBump = itemView.findViewById<TextView>(R.id.bump)
//    var blinkView = itemView.findViewById<View>(R.id.blink_view)
    var mainLayout = itemView.findViewById<RelativeLayout>(R.id.main_layout)


    fun bindData(order: Order) {
      if (order.comments!!.isEmpty()) {
        mComment.visibility = View.GONE
      } else {
        mComment.text = order.comments
      }
      if (order.pos_table_name!!.isEmpty()) {
        tableNumber.visibility = View.GONE
      } else {
//        tableNumber.text = "T-${order.pos_table_name}"
        tableNumber.text = ""
      }
      if (order.order_location.equals("pos")) {
        if (!order.pos_table_name.isNullOrEmpty() && !order.pos_table_name.equals("Quick Serve")) {
          table_name.text = order.pos_table_name
        } else {
          if (!order.delivery_option.isNullOrEmpty()) {
            table_name.text = order.delivery_option
          } else {
            table_name.text = "Quick Serve"
          }
        }
      } else {
        table_name.text = order.delivery_option!!.replace("_", " ")
      }

      if (order.order_from != null) {
        table_name.text = table_name.text.toString() + " " + order.order_from
      }
//      else{
//        table_name.text = table_name.text.toString() + " (Self)"
//      }
      if (order.sequence_order_id.equals("0")) {
        order_id.text =
          "#" + order.order_id!!.substring(order.order_id!!.length - 5, order.order_id!!.length)
      } else {
        order_id.text = "#" + order.sequence_order_id
      }
//        "#" + order.order_id!!.substring(order.order_id!!.length - 4, order.order_id!!.length)

      if (order.delivery_firstname.isNullOrEmpty() && order.delivery_lastname.isNullOrEmpty()) {
        customer_name.text = "Guest"
      } else {
        customer_name.text = order.delivery_firstname + " " + order.delivery_lastname
      }

//      if (order.order_time?.length!! > 5) {
//        preparing_time.text = order.order_time?.removeRange(5, 8) ?: ""
//      }

      val millis = stringToMillis(order.order_date!!, order.order_time!!)
      val timeAgo = getTimeAgo(millis)
      preparing_time.text = timeAgo

      product_recyclerView!!.layoutManager = LinearLayoutManager(
        context,
        LinearLayoutManager.VERTICAL,
        false
      )
      product_recyclerView.apply {
        isNestedScrollingEnabled = false
        setHasFixedSize(false)
      }
      product_recyclerView!!.setItemAnimator(DefaultItemAnimator())
//      var productsFilter: ArrayList<Product>? = ArrayList<Product>()
//      var listProducts = Utility().convertJsonToList(context)
//      for(prod in order.products!!){
//        listProducts.forEach { (key, value) ->
//          println("Key: $key, Values: $value")
//          for (mV in value){
//            if(prod.product_id.equals(mV)){
//              productsFilter!!.add(prod)
//            }
//          }
//        }
//      }
      if (order.products!!.isEmpty()) {
        mainLayout.visibility = View.GONE
      }
      var productAdapter = ProductAdpter(order.products!!, context!!, this)
      productAdapter.submitList(order.products!!)
      product_recyclerView!!.adapter = productAdapter
      productAdapter!!.notifyDataSetChanged()
    }

    override fun updateProductStatus(product: com.zotto.kds.database.table.Product, position: Int) {
      orderOnClickListner.updateProduct(product)
    }

    override fun cancelProduct(product: com.zotto.kds.database.table.Product, position: Int) {
      orderOnClickListner.cancelProduct(product)
    }

    override fun updateProductTicket(product: com.zotto.kds.database.table.Product, position: Int) {
      orderOnClickListner.updateProductTicket(product)
    }

//    override fun updateProduct(product: Product) {
//      orderOnClickListner.updateProduct(product)
//    }

    fun stringToMillis(dateStr: String, timeStr: String): Long {
      val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
      inputFormat.timeZone = TimeZone.getTimeZone("UTC")

      val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val date = inputFormat.parse(dateStr)

      var formatDate = outputFormat.format(date!!)
      val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
      var dateTime = "$formatDate $timeStr"
      return format.parse(dateTime)?.time ?: 0L
    }

    fun getTimeAgo(timeMillis: Long): String {
      val now = System.currentTimeMillis()
      if (timeMillis > now || timeMillis <= 0) {
        return "just now"
      }

      val diff = now - timeMillis

      val second = 1000L
      val minute = 60 * second
      val hour = 60 * minute
      val day = 24 * hour

      return when {
        diff < minute -> "just now"
        diff < 2 * minute -> "one min ago"
        diff < 60 * minute -> "${diff / minute} min ago"
        diff < 2 * hour -> "1 hr ago"
        diff < 24 * hour -> "${diff / hour} hr ago"
        diff < 2 * day -> "yesterday"
        else -> "${diff / day} days ago"
      }
    }

  }


  class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
      return oldItem.order_id == newItem.order_id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
      return oldItem == newItem
    }

  }

  //  @RequiresApi(Build.VERSION_CODES.O)
  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    var order = getItem(position)
    if (order != null) {
      holder.bindData(order)
    }
    val r = Random()
    val red: Int = r.nextInt(255 - 0 + 1) + 0
    val green: Int = r.nextInt(255 - 0 + 1) + 0
    val blue: Int = r.nextInt(255 - 0 + 1) + 0
    val draw = holder.order_header.getBackground() as GradientDrawable
    draw.setColor(Color.rgb(red, green, blue))
    holder.order_header.setBackground(draw)
//    holder.blinkView.visibility = View.VISIBLE
    holder.order_header.setOnClickListener {
      order!!.order_status = "Ready"
//      var homeFragment = HomeFragment()
//      homeFragment.startSpeech("Hi there! order " + order.sequence_order_id + "is ready now.", "")
      orderOnClickListner.updateOrder(position, order)
    }

    holder.mPrintOrder.setOnClickListener {
      val hprtPrinterPrinting = HPRTPrinterPrinting(context)
      if (order != null) {
        hprtPrinterPrinting.kitchenReciept(
          order.products!!, order.order_id!!, context
        )
//          PrintingOther(context).connectReceiptUsb()
      }
    }

//    holder.mBump.setOnClickListener{
//      Log.e("bump", "bump$order")
//      var parentJson = JSONObject()
//      var childJson = JSONObject()
//      var notificationArr = JSONArray()
//      parentJson.put("restaurant_id",order!!.restaurant_id)
//      parentJson.put("order_id",order.order_id)
//      for ((index, value) in order.products!!.withIndex()) {
//        childJson.put("id","1")
//        childJson.put("productData", listOf(order.products!![index].product_id))
//        notificationArr.put(childJson)
//      }
//      parentJson.put("notificationData",notificationArr)
//      Log.e("tag",""+parentJson)
//      sendKdsToKdsRes(parentJson)
//    }

//    holder.order_header.setOnClickListener{
//    if(compareTimes(order!!.delivery_time)) {
//      val animation = AnimationUtils.loadAnimation(context, R.anim.blink)
//      holder.blinkView.startAnimation(animation)
//    }
//    else{
//      holder.blinkView.visibility = View.INVISIBLE
//    }
//    }

  }

  private fun sendKdsToKdsRes(jsonObject: JSONObject) {
    var apiServices: ApiServices? = RetroClient.getApiService()
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices!!.sendKDStoKDS(
        SessionManager.getToken(context),
        jsonObject.toString()
      )
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(
          { response -> onResponse(response) },
          { t -> onFailure(t) })
    )
  }

  private fun onResponse(response: String) {
    try {
      Log.e("response", response)
      val jsonObj = JSONObject(response)
      val resultBool = jsonObj.getBoolean("result")
      if (resultBool) {
        Toast.makeText(context, "Order send to other KDS", Toast.LENGTH_LONG).show()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun onFailure(t: Throwable) {
    t.printStackTrace()
  }

  interface OrderOnClickListner {
    fun updateOrder(position: Int, order: Order)
    fun updateProduct(product: com.zotto.kds.database.table.Product)
    fun updateProductTicket(product: com.zotto.kds.database.table.Product)
    fun cancelProduct(product: com.zotto.kds.database.table.Product)
  }

}