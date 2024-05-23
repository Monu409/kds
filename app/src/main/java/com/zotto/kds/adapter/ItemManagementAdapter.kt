package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.model.Summary
import java.lang.Exception

class ItemManagementAdapter(var header:String, val productList: List<Summary>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val VIEW_TYPE_ITEM = 1
    val VIEW_TYPE_HEADER = 0
    var fmname=""
    var omname=""
    var toppingname=""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view : View?=null
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_row, parent, false)
            return MyViewHolder(view)
        }else if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_header, parent, false)
            return HeaderViewHolder(view)
        }
        throw  RuntimeException("There is no type that matches the type " + viewType + ". Make sure you are using view types correctly!");
    }

    override fun getItemCount(): Int {
        if (productList != null && productList.size == 0){
            return 1
        }else{
            return productList.size+1
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionHeader(position))
            return VIEW_TYPE_HEADER
        else
            return VIEW_TYPE_ITEM

    }
    fun isPositionHeader(position: Int):Boolean{
        if (position == 0){
            return true
        }else{
            return false
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        if (holder is HeaderViewHolder) {
            Log.e("categoryName=",header!!)
            (holder as HeaderViewHolder).header.text=header!!
        }else if (holder is MyViewHolder){
            var product=productList.get(position-1)
            (holder as MyViewHolder).product_name.text=product.name


        }
    }
    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header= itemView.findViewById<TextView>(R.id.header)
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var product_name= itemView.findViewById<TextView>(R.id.product_name)
        var quantity=itemView.findViewById<TextView>(R.id.quantity)
        var modifiersname=itemView.findViewById<TextView>(R.id.modifiers)
        var topping_name=itemView.findViewById<TextView>(R.id.topping_name)
    }

}