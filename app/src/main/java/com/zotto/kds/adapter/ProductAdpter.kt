package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.database.table.Product
import com.zotto.kds.utils.Singleton
import java.lang.Exception


class ProductAdpter() :
    ListAdapter<Product,ProductAdpter.MyViewHolder>(ProductDiffUtil()) {
    var productList: List<Product>?=null
    var context: Context?=null
    var productOnClickListner: ProductOnClickListner?=null
    constructor( productList: List<Product>,  context: Context, productOnClickListner: ProductOnClickListner) : this() {
        this.productList=productList
        this.context=context
        this.productOnClickListner=productOnClickListner
    }
//    constructor( productList: List<Product>,  context: Context,) : this() {
//        this.productList=productList
//        this.context=context
//        this.productOnClickListner=productOnClickListner
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_row, parent, false)
        return MyViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var product=productList!!.get(position)
        holder.bindProductData(product)
        if (product.status.equals("Ready")){
            holder.quantity.setTextColor(context!!.resources.getColor(R.color.colorChatelle))
            holder.product_name.setTextColor(context!!.resources.getColor(R.color.colorChatelle))
            holder.modifiersname.setTextColor(context!!.resources.getColor(R.color.colorChatelle))
            holder.topping_name.setTextColor(context!!.resources.getColor(R.color.colorChatelle))
            holder.quantity.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.straight_line))
            holder.product_name.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.straight_line))
        }else{
            holder.quantity.setTextColor(context!!.resources.getColor(R.color.black))
            holder.product_name.setTextColor(context!!.resources.getColor(R.color.black))
        }
        holder.product_layout.setOnClickListener {
            product.status="Ready"
            productOnClickListner!!.updateProductStatus(product,position)
            productOnClickListner!!.updateProductTicket(product,position)
            notifyItemChanged(position)
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var quantity= itemView.findViewById<TextView>(R.id.quantity)
        var product_name= itemView.findViewById<TextView>(R.id.product_name)
        var product_layout=itemView.findViewById<ConstraintLayout>(R.id.product_layout)
        var modifiersname=itemView.findViewById<TextView>(R.id.modifiers)
        var topping_name=itemView.findViewById<TextView>(R.id.topping_name)


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

    class ProductDiffUtil:DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    interface ProductOnClickListner{
        fun updateProductStatus(product: Product,position:Int)
        fun cancelProduct(product: Product,position:Int)
        fun updateProductTicket(product: Product,position:Int)
    }

}