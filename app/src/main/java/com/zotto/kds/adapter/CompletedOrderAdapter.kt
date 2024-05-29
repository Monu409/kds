package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.utils.Singleton
import org.w3c.dom.Text

class CompletedOrderAdapter(val orderList: List<Order>, var context: Context,var completedOrderOnClickListner:CompletedOrderOnClickListner) :
    ListAdapter<Order, CompletedOrderAdapter.MyViewHolder>(DiffUtil()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.completed_order_row, parent, false)
        return MyViewHolder(view,context)
    }
    class MyViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        var table_name= itemView.findViewById<TextView>(R.id.table_name)
        var order_id= itemView.findViewById<TextView>(R.id.order_id)
        var customer_name= itemView.findViewById<TextView>(R.id.customer_name)
        var preparing_time= itemView.findViewById<TextView>(R.id.preparing_time)
        var product_recyclerView=itemView.findViewById<RecyclerView>(R.id.product_recyclerView)
        var recall_order=itemView.findViewById<ImageView>(R.id.recall_order)
        var makeOrderNew=itemView.findViewById<TextView>(R.id.make_order_new)
        fun bindData(order: Order){
            if (order.order_location.equals("pos")){
                if (!order.pos_table_name.isNullOrEmpty()){
                    table_name.text=order.pos_table_name
                }else{
                    if (!order.delivery_option.isNullOrEmpty()){
                        table_name.text=order.delivery_option
                    }else{
                        table_name.text="Quick Serve"
                    }

                }
            }else{
                table_name.text=order.order_location
            }
            order_id.text="#"+order.order_id!!.substring(order.order_id!!.length-4,order.order_id!!.length)
            if (order.delivery_firstname.isNullOrEmpty() || order.delivery_lastname.isNullOrEmpty()){
                customer_name.text="Guest"
            }else{
                customer_name.text=order.delivery_firstname+" "+order.delivery_lastname
            }
            preparing_time.text=order.order_time
            product_recyclerView!!.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            product_recyclerView!!.setItemAnimator(DefaultItemAnimator())
            var productAdapter = ProductAdpter(order.products!!, context)
            productAdapter.submitList(order.products!!)
            product_recyclerView!!.adapter = productAdapter
            productAdapter!!.notifyDataSetChanged()
        }


    }

    class DiffUtil:androidx.recyclerview.widget.DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.order_id == newItem.order_id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var order=orderList.get(position)
        holder.bindData(order)
        holder.recall_order.setOnClickListener {
            order.order_status="Confirm"
            completedOrderOnClickListner.reecallOrder(position,order)
        }

        holder.makeOrderNew.setOnClickListener{
            order.order_status="New"
            //Singleton.ordertype="active"
            completedOrderOnClickListner.reecallOrder(position,order)
        }

    }
    interface CompletedOrderOnClickListner{
        fun reecallOrder(position: Int,order: Order)
    }
}