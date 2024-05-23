package com.zotto.kds.ui.summary

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.adapter.SummaryAdapter
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.ProductDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.databinding.SummaryDialogBinding
import com.zotto.kds.model.Summary
import com.zotto.kds.repository.SummaryRepository
import com.zotto.kds.utils.SessionManager
import java.util.*
import kotlin.collections.ArrayList

class SummaryDailog: Fragment() {
    companion object{
        var binding: SummaryDialogBinding? = null
        var summaryViewModel: SummaryViewModel?=null
        var appDatabase: AppDatabase?=null
        var orderDao: OrderDao?=null
        var productDao: ProductDao?=null
        var summary_layout:LinearLayout?=null
        var summaryAdapter: SummaryAdapter? = null
        var dishList:ArrayList<Summary>?=null
        var drinkList:ArrayList<Summary>?=null
        var pizzaList:ArrayList<Summary>?=null
        var orderList=ArrayList<Order>()
        var productList=ArrayList<Product>()
        var summaryRepository:SummaryRepository?=null
        var dishqty=0
        var isDishes=true
        var isDrink=true
        var isPizza=true
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.summary_dialog,container,false)
        appDatabase= DatabaseClient.getInstance(requireActivity()!!)!!.getAppDatabase()
        orderDao=appDatabase!!.orderDao()
        productDao=appDatabase!!.productDao()
        summary_layout=binding!!.summaryLayout
        summaryRepository= SummaryRepository(requireActivity(),orderDao!!)
        summaryViewModel= ViewModelProvider(this, SummaryViewModelFactory(summaryRepository!!)).get(SummaryViewModel::class.java)
       /// binding!!.summaryviewmodel= summaryViewModel
        binding!!.lifecycleOwner=this
        dishList= ArrayList()
        drinkList=ArrayList()
        pizzaList=ArrayList()
        orderList= ArrayList()
        productList=ArrayList<Product>()
        dishqty=0
        isDishes=true
        isDrink=true
        isPizza=true

        summaryViewModel!!.orderlivedata.observe(viewLifecycleOwner, Observer {
            Log.e("productlist order size=",it.size.toString()+"--")
            if (it.size==0){
                dishList!!.clear()
                drinkList!!.clear()
                pizzaList!!.clear()
                if (summaryAdapter != null){
                    summaryAdapter!!.notifyDataSetChanged()
                }
            }
            orderList.clear()
            if (it.size>0 ){
                orderList.add(it.get(0))
            }
        })

        summaryViewModel!!.productlivedata.observe(viewLifecycleOwner, Observer {
           try {

               Log.e("productlist order size=",it.size.toString()+"--")
               for (product in it){
                   var fmname=""
                   var detourname=""
                   var omname=""
                   var toppingname=""
                   Log.e("summary fmname=",product.fm!!.size.toString()+"-omsize-"+product.om!!.size+"-dishList-"+dishList!!.size)
                   if (product.type.equals("Dishes")){
                       if (dishList!!.size>0){
                           var summary:Summary?=null
                           for (dish in dishList!!){
                               Log.e("summary fmname =",dish.name!!+"-productname-"+product.name)
                               if (dish.name!!.equals(product.name)){
                                   summary=dish
                                   break
                               }
                           }

                           if (summary !=null){
                               summary.quantity=summary.quantity!!+product.quantity!!
                           }else{
                               var summary=Summary()
                               summary.name=product.name
                               summary.quantity=product.quantity
                               if (product.fm!! != null){
                                   for (modifier in product.fm!!){
                                       Log.e("summary fmname=",modifier.fm_item_name!!)
                                       if (fmname.isNullOrEmpty()){
                                           fmname=modifier.fm_item_name!!
                                       }else{
                                           fmname=fmname+"\n"+modifier.fm_item_name!!
                                       }
                                   }
                               }

                               summary.fmname=fmname
                               if (product.detourom!! != null){
                                   for (modifier in product.detourom!!){
                                       if (detourname.isNullOrEmpty()){
                                           detourname=modifier.om_item_name!!
                                       }else{
                                           detourname=detourname+"\n"+modifier.om_item_name!!
                                       }
                                   }
                               }
                               summary.detourname=detourname
                               if (product.om!! != null){
                                   for (modifier in product.om!!){
                                       if (omname.isNullOrEmpty()){
                                           omname=modifier.om_item_name!!
                                       }else{
                                           omname=omname+"\n"+modifier.om_item_name!!
                                       }
                                   }
                               }
                               summary.omname=omname
                               if (product.topping!! != null){
                                   for (topping in product.topping!!){
                                       if (toppingname.isNullOrEmpty()){
                                           toppingname=topping.name!!
                                       }else{
                                           toppingname=toppingname+"\n"+topping.name!!
                                       }
                                   }
                               }
                               summary.toppingname=toppingname
                               Log.e("summary fmname=",fmname.toString()+"-omname-"+omname+"-toppingname-"+toppingname)
                               summary.productType=product.type!!
                               dishList!!.add(summary)
                               fmname=""
                               omname=""
                               toppingname=""
                           }
                       }else{
                           var summary=Summary()
                           summary.name=product.name
                           summary.quantity=product.quantity
                           if (product.fm!! != null){
                               for (modifier in product.fm!!){
                                   Log.e("summary fmname=",modifier.fm_item_name!!)
                                   if (fmname.isNullOrEmpty()){
                                       fmname=modifier.fm_item_name!!
                                   }else{
                                       fmname=fmname+"\n"+modifier.fm_item_name!!
                                   }
                               }
                           }

                           summary.fmname=fmname
                           if (product.detourom!! != null){
                               for (modifier in product.detourom!!){
                                   if (detourname.isNullOrEmpty()){
                                       detourname=modifier.om_item_name!!
                                   }else{
                                       detourname=detourname+"\n"+modifier.om_item_name!!
                                   }
                               }
                           }
                           summary.detourname=detourname
                           if (product.om!! != null){
                               for (modifier in product.om!!){
                                   if (omname.isNullOrEmpty()){
                                       omname=modifier.om_item_name!!
                                   }else{
                                       omname=omname+"\n"+modifier.om_item_name!!
                                   }
                               }
                           }
                           summary.omname=omname
                           if (product.topping!! != null){
                               for (topping in product.topping!!){
                                   if (toppingname.isNullOrEmpty()){
                                       toppingname=topping.name!!
                                   }else{
                                       toppingname=toppingname+"\n"+topping.name!!
                                   }
                               }
                           }
                           summary.toppingname=toppingname
                           Log.e("summary fmname=",fmname.toString()+"-omname-"+omname+"-toppingname-"+toppingname)
                           summary.productType=product.type!!
                           dishList!!.add(summary)
                           fmname=""
                           omname=""
                           detourname=""
                           toppingname=""
                       }
                   }
                   if (product.type.equals("Drinks")){
                       if (drinkList!!.size>0){
                           var summary:Summary?=null
                           for (drink in drinkList!!){
                               if (drink.name!!.equals(product.name)){
                                   summary=drink
                                   break
                               }
                           }
                           Log.e("summary =",summary.toString())
                           if (summary != null){
                               summary.quantity=summary.quantity!!+product.quantity!!
                           }else{
                               var summary=Summary()
                               summary.name=product.name
                               summary.quantity=product.quantity
                               if (product.fm!! != null)
                                   for (modifier in product.fm!!){
                                       if (fmname.isNullOrEmpty()){
                                           fmname=modifier.fm_item_name!!
                                       }else{
                                           fmname=fmname+"\n"+modifier.fm_item_name!!
                                       }
                                   }
                               summary.fmname=fmname
                               if (product.detourom!! != null){
                                   for (modifier in product.detourom!!){
                                       if (detourname.isNullOrEmpty()){
                                           detourname=modifier.om_item_name!!
                                       }else{
                                           detourname=detourname+"\n"+modifier.om_item_name!!
                                       }
                                   }
                               }
                               summary.detourname=detourname
                               if (product.om!! != null)
                                   for (modifier in product.om!!){
                                       if (omname.isNullOrEmpty()){
                                           omname=modifier.om_item_name!!
                                       }else{
                                           omname=omname+"\n"+modifier.om_item_name!!
                                       }
                                   }
                               summary.omname=omname
                               if (product.topping!! != null)
                                   for (topping in product.topping!!){
                                       if (toppingname.isNullOrEmpty()){
                                           toppingname=topping.name!!
                                       }else{
                                           toppingname=toppingname+"\n"+topping.name!!
                                       }
                                   }
                               summary.toppingname=toppingname
                               summary.productType=product.type!!
                               drinkList!!.add(summary)
                               fmname=""
                               omname=""
                               detourname=""
                               toppingname=""
                           }
                       }else{
                           var summary=Summary()
                           summary.name=product.name
                           summary.quantity=product.quantity
                           if (product.fm!! != null)
                               for (modifier in product.fm!!){
                                   if (fmname.isNullOrEmpty()){
                                       fmname=modifier.fm_item_name!!
                                   }else{
                                       fmname=fmname+"\n"+modifier.fm_item_name!!
                                   }
                               }
                           summary.fmname=fmname
                           if (product.detourom!! != null){
                               for (modifier in product.detourom!!){
                                   if (detourname.isNullOrEmpty()){
                                       detourname=modifier.om_item_name!!
                                   }else{
                                       detourname=detourname+"\n"+modifier.om_item_name!!
                                   }
                               }
                           }
                           summary.detourname=detourname
                           if (product.om!! != null)
                               for (modifier in product.om!!){
                                   if (omname.isNullOrEmpty()){
                                       omname=modifier.om_item_name!!
                                   }else{
                                       omname=omname+"\n"+modifier.om_item_name!!
                                   }
                               }
                           summary.omname=omname
                           if (product.topping!! != null)
                               for (topping in product.topping!!){
                                   if (toppingname.isNullOrEmpty()){
                                       toppingname=topping.name!!
                                   }else{
                                       toppingname=toppingname+"\n"+topping.name!!
                                   }
                               }
                           summary.toppingname=toppingname
                           summary.productType=product.type!!
                           drinkList!!.add(summary)
                           fmname=""
                           omname=""
                           detourname=""
                           toppingname=""
                       }
                   }
                   if (product.type.equals("Pizza")){
                       if (pizzaList!!.size>0){
                           var summary:Summary?=null
                           for (pizza in pizzaList!!){
                               if (pizza.name!!.equals(product.name)){
                                   summary=pizza
                                   break
                               }
                           }
                           Log.e("summary =",summary.toString())
                           if (summary != null){
                               summary.quantity=summary.quantity!!+product.quantity!!
                           }else{
                               var summary=Summary()
                               summary.name=product.name
                               summary.quantity=product.quantity
                               if (product.fm!! != null)
                                   for (modifier in product.fm!!){
                                       if (fmname.isNullOrEmpty()){
                                           fmname=modifier.fm_item_name!!
                                       }else{
                                           fmname=fmname+"\n"+modifier.fm_item_name!!
                                       }
                                   }
                               summary.fmname=fmname
                               if (product.detourom!! != null){
                                   for (modifier in product.detourom!!){
                                       if (detourname.isNullOrEmpty()){
                                           detourname=modifier.om_item_name!!
                                       }else{
                                           detourname=detourname+"\n"+modifier.om_item_name!!
                                       }
                                   }
                               }
                               summary.detourname=detourname
                               if (product.om!! != null)
                                   for (modifier in product.om!!){
                                       if (omname.isNullOrEmpty()){
                                           omname=modifier.om_item_name!!
                                       }else{
                                           omname=omname+"\n"+modifier.om_item_name!!
                                       }
                                   }
                               summary.omname=omname
                               if (product.topping!! != null)
                                   for (topping in product.topping!!){
                                       if (toppingname.isNullOrEmpty()){
                                           toppingname=topping.name!!
                                       }else{
                                           toppingname=toppingname+"\n"+topping.name!!
                                       }
                                   }
                               summary.toppingname=toppingname
                               summary.productType=product.type!!
                               pizzaList!!.add(summary)
                               fmname=""
                               omname=""
                               detourname=""
                               toppingname=""
                           }
                       }else{
                           var summary=Summary()
                           summary.name=product.name
                           summary.quantity=product.quantity
                           if (product.fm!! != null)
                               for (modifier in product.fm!!){
                                   if (fmname.isNullOrEmpty()){
                                       fmname=modifier.fm_item_name!!
                                   }else{
                                       fmname=fmname+"\n"+modifier.fm_item_name!!
                                   }
                               }
                           summary.fmname=fmname
                           if (product.detourom!! != null){
                               for (modifier in product.detourom!!){
                                   if (detourname.isNullOrEmpty()){
                                       detourname=modifier.om_item_name!!
                                   }else{
                                       detourname=detourname+"\n"+modifier.om_item_name!!
                                   }
                               }
                           }
                           summary.detourname=detourname
                           if (product.om!! != null)
                               for (modifier in product.om!!){
                                   if (omname.isNullOrEmpty()){
                                       omname=modifier.om_item_name!!
                                   }else{
                                       omname=omname+"\n"+modifier.om_item_name!!
                                   }
                               }
                           summary.omname=omname
                           if (product.topping!! != null)
                               for (topping in product.topping!!){
                                   if (toppingname.isNullOrEmpty()){
                                       toppingname=topping.name!!
                                   }else{
                                       toppingname=toppingname+"\n"+topping.name!!
                                   }
                               }
                           summary.toppingname=toppingname
                           summary.productType=product.type!!
                           pizzaList!!.add(summary)
                           fmname=""
                           omname=""
                           detourname=""
                           toppingname=""
                       }
                   }
               }
               if (dishList!!.size>0 && isDishes){
                   isDishes=false
                   if (SessionManager.isDisplayDish(requireActivity())!! || SessionManager.isDisplayAllProduct(requireActivity())!!){
                       intiateRecycleview("Dishes", dishList!!)
                   }

               }
               if (drinkList!!.size>0 && isDrink){
                   isDrink=false
                   if (SessionManager.isSelectedDrink(requireActivity())!! || SessionManager.isDisplayAllProduct(requireActivity())!!){
                       intiateRecycleview("Drinks",drinkList!!)
                   }

               }
               if (pizzaList!!.size>0 && isPizza){
                   isPizza=false
                   if (SessionManager.isSelectedPizza(requireActivity())!! || SessionManager.isDisplayAllProduct(requireActivity())!!){
                       intiateRecycleview("Pizza",pizzaList!!)
                   }

               }
               if (summaryAdapter != null)
                   summaryAdapter!!.notifyDataSetChanged()
           }catch (e:Exception){
               e.printStackTrace()
           }catch (e:NullPointerException){
               e.printStackTrace()
           }

        })

        return binding!!.root
    }

    fun intiateRecycleview(headername:String,productlist:ArrayList<Summary>){
        Log.e("productlist size=",productlist.size.toString()+"--")
        val summary_recyclerView = RecyclerView(requireActivity())
        var mLayoutManager= LinearLayoutManager(requireActivity())
        summary_recyclerView.layoutManager = mLayoutManager
        summary_recyclerView.setItemAnimator(DefaultItemAnimator())
        summaryAdapter= SummaryAdapter(productlist,requireActivity())
        summary_recyclerView.adapter = summaryAdapter
        summary_layout!!.addView(summary_recyclerView)
    }

    fun updateSummary(){
      summaryRepository!!.getAllOrderFromLocal()
    }
}