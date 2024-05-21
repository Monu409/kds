package com.zotto.kds.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.zotto.kds.R
import com.zotto.kds.database.table.Product
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class ProductAdpter() :
    ListAdapter<Product, ProductAdpter.MyViewHolder>(ProductDiffUtil()) {
    var productList: List<Product>? = null
    var context: Context? = null
    var parentView: MaterialCardView? = null
    var orderTime: Date? = null
    var productOnClickListner: ProductOnClickListner? = null

    var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    var timeFormat: DateFormat = SimpleDateFormat("hh:mm:ss")

    constructor(
        productList: List<Product>,
        context: Context,
        productOnClickListner: ProductOnClickListner,
        parentView: MaterialCardView,
        orderTime: String?
    ) : this() {
        this.productList = productList
        this.context = context
        this.productOnClickListner = productOnClickListner
        this.parentView = parentView
        if (!orderTime.isNullOrEmpty()) {
            this.orderTime = timeFormat.parse(orderTime)
        }
    }

    constructor(productList: List<Product>, context: Context) : this() {
        this.productList = productList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var product = productList!!.get(position)
        holder.bindProductData(product)
        if (product.status.equals("Ready")) {
            holder.quantity.setTextColor(ContextCompat.getColor(context!!, R.color.colorChatelle))
            holder.product_name.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorChatelle
                )
            )
            holder.modifiersname.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorChatelle
                )
            )
            holder.topping_name.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorChatelle
                )
            )
            holder.quantity.setBackgroundResource(R.drawable.straight_line)
            holder.product_name.setBackgroundResource(R.drawable.straight_line)
        } else {
            holder.quantity.setTextColor(ContextCompat.getColor(context!!, R.color.black))
            holder.product_name.setTextColor(ContextCompat.getColor(context!!, R.color.black))
            holder.quantity.background = null
            holder.product_name.background = null
        }

        var animator: ObjectAnimator? = null
        if (!product.status.equals(
                "Ready",
                true
            ) && !product.preparation_time.isNullOrEmpty() && !product.preparation_time!!.contains("0000-00-00")
        ) {
            var preparation_time: Date = dateFormat.parse(product.preparation_time!!)!!
            preparation_time = timeFormat.parse(timeFormat.format(preparation_time))!!
            Log.e("preparation_time == ", "$preparation_time")
            Log.e("order_time == ", "$orderTime")
            if (preparation_time.after(orderTime)) {
                if (parentView != null) {
                    animator = blinkView(parentView!!)
                }
            } else {
                var delay: Long = orderTime!!.time - preparation_time.time
                Log.e("delay == ", "$delay")
                val timer = Timer()
                val task: TimerTask = object : TimerTask() {
                    override fun run() {
                        (context as Activity).runOnUiThread(Runnable {
                            animator = blinkView(parentView!!)
                        })
                    }
                }
                timer.schedule(task, delay)
            }
        }

//    holder.product_layout.setOnClickListener {
//      if (productOnClickListner != null && !product.status.equals("Ready")) {
//        product.status = "Ready"
//        placeNextToPrepare()
//        productOnClickListner!!.updateProductStatus(product, position)
//        productOnClickListner!!.updateProductTicket(product, position)
//        notifyDataSetChanged()
//      }
//    }

        var doubleTap = false
        holder.product_layout.setOnClickListener {
            if (doubleTap) {
                if (productOnClickListner != null && !product.status.equals("Ready")) {
                    product.status = "Ready"
                    sortProducts()
//          placeNextToPrepare()
                    productOnClickListner!!.updateProductStatus(product, position)
                    productOnClickListner!!.updateProductTicket(product, position)
                    notifyDataSetChanged()
                }
            }
            doubleTap = true
            Handler(Looper.getMainLooper()).postDelayed({
                doubleTap = false
            }, 800)
        }

    }

    fun blinkView(view: MaterialCardView): ObjectAnimator {
        var abg: ObjectAnimator =
            ObjectAnimator.ofArgb(view, "strokeColor", Color.parseColor("#fc3700"))
                .apply {
                    duration = 700
                    addUpdateListener {
                        view.invalidate()
                    }
                    doOnEnd {
//            reverse = !reverse
                    }
                    start()
                }
        abg.repeatCount = ObjectAnimator.INFINITE
        abg.repeatMode = ObjectAnimator.REVERSE
        return abg
    }

    fun sortProducts() {
        Collections.sort(productList, Comparator { obj1, obj2 ->
            obj1.status!!.compareTo(obj2.status!!)
        })
    }

//  fun placeNextToPrepare() {
//    // Move first to last
//    Collections.swap(productList, 0, productList!!.size - 1)
//    // find next to prepare
//    val product: Product? = productList!!.find { !it.status.equals("Ready") }
//    if (product != null) {
//      var fromIndex = productList!!.indexOf(product)
//      Collections.swap(productList, fromIndex, 0)
//    }
//  }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var quantity = itemView.findViewById<TextView>(R.id.quantity)
        var product_name = itemView.findViewById<TextView>(R.id.product_name)
        var product_layout = itemView.findViewById<ConstraintLayout>(R.id.product_layout)
        var modifiersname = itemView.findViewById<TextView>(R.id.modifiers)
        var topping_name = itemView.findViewById<TextView>(R.id.topping_name)

        fun bindProductData(product: Product) {
            quantity.text = product.quantity.toString()
            product_name!!.text = product.name!!
            try {
//        Log.e("product json=", Gson().toJson(product).toString())
//        Log.e("fm=", "" + product.fm!! + "-om-" + product.om!!)

                var allModifiers = ""
                if (product.fm != null)
                    for (modifier in product.fm!!) {
                        if (!modifier.fm_item_name.isNullOrEmpty()) {
                            if (!modifier.fm_cat_name.isNullOrEmpty()) {
                                if (allModifiers.contains(modifier.fm_cat_name!! + ": ")) {
                                    allModifiers = allModifiers.replace(
                                        modifier.fm_cat_name!! + ":",
                                        "${modifier.fm_cat_name!!}: ${modifier.fm_item_name!!},"
                                    )
                                } else {
                                    if (allModifiers.isNotEmpty())
                                        allModifiers += "\n"
                                    allModifiers += modifier.fm_cat_name!! + ": " + modifier.fm_item_name!!
                                }
                            } else {
                                if (allModifiers.isNotEmpty())
                                    allModifiers += "\n"
                                allModifiers += modifier.fm_item_name!!
                            }
                        }
                    }
                if (product.detourom != null)
                    for (modifier in product.detourom!!) {
                        if (!modifier.om_item_name.isNullOrEmpty()) {
                            if (allModifiers.isNotEmpty())
                                allModifiers += "\n"
                            allModifiers += modifier.om_item_name!!
                        }
                    }

                if (product.om != null)
                    for (modifier in product.om!!) {
                        if (!modifier.om_item_name.isNullOrEmpty()) {
                            if (!modifier.om_cat_name.isNullOrEmpty()) {
                                if (allModifiers.contains(modifier.om_cat_name!! + ":")) {
                                    allModifiers = allModifiers.replace(
                                        modifier.om_cat_name!! + ":",
                                        "${modifier.om_cat_name!!}: ${modifier.om_item_name!!},"
                                    )
                                } else {
                                    if (allModifiers.isNotEmpty())
                                        allModifiers += "\n"
                                    allModifiers += modifier.om_cat_name!! + ": " + modifier.om_item_name!!
                                }
                            } else {
                                if (allModifiers.isNotEmpty())
                                    allModifiers += "\n"
                                allModifiers += modifier.om_item_name!!
                            }
                        }
                    }


                if (allModifiers.isNotEmpty()) {
                    modifiersname.visibility = View.VISIBLE
                    modifiersname.text = allModifiers
                } else {
                    modifiersname.visibility = View.GONE
                }
                var allTopping = ""
                for (topping in product.topping!!) {
                    if (!topping.name.isNullOrEmpty()) {
                        if (allTopping.isNotEmpty())
                            allTopping += ", "
                        allTopping += topping.name!!
                    }
                }
                if (allTopping.isNotEmpty()) {
                    topping_name.visibility = View.VISIBLE
                    topping_name.text = "Toppings: $allTopping"
                } else {
                    topping_name.visibility = View.GONE
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }

    }

    class ProductDiffUtil : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    interface ProductOnClickListner {
        fun updateProductStatus(product: Product, position: Int)
        fun cancelProduct(product: Product, position: Int)
        fun updateProductTicket(product: Product, position: Int)
    }

}