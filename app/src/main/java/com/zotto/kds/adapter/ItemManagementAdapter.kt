package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.database.dao.DisabledCategoryDao
import com.zotto.kds.database.table.CategoryTable
import com.zotto.kds.database.table.DisabledTable
import com.zotto.kds.ui.main.MainActivity

class ItemManagementAdapter(
    val restId: String,
    val context: Context,
    val disabledProductDao: DisabledCategoryDao?
) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffUtil()) {
    val VIEW_TYPE_ITEM = 1
    val VIEW_TYPE_HEADER = 0

    class DiffUtil : ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is CategoryTable && newItem is CategoryTable)
                return oldItem.category_id == newItem.category_id
            else return oldItem.equals(newItem)
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View?
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_management_row, parent, false)
            return MyViewHolder(view)
        } else if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_management_header, parent, false)
            return HeaderViewHolder(view)
        }
        throw RuntimeException("There is no type that matches the type " + viewType + ". Make sure you are using view types correctly!");

    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is String)
            return VIEW_TYPE_HEADER
        else
            return VIEW_TYPE_ITEM

    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header = itemView.findViewById<TextView>(R.id.header)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var product_name = itemView.findViewById<TextView>(R.id.product_name)
        var quantity = itemView.findViewById<TextView>(R.id.quantity)
        var btn_parent = itemView.findViewById<LinearLayout>(R.id.btn_parent)
        var enable_btn = itemView.findViewById<RelativeLayout>(R.id.enable_btn)
        var disable_btn = itemView.findViewById<RelativeLayout>(R.id.disable_btn)
        var enable_txt = itemView.findViewById<TextView>(R.id.enable_txt)
        var disable_txt = itemView.findViewById<TextView>(R.id.disable_txt)
        var modifiersname = itemView.findViewById<TextView>(R.id.modifiers)
        var topping_name = itemView.findViewById<TextView>(R.id.topping_name)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = getItem(position)
        if (holder is HeaderViewHolder) {
            Log.e("categoryName=", item.toString())
            holder.header.text = item.toString()
        } else if (holder is MyViewHolder) {
            var cat_item: CategoryTable = item as CategoryTable
//            var product: Product = item as Product
            holder.product_name.text = cat_item.cname

            setUpButton(holder, cat_item)
            holder.btn_parent.setOnClickListener {
                MainActivity.refreshFragment = true
                if (disabledProductDao!!.isCategoryIsExist(restId, cat_item.category_id!!)) {
                    disabledProductDao.deleteDisabledCategory(restId, cat_item.category_id!!)
                } else {
                    var disPr = DisabledTable()
                    disPr.restaurant_id = restId
                    disPr.category_id = cat_item.category_id
                    disPr.status = 0
                    disPr.type = cat_item.type
                    disabledProductDao.insertDisabledCategory(disPr)
                }
                notifyItemChanged(position)
            }

        }
    }

    private fun setUpButton(holder: MyViewHolder, product: CategoryTable) {
        if (disabledProductDao!!.isCategoryIsExist(restId, product.category_id!!)) {
            holder.disable_btn.background = ContextCompat.getDrawable(
                context,
                R.drawable.rectangular_right_roundshape_red
            )
            holder.enable_btn.background = null
            holder.disable_txt.setTextColor(Color.parseColor("#FFFFFF"))
            holder.enable_txt.setTextColor(Color.parseColor("#666171"))

        } else {
            holder.enable_btn.background = ContextCompat.getDrawable(
                context,
                R.drawable.rectangular_left_roundshape_green
            )
            holder.disable_btn.background = null

            holder.enable_txt.setTextColor(Color.parseColor("#FFFFFF"))
            holder.disable_txt.setTextColor(Color.parseColor("#666171"))
        }

    }

}