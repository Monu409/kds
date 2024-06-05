package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.*
import com.zotto.kds.R
import com.zotto.kds.database.table.Order
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


class OrderAdapter(var context: Context, var orderOnClickListner: OrderOnClickListner) :
  PagedListAdapter<Order, OrderAdapter.MyViewHolder>(DiffUtil()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.orders_row, parent, false)
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
    var blinkView = itemView.findViewById<View>(R.id.blink_view)



    fun bindData(order: Order) {
      if(order.comments!!.isEmpty()){
        mComment.visibility = View.GONE
      }
      else{
        mComment.text = order.comments
      }
      if(order.pos_table_name!!.isEmpty()){
        tableNumber.visibility = View.GONE
      }
      else{
        tableNumber.text = "T-${order.pos_table_name}"
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
        table_name.text = order.delivery_option!!.replace("_"," ")
      }

      if (order.order_from != null) {
        table_name.text = table_name.text.toString() + " " + order.order_from
      }
//      else{
//        table_name.text = table_name.text.toString() + " (Self)"
//      }

      order_id.text =
        "#" + order.order_id!!.substring(order.order_id!!.length - 4, order.order_id!!.length)
      if (order.delivery_firstname.isNullOrEmpty() && order.delivery_lastname.isNullOrEmpty()) {
        customer_name.text = "Guest"
      } else {
        customer_name.text = order.delivery_firstname + " " + order.delivery_lastname
      }

      if(order.order_time?.length!! > 5){
        preparing_time.text = order.order_time?.removeRange(5,8) ?: ""
      }
      product_recyclerView!!.layoutManager = LinearLayoutManager(
        context,
        LinearLayoutManager.VERTICAL,
        false
      )
      product_recyclerView!!.setItemAnimator(DefaultItemAnimator())
      var productAdapter = ProductAdpter(order.products!!, context!!,this)
      productAdapter.submitList(order.products!!)
      product_recyclerView!!!!.adapter = productAdapter
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

  @RequiresApi(Build.VERSION_CODES.O)
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
    holder.blinkView.visibility = View.VISIBLE
    holder.blinkView.setOnClickListener {
      order!!.order_status = "Ready"
      orderOnClickListner.updateOrder(position, order)
//      order.delivery_time?.let { it1 -> compareTimes(it1) }
    }

//    holder.order_header.setOnClickListener{
    if(compareTimes(order!!.delivery_time)) {
      val animation = AnimationUtils.loadAnimation(context, R.anim.blink)
      holder.blinkView.startAnimation(animation)
    }
//    }

  }

  interface OrderOnClickListner {
    fun updateOrder(position: Int, order: Order)
    fun updateProduct(product: com.zotto.kds.database.table.Product)
    fun updateProductTicket(product: com.zotto.kds.database.table.Product)
    fun cancelProduct(product: com.zotto.kds.database.table.Product)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun compareTimes(deliveryTime: String?):Boolean{
    var shouldBlink = false
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    try {
      val time: LocalTime = LocalTime.parse(deliveryTime, formatter)
      println("Parsed time: $time")
      val sdf = SimpleDateFormat("HH:mm")
      val currentDate = sdf.format(Date())

      val d: Duration = Duration.between(
        time,
        LocalTime.parse(currentDate, formatter)
      )
      Log.e("xcvb",d.toMinutes().toString())
      if(d.toMinutes()>=10){
        shouldBlink = true
      }
    } catch (e: DateTimeParseException) {
      println("Error parsing time: $e")
    }
    return shouldBlink;
  }

}