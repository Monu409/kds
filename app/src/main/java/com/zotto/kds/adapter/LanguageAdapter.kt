package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.zotto.kds.R
import com.zotto.kds.database.table.Order
import com.zotto.kds.model.Language

class LanguageAdapter(val languageList: List<Language>, var context: Context,var languageOnClickListner: LanguageOnClickListner) :
    ListAdapter<Language, LanguageAdapter.MyViewHolder>(DiffUtil()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.language_row, parent, false)
        return MyViewHolder(view,context)
    }

    class MyViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        var language_name= itemView.findViewById<TextView>(R.id.language_name)
        var language_image= itemView.findViewById<ShapeableImageView>(R.id.language_image)
        var select_language=itemView.findViewById<RelativeLayout>(R.id.select_language)

        fun bindData(language: Language){
            Log.e("languagename =",language.languagename+"--")
            language_name.text=language.languagename
            language_image.setImageDrawable(language.countryimage)
        }
    }

    class DiffUtil:androidx.recyclerview.widget.DiffUtil.ItemCallback<Language>(){
        override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem.languagename == newItem.languagename
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var language=languageList.get(position)
        holder.bindData(language)
        holder.select_language.setOnClickListener {
            languageOnClickListner.selectLangauage(language)
        }
    }
    interface LanguageOnClickListner{
        fun selectLangauage(language: Language)
    }
}