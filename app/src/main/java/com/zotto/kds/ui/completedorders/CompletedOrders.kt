package com.zotto.kds.ui.completedorders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zotto.kds.R
import com.zotto.kds.adapter.CompletedOrderAdapter
import com.zotto.kds.adapter.OrderAdapter
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.ProductDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.databinding.CompleteOrdersBinding
import com.zotto.kds.databinding.FragmentHomeBinding
import com.zotto.kds.printing.HPRTPrinterPrinting
import com.zotto.kds.repository.CompletedOrdersRepository
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.home.HomeFragment
import com.zotto.kds.ui.home.HomeModelFactory
import com.zotto.kds.ui.home.HomeViewModel
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.ui.main.MainActivity.Companion.completedOrders
import com.zotto.kds.utils.Singleton
import com.zotto.kds.utils.Singleton.Companion.ordertype

class CompletedOrders :AppCompatActivity(),CompletedOrderAdapter.CompletedOrderOnClickListner,OrderAdapter.OrderOnClickListner{
   companion object{
       private var binding: CompleteOrdersBinding? = null
       private  var completedOrderViewModel: CompletedOrderViewModel?=null
       private var apiServices: ApiServices?=null
       private var appDatabase: AppDatabase?=null
       private var orderDao: OrderDao?=null
       private var productDao: ProductDao?=null
       private var order_recycleview: RecyclerView? = null
       private var completedorderAdapter: CompletedOrderAdapter? = null
       private var completedOrdersRepository: CompletedOrdersRepository?=null
       private var orderlist=ArrayList<Order>()
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.complete_orders)
        appDatabase = DatabaseClient.getInstance(this)!!.getAppDatabase()
        orderDao = appDatabase!!.orderDao()
       productDao = appDatabase!!.productDao()
       apiServices = RetroClient.getApiService()
        completedorderAdapter = CompletedOrderAdapter(orderlist,this,this,this)
        completedOrdersRepository = CompletedOrdersRepository(this,
           orderDao!!, productDao!!,
           apiServices!!, completedorderAdapter!!,supportFragmentManager)
       completedOrderViewModel =
            ViewModelProvider(this, CompletedOrderViewModelFactory(completedOrdersRepository!!)).get(CompletedOrderViewModel::class.java)
        binding!!.completedOrderViewModel= completedOrderViewModel
         binding!!.lifecycleOwner=this
       order_recycleview = binding!!.orderRecycleview
//        order_recycleview!!.layoutManager = GridLayoutManager(this,3 )
        order_recycleview!!.layoutManager = StaggeredGridLayoutManager(3 , LinearLayoutManager.VERTICAL)
        order_recycleview!!.layoutDirection = View.LAYOUT_DIRECTION_RTL
        order_recycleview!!.setItemAnimator(DefaultItemAnimator())
        order_recycleview!!.setHasFixedSize(true)
        order_recycleview!!.adapter = completedorderAdapter

        completedOrderViewModel!!.orderlivedata!!.observe(this, Observer{
            try {
                Log.e("completedOrderView==",it.size.toString()+"--"+it!!.get(0)!!.order_status+"-order_id-"+it!!.get(0)!!.order_id+"-ordertype-"+Singleton.ordertype)
                if (Singleton.ordertype.equals("completed")  && it!!.size>0){
                    MainActivity.activeOrders!!.text=resources.getString(R.string.active_txt)
                    if (it != null && it.size>0 && it.get(0)!!.order_status.equals("Ready")){
                        binding!!.completedOrders.text=resources.getString(R.string.finished_order_txt)+"("+it.size.toString()+")"
                    }else{
                        binding!!.completedOrders.text=resources.getString(R.string.finished_order_txt)
                    }

                    order_recycleview!!.apply {
                        order_recycleview!!.layoutManager=null
                        completedorderAdapter = CompletedOrderAdapter(it, this@CompletedOrders,this@CompletedOrders,this@CompletedOrders)
                        order_recycleview!!.layoutManager = GridLayoutManager(context,3 )
                        order_recycleview!!.setItemAnimator(DefaultItemAnimator())
                        order_recycleview!!.setHasFixedSize(true)
                    }
                    if (it != null && it.size>0 && it.get(0)!!.order_status.equals("Ready")){
                        completedorderAdapter!!.submitList(it?.toMutableList())
                    }else{
                        completedorderAdapter!!.submitList(it?.toMutableList())
                    }

                    order_recycleview!!.adapter = completedorderAdapter
                    if (completedorderAdapter != null)
                        completedorderAdapter!!.notifyDataSetChanged()
                }else{
                    if (it != null && it.size>0 && it.get(0)!!.order_status.equals("Ready")){
                        completedorderAdapter!!.submitList(it?.toMutableList())
                    }else{
                        completedorderAdapter!!.submitList(it?.toMutableList())
                    }

                    order_recycleview!!.adapter = completedorderAdapter
                    if (completedorderAdapter != null)
                        completedorderAdapter!!.notifyDataSetChanged()
                }


            }catch (e:Exception){
                e.printStackTrace()
            }catch (e:NullPointerException){
                e.printStackTrace()
            }
        })
        binding!!.backBtn.setOnClickListener {
            Singleton.ordertype="active"
            Singleton.isactiveclicked=true
           // var intent= Intent(this, MainActivity::class.java)
           // startActivity(intent)
            finish()
        }
    }

    override fun reecallOrder(position: Int, order: Order) {
       completedOrdersRepository!!.updateRecallOrder(completedOrdersRepository!!.updateOrderJsonMap(order.order_date!!,order.comments!!,order.order_id!!,
            order.order_status!!,order.order_time!!,order.order_time!!,order.rname?:"",order.order_time!!,order)!!,order.order_id!!)
        if (completedorderAdapter != null){
           completedorderAdapter!!.notifyItemChanged(position)
            completedOrderViewModel!!.orderlivedata!!.observe(this, Observer {
                completedorderAdapter!!.submitList(it)
            })
        }
    }

    override fun updateOrder(position: Int, order: Order) {

    }

    override fun updateProduct(product: Product) {

    }

    override fun updateProductTicket(product: Product) {

    }

    override fun cancelProduct(product: Product) {

    }
}