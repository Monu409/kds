package com.zotto.kds.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.zotto.kds.R
import com.zotto.kds.database.table.RouteTable

class RouteTableAdapter(context: Context, private val routes: List<RouteTable>) :
    ArrayAdapter<RouteTable>(context, 0, routes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.route_rule_layout, parent, false)
        val route = getItem(position)
        val textView = view.findViewById<TextView>(R.id.route_name)
        textView.text = route?.rule_name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val route = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = route?.rule_name
        return view
    }
}
