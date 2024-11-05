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


        fun bindProductData(product: Product){
            var fmname=""
            var detourname=""
            var omname=""
            var toppingname=""
            quantity.text=product.quantity.toString()
            product_name!!.text=product.name!!

            try {
                if(product.fmname != null && !product.fmname.isNullOrEmpty() && product.fmname!!.length>1){
                    fmname = product.fmname!!
                    Log.e("ProductFmName",product.fmname!!)

                }
                if(product.omname != null && !product.omname.isNullOrEmpty() && product.omname!!.length>1){
                    omname = product.omname!!
                    Log.e("ProductOmName",product.omname!!)

                }
                if(product.detourname != null && !product.detourname.isNullOrEmpty() && product.detourname!!.length>1){
                    detourname = product.detourname!!
                    Log.e("ProductFDetourName",product.detourname!!)
                }
                Log.e("fm=",product.toString()+"--"+product.fm!!+"-om-"+product.om!!)
                if (product.fm!! != null)
                    for (modifier in product.fm!!){
                        if (fmname.isNullOrEmpty()){
                            fmname=modifier.fm_item_name!!
                        }else{
                            fmname=checkBlank(fmname,modifier.fm_item_name!!)+modifier.fm_item_name!!
                        }
                    }
                if (product.detourom!! != null)
                    for (modifier in product.detourom!!){
                        if (detourname.isNullOrEmpty()){
                            detourname=modifier.om_item_name!!
                        }else{
                            detourname=checkBlank(detourname,modifier.om_item_name!!)+modifier.om_item_name!!
                        }
                    }
                if (product.om!! != null)
                    for (modifier in product.om!!){
                        if (omname.isNullOrEmpty()){
                            omname=modifier.om_item_name!!
                        }else{
                            omname=checkBlank(omname,modifier.om_item_name!!)+modifier.om_item_name!!
                        }
                    }

                if (!fmname.isNullOrEmpty() && !omname.isNullOrEmpty() && !detourname.isNullOrEmpty()){
                    modifiersname.visibility=View.VISIBLE
                    modifiersname.text=checkBlank(fmname,omname)+checkBlank(omname,detourname)+detourname

                }else{
                    if (fmname.isNullOrEmpty() && omname.isNullOrEmpty() && detourname.isNullOrEmpty()){
                        modifiersname.visibility=View.GONE
                    }else{
                        modifiersname.visibility=View.VISIBLE
                        if (!omname.isNullOrEmpty() && !detourname.isNullOrEmpty()){
                            modifiersname.text=checkBlank(fmname,omname)+checkBlank(omname,detourname)+detourname
                        }else if (!detourname.isNullOrEmpty()){
                            modifiersname.text=checkBlank(fmname,detourname)+detourname
                        }else if (!omname.isNullOrEmpty()){
                            modifiersname.text=checkBlank(fmname,omname)+omname
                        }else{
                            modifiersname.text=checkBlank(fmname,omname)+checkBlank(omname,detourname)+detourname
                        }

                    }

                }
                for (topping in product.topping!!){
                    if (toppingname.isNullOrEmpty()){
                        toppingname=topping.name!!
                    }else{
                        toppingname=checkBlank(toppingname,topping.name!!)+topping.name!!
                    }
                }
                if (!toppingname.isNullOrEmpty()){
                    topping_name.visibility=View.VISIBLE
                    topping_name.text=toppingname
                }else{
                    topping_name.visibility=View.GONE
                    topping_name.text=toppingname
                }

            }catch (e:Exception){
                e.printStackTrace()
            }catch (e:NullPointerException){
                e.printStackTrace()
            }



        }

        private fun checkBlank(firstName: String, secondName: String): String{
            return when {
                firstName.isEmpty() -> secondName
                secondName.isEmpty() -> firstName
                else -> "$firstName\n$secondName"
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