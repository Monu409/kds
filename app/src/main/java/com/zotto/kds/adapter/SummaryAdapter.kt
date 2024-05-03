package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.model.Summary

class SummaryAdapter(var productList: List<Any>, val context: Context) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  val VIEW_TYPE_ITEM = 1
  val VIEW_TYPE_HEADER = 0
  var fmname = ""
  var omname = ""
  var toppingname = ""
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    var view: View? = null
    if (viewType == VIEW_TYPE_ITEM) {
      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_row, parent, false)
      return MyViewHolder(view)
    } else if (viewType == VIEW_TYPE_HEADER) {
      view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_header, parent, false)
      return HeaderViewHolder(view)
    }
    throw RuntimeException("There is no type that matches the type " + viewType + ". Make sure you are using view types correctly!");
  }

  override fun getItemCount(): Int {
    return productList.size
  }

  override fun getItemViewType(position: Int): Int {
    if (productList.get(position) is String) return VIEW_TYPE_HEADER
    else return VIEW_TYPE_ITEM

  }

  override fun onBindViewHolder(
    holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int
  ) {

    if (holder is HeaderViewHolder) {
      holder.header.text = productList.get(position).toString()
    } else if (holder is MyViewHolder) {
      var product = productList.get(position) as Summary// yha pe crash hota hai kabhi kabhi
      holder.product_name.text = product.name
      holder.quantity.text = product.quantity.toString()
//      Log.e("quantity=", product.quantity!!.toString())
      fmname = ""
//      omname = ""
//      toppingname = ""
      try {
        Log.e(
          "fm=",
          product.toString() + "--" + product.fmname!! + "-om-" + product.omname!! + "-toppingname-" + product.toppingname!!
        )
        if (!product.fmname.isNullOrEmpty()) {
          fmname += product.fmname
        }
        if (!product.detourname.isNullOrEmpty()) {
          if (fmname.isEmpty()) fmname += product.detourname
          else fmname += "\n" + product.detourname
        }
        if (!product.omname.isNullOrEmpty()) {
          if (fmname.isEmpty()) fmname += product.omname
          else fmname += "\n" + product.omname
        }
        if (fmname.isEmpty()) {
          holder.modifiersname.visibility = View.GONE
        } else {
          holder.modifiersname.visibility = View.VISIBLE
          holder.modifiersname.text = fmname
        }
        if (!product.toppingname.isNullOrEmpty()) {
          holder.topping_name.visibility = View.VISIBLE
          holder.topping_name.text = "Toppings: ${product.toppingname}"
        } else {
          holder.topping_name.visibility = View.GONE
        }

      } catch (e: Exception) {
        e.printStackTrace()
      } catch (e: NullPointerException) {
        e.printStackTrace()
      }
    }
  }

  class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var header = itemView.findViewById<TextView>(R.id.header)
  }

  class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var product_name = itemView.findViewById<TextView>(R.id.product_name)
    var quantity = itemView.findViewById<TextView>(R.id.quantity)
    var modifiersname = itemView.findViewById<TextView>(R.id.modifiers)
    var topping_name = itemView.findViewById<TextView>(R.id.topping_name)
  }
}