package com.zotto.kds.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.database.m_table.MProduct
import com.zotto.kds.database.table.Product
import com.zotto.kds.localIP.SendTask
import com.zotto.kds.model.Data
import com.zotto.kds.model.DataX
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.utils.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import org.w3c.dom.Text

class ProdByCatAdapter (private val mList: List<DataX>, private val context: Context,private val port: Int, private val ip: String) : RecyclerView.Adapter<ProdByCatAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.prod_by_cat_row, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.productName.text = itemsViewModel.pname
        if(itemsViewModel.status == 0){
            holder.enableTxt.text = "Unavailable"
        }
        else{
            holder.enableTxt.text = "Available"
        }

        holder.enableBtn.setOnClickListener {
            var apiServices: ApiServices? = RetroClient.getApiService()
            var availableKey = "";
            if(holder.enableTxt.text == "Available"){
                holder.enableTxt.text = "Unavailable"
                availableKey = "0"
            }
            else{
                holder.enableTxt.text = "Available"
                availableKey = "1"
            }
            val compositeDisposable = CompositeDisposable()
            val rootObject = JSONObject()
            val messageJson = JSONObject()
            messageJson.put("product_id",itemsViewModel.product_id)
            messageJson.put("restaurant_Id",SessionManager.getRestaurantId(context))
            messageJson.put("product_status",availableKey)
            Log.e("myport",port.toString())
            Log.e("myip",ip)
            if(ip.isNotEmpty()){
                Log.e("myport inside if",port.toString())
                Log.e("myip inside if",ip)
                rootObject.put("type","OnlineProductStockUpdate")
                rootObject.put("message",messageJson)
                var orderPort = port
                var orderIp = ip
                val sendTask = SendTask(context, rootObject.toString(), orderIp, orderPort)
                sendTask.execute()
            }

            else{
                if (apiServices != null) {
                    compositeDisposable.add(
                        apiServices.updateItem(
                            SessionManager.getToken(context),
                            messageJson.toString()
                        ).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                            .subscribe({ response -> (response)
                                getRes(response)
                            },
                                { t -> (t) })
                    )
                }
            }
        }

    }
    private fun getRes(str: String){
        Log.e("response",str)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        var enableBtn: RelativeLayout = itemView.findViewById(R.id.enable_btn)
        var enableTxt: TextView = itemView.findViewById(R.id.enable_txt)
    }
}