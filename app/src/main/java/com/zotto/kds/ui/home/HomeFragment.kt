package com.zotto.kds.ui.home

import android.annotation.TargetApi
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zotto.kds.*
import com.zotto.kds.adapter.CompletedOrderAdapter
import com.zotto.kds.adapter.OrderAdapter
import com.zotto.kds.adapter.SummaryAdapter
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.ProductDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.databinding.FragmentHomeBinding
import com.zotto.kds.`interface`.OrderBroadcastReciever
import com.zotto.kds.model.Summary
import com.zotto.kds.printing.HPRTPrinterPrinting
import com.zotto.kds.rabbitmq.Config
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.dialog.ProductUnavailable
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.Singleton
import java.util.*


class HomeFragment : Fragment(), OrderAdapter.OrderOnClickListner,
  CompletedOrderAdapter.CompletedOrderOnClickListner {

  companion object {
    private var binding: FragmentHomeBinding? = null
    private var homeViewModel: HomeViewModel? = null
    private var apiServices: ApiServices? = null
    private var appDatabase: AppDatabase? = null
    private var orderDao: OrderDao? = null
    private var productDao: ProductDao? = null
    private var order_recycleview: RecyclerView? = null
    private var orderAdapter: OrderAdapter? = null
    private var completedorderAdapter: CompletedOrderAdapter? = null
    private var homeRepository: HomeRepository? = null
    private var orderlist = ArrayList<Order>()
    private var textToSpeech: TextToSpeech? = null
    private var ttsStatus = false
    var summary_layout: LinearLayout? = null
    var summaryAdapter: SummaryAdapter? = null
    var dishList: ArrayList<Summary>? = null
    var drinkList: ArrayList<Summary>? = null
    var pizzaList: ArrayList<Summary>? = null
    var productList = ArrayList<Product>()
    var dishqty = 0
    var isDishes = true
    var isDrink = true
    var isPizza = true

  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    RetroClient.setupRestClient()
    Singleton.fragmentManager = requireActivity()!!.supportFragmentManager
    appDatabase = DatabaseClient.getInstance(requireActivity()!!)!!.getAppDatabase()
    orderDao = appDatabase!!.orderDao()
    productDao = appDatabase!!.productDao()
    apiServices = RetroClient.getApiService()
    orderAdapter = OrderAdapter(requireActivity()!!, this)
    homeRepository = HomeRepository(
      requireActivity(),
      orderDao!!,
      productDao!!,
      apiServices!!,
      orderAdapter!!,
      this,
      requireActivity().supportFragmentManager
    )
    homeViewModel =
      ViewModelProvider(this, HomeModelFactory(homeRepository!!)).get(HomeViewModel::class.java)
    binding!!.homeviewmodel = homeViewModel
    summary_layout = binding!!.summary.summaryLayout
    binding!!.lifecycleOwner = viewLifecycleOwner
    dishList = ArrayList()
    drinkList = ArrayList()
    pizzaList = ArrayList()
    productList = ArrayList<Product>()
    dishqty = 0
    isDishes = true
    isDrink = true
    isPizza = true
    if (!SessionManager.isSelectedPizza(requireActivity())!! && !SessionManager.isSelectedDrink(
        requireActivity()
      )!!
      && !SessionManager.isDisplayDish(requireActivity())!!
    ) {
      SessionManager.setDisplayAllProduct(requireActivity(), true)
    }
    if (!SessionManager.isInShopOrder(requireActivity()) && !SessionManager.isOnlineOrder(
        requireActivity()
      )
      && !SessionManager.isAllOrder(requireActivity())
    ) {
      SessionManager.setAllOrder(requireActivity(), true)
    }
    if (!SessionManager.isAutoPrint(requireActivity()) && !SessionManager.isPrint(requireActivity())) {
      SessionManager.setAutoPrint(requireActivity(), true)
    }

//    var mGridLayoutManager = GridLayoutManager(context,3)
//    val marginStandard = resources.getDimension(R.dimen.margin_standard)
//    Toast.makeText(requireActivity(), "margin_standard: $marginStandard pixels", Toast.LENGTH_LONG).show()


    val mGridLayoutManager = GridLayoutManager(context, 3)

    order_recycleview = binding!!.orderRecycleview
    order_recycleview!!.layoutManager = mGridLayoutManager
    order_recycleview!!.layoutDirection = View.LAYOUT_DIRECTION_RTL
    order_recycleview!!.setItemAnimator(DefaultItemAnimator())
    order_recycleview!!.setHasFixedSize(true)
    order_recycleview!!.adapter = orderAdapter

    var hprtPrinterPrinting = HPRTPrinterPrinting(requireActivity())
    hprtPrinterPrinting.printerPermission(requireActivity())
    hprtPrinterPrinting.openUSBPrintingPort(requireActivity())
    homeViewModel!!.orderlivedata!!.observe(viewLifecycleOwner, Observer {
      try {
//        Log.e(
//          "orderstatus==",
//          it.size.toString() + "--" + it!!.get(0)!!.order_status + "-order_id-" + it!!.get(0)!!.order_id + "-ordertype-" + Singleton.ordertype
//        )
        if (Singleton.ordertype.equals("active") && it!!.size > 0) {
          MainActivity.completedOrders!!.text = resources.getString(R.string.completed_txt)
          if (it != null && it.size > 0 && it.get(0)!!.order_status.equals("Confirm")) {
            MainActivity.activeOrders!!.text =
              resources.getString(R.string.active_txt) + "(" + it.size.toString() + ")"
          } else {
            MainActivity.activeOrders!!.text = resources.getString(R.string.active_txt)
          }

          order_recycleview!!.apply {
            order_recycleview!!.layoutManager = null
            orderAdapter = null
            orderAdapter = OrderAdapter(requireActivity()!!, this@HomeFragment)
//            order_recycleview!!.layoutManager = GridLayoutManager(context, 3)
            order_recycleview!!.layoutManager = StaggeredGridLayoutManager(3 , LinearLayoutManager.VERTICAL)
            order_recycleview!!.setItemAnimator(DefaultItemAnimator())
            order_recycleview!!.setHasFixedSize(true)
          }

          if (it != null && it.size > 0 && !it.get(0)!!.order_status.equals("Ready")) {
            orderAdapter!!.submitList(it)
          }
          order_recycleview!!.adapter = orderAdapter
          if (it.size == 0) {
            dishList!!.clear()
            drinkList!!.clear()
            pizzaList!!.clear()
            if (summaryAdapter != null) {
              summaryAdapter!!.notifyDataSetChanged()
            }
          }

          /*  if (!Singleton.isactiveclicked){
                var summaryDailog=SummaryDailog()
                summaryDailog.updateSummary()
                *//*if (it != null && it.size>0)
                            startSpeech("Hi there! New order arrive. Please check.", "")*//*
                    }*/

        } else if (Singleton.ordertype.equals("completed") && it!!.size > 0) {
          MainActivity.activeOrders!!.text = resources.getString(R.string.active_txt)
          if (it != null && it.size > 0 && it.get(0)!!.order_status.equals("Ready")) {
            MainActivity.completedOrders!!.text =
              resources.getString(R.string.completed_txt) + "(" + it.size.toString() + ")"
          } else {
            MainActivity.completedOrders!!.text = resources.getString(R.string.completed_txt)
          }

          order_recycleview!!.apply {
            order_recycleview!!.layoutManager = null
            orderAdapter = null
            completedorderAdapter =
              CompletedOrderAdapter(it, requireActivity()!!, this@HomeFragment,this@HomeFragment)
//            order_recycleview!!.layoutManager = GridLayoutManager(context, 3)
            order_recycleview!!.layoutManager = StaggeredGridLayoutManager(3 , LinearLayoutManager.VERTICAL)
            order_recycleview!!.setItemAnimator(DefaultItemAnimator())
            order_recycleview!!.setHasFixedSize(true)
          }
          if (it != null && it.size > 0 && it.get(0)!!.order_status.equals("Ready")) {
            completedorderAdapter!!.submitList(it?.toMutableList())
          }

          order_recycleview!!.adapter = completedorderAdapter
          if (completedorderAdapter != null)
            completedorderAdapter!!.notifyDataSetChanged()
        }
        homeViewModel!!.getAllOrderFromLocal(requireActivity())
      } catch (e: Exception) {
        e.printStackTrace()
      } catch (e: NullPointerException) {
        e.printStackTrace()
      }
    })
    homeViewModel!!.productlivedata.observe(viewLifecycleOwner) {

      summary_layout!!.removeAllViews()
      dishList = ArrayList()
      drinkList = ArrayList()
      pizzaList = ArrayList()
      try {
        Log.e("productlivedata =", "observed now== " + it.size.toString())
        Log.e("productlist order size=", it.size.toString() + "--")
        for (product in it) {
          if (!product.status.equals("Ready")) {

            if (product.type.equals("Dishes")) {
              createSummary(product, dishList)
            }
            if (product.type.equals("Drinks")) {
              createSummary(product, drinkList)
            }
            if (product.type.equals("Pizza")) {
              createSummary(product, pizzaList)
            }

          }
        }
        Log.e(
          "dishsize = ", dishList!!.size.toString()
              + "-isDisplayDish-" + SessionManager.isDisplayDish(requireActivity()) + "-isDisplayAllProduct-" + SessionManager.isDisplayAllProduct(
            requireActivity()
          )
        )

        var allData: ArrayList<Any> = ArrayList()
        if (dishList!!.size > 0) {
          allData.add("Dishes")
          allData.addAll(dishList!!)
        }
        if (drinkList!!.size > 0) {
          allData.add("Drinks")
          allData.addAll(drinkList!!)
        }
        if (pizzaList!!.size > 0) {
          allData.add("Pizza")
          allData.addAll(pizzaList!!)
        }
        if (allData.size > 0) {
         intiateRecycleview(allData)
        }
      } catch (e: Exception) {
        e.printStackTrace()
      } catch (e: NullPointerException) {
        e.printStackTrace()
      }

    }
    val orderBroadcastReceiver = OrderBroadcastReciever(homeRepository!!)
    val intentFilter = IntentFilter()
    intentFilter.addAction(Config.ORDER_NOTIFICATION)
    intentFilter.addAction(Config.ROBOT_NOTIFICATION)
    intentFilter.addAction(Config.APPUPDATE_NOTIFICATION)
    intentFilter.addAction(Config.LOCAL_IP_ORDER_NOTIFICATION)
    LocalBroadcastManager.getInstance(requireActivity())
      .registerReceiver(orderBroadcastReceiver, intentFilter)

    textToSpeech = TextToSpeech(requireActivity(),
      OnInitListener { status ->
        if (status == TextToSpeech.SUCCESS) {
          ttsStatus = true
        }

      })
    return binding!!.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    LocalBroadcastManager.getInstance(requireActivity())
      .unregisterReceiver(OrderBroadcastReciever(homeRepository!!))
  }

  fun startSpeech(speech: String?, orderid: String) {
    try {
      if (ttsStatus) {
        if (!orderid.isEmpty()) {
          val orders: List<Order> = orderlist
          var filterOrder: Order? = null
          var orderAvailability = false
          for (j in orders.indices) {
            val order = orders[j]
            if (order.order_id.equals(orderid)) {
              orderAvailability = true
              filterOrder = order
              break
            }
          }
          if (orderAvailability) {
            if (filterOrder!!.order_time.equals(getString(R.string.time_up))) {
              var speechStatus = 0
              if (Build.VERSION.SDK_INT >= 21) speechStatus =
                ttsGreater21(speech!!) else speechStatus = ttsUnder20(speech!!)
              if (speechStatus == TextToSpeech.ERROR) {
                Log.e("TTS", "Error in converting Text to Speech!")
                textToSpeech = TextToSpeech(requireActivity(),
                  OnInitListener {
                    if (Build.VERSION.SDK_INT >= 21) ttsGreater21(speech!!) else ttsUnder20(
                      speech
                    )
                  })
              }
            }
          }
        } else {
          var speechStatus = 0
          if (Build.VERSION.SDK_INT >= 21) speechStatus =
            ttsGreater21(speech!!) else speechStatus = ttsUnder20(speech!!)
          if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!")
            textToSpeech = TextToSpeech(requireActivity(),
              OnInitListener {
                if (Build.VERSION.SDK_INT >= 21) ttsGreater21(speech!!)
                else
                  ttsUnder20(
                    speech!!
                  )
              })
          }
        }
      }
    } catch (ex: Exception) {
      ex.printStackTrace()
    } catch (ex: Error) {
      ex.printStackTrace()
    }
  }

  private fun ttsUnder20(speech: String): Int {
    var result = 0
    try {
      val map = HashMap<String, String>()
      map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
      val speechStatus = textToSpeech!!.speak(speech, TextToSpeech.QUEUE_FLUSH, map)
      if (speechStatus == TextToSpeech.ERROR) result = TextToSpeech.ERROR
    } catch (ex: java.lang.Exception) {
      ex.printStackTrace()
    }
    return result
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private fun ttsGreater21(speech: String): Int {
    var result = 0
    try {
      val utteranceId = this.hashCode().toString() + ""
      val params = Bundle()
      params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
      val speechStatus =
        textToSpeech!!.speak(speech, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
      if (speechStatus == TextToSpeech.ERROR) result = TextToSpeech.ERROR
    } catch (ex: java.lang.Exception) {
      ex.printStackTrace()
    }
    return result
  }

  private fun stopSpeech() {
    try {
      if (textToSpeech != null) {
        textToSpeech!!.shutdown()
      }

    } catch (ex: Exception) {
      ex.printStackTrace()
    }
  }

  override fun updateOrder(position: Int, order: Order) {
    homeViewModel!!.updateOrder(
      order.order_date!!,
      order.comments!!,
      order.order_id!!,
      order.order_status!!,
      order.order_time!!,
      order.order_time!!,
      order.rname ?: "",
      order.order_time!!,
      order
    )
    /* homeViewModel!!.productlivedata.observe(viewLifecycleOwner, Observer {
         for (product in order.products!!){
             if (dishList!!.size>0){
                 Log.e("updateorder =",dishList!!.size.toString())
                 for (dish in dishList!!){
                     Log.e("updateorder=",dish.name+"-product-"+product.name+"-dishqty-"+dish.quantity+"-productqty-"+product.quantity!!)
                     if (dish.name!!.equals(product.name)){
                         var index=dishList!!.indexOf(dish!!)
                         if (dish.quantity!! == 1){
                             dishList!!.removeAt(index)
                         }else{
                             dish.quantity=(dish.quantity!!-product.quantity!!)
                             dishList!!.set(index,dish)
                         }

                         if (summaryAdapter != null)
                             summaryAdapter!!.notifyItemChanged(index)
                     }
                 }
             }

         }
     })*/
    if (orderAdapter != null) {
      orderAdapter!!.notifyItemChanged(position)
      if (position == 0) {
        homeViewModel!!.orderlivedata!!.observe(viewLifecycleOwner, Observer {
          orderAdapter!!.submitList(it!!)
        })
//        orderAdapter!!.submitList(null)
        homeViewModel!!.getAllOrderFromLocal(requireActivity())
      }
    }
  }

  override fun updateProduct(product: Product) {
    homeViewModel!!.updateProduct(
      requireActivity(),
      product.product_id ?: "",
      product.serial_no ?: "",
      product.order_id ?: "",
      product.status ?: "",
      product.timestamp ?: "",
      product.name ?: "",
      "",
      "",
      product
    )
  }

  override fun updateProductTicket(product: Product) {
    homeViewModel!!.updateProductTicket(
      product.timestamp!!,
      product.order_id!!,
      product.order_id!!,
      product.status!!,
      product.timestamp!!,
      product.name!!,
      ""
    )
  }

  override fun cancelProduct(product: Product) {
    val fm: FragmentManager = requireActivity().getSupportFragmentManager()
    val dFragment = ProductUnavailable()
    dFragment.show(fm, "Dialog Fragment")
    dFragment.setCancelable(true)
  }

  override fun reecallOrder(position: Int, order: Order) {

    homeRepository!!.updateRecallOrder(
      homeRepository!!.updateOrderJsonMap(
        order.order_date!!,
        order.comments!!,
        order.order_id!!,
        order.order_status!!,
        order.order_time!!,
        order.order_time!!,
        order.rname!!,
        order.order_time!!,
        order
      )!!, order.order_id!!, order
    )
    if (completedorderAdapter != null) {
      completedorderAdapter!!.notifyItemChanged(position)
    }
  }

  fun createSummary(product: Product, dataArray: ArrayList<Summary>?): Summary {
    var summary: Summary? = dataArray!!.find { it.name.equals(product.name) }
    if (summary != null) {
      summary.quantity = summary.quantity!! + product.quantity!!
    } else {
      summary = Summary()
      summary.name = product.name
      summary.quantity = product.quantity
      summary.fmname = getFModifier(product)
      summary.detourname = getDetourom(product)
      summary.omname = getOModifier(product)
      summary.toppingname = getTopping(product)
      summary.productType = product.type!!
      dataArray.add(summary)
    }
    return summary
  }

  fun getFModifier(product: Product): String {
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
    return allModifiers
  }

  fun getDetourom(product: Product): String {
    var allModifiers = ""
    if (product.detourom != null)
      for (modifier in product.detourom!!) {
        if (!modifier.om_item_name.isNullOrEmpty()) {
          if (allModifiers.isNotEmpty())
            allModifiers += "\n"
          allModifiers += modifier.om_item_name!!
        }
      }
    return allModifiers
  }

  fun getOModifier(product: Product): String {
    var allModifiers = ""
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
    return allModifiers
  }

  fun getTopping(product: Product): String {
    var allTopping = ""
    if (product.topping != null)
      for (topping in product.topping!!) {
        if (!topping.name.isNullOrEmpty()) {
          if (allTopping.isNotEmpty())
            allTopping += ", "
          allTopping += topping.name!!
        }
      }
    return allTopping
  }

  fun sortProducts(productList: List<Product>) {
    Collections.sort(productList, Comparator { obj1, obj2 ->
      obj1.status!!.compareTo(obj2.status!!)
    })
  }

  fun intiateRecycleview(productlist: ArrayList<Any>) {
    val summary_recyclerView = RecyclerView(requireActivity())
    var mLayoutManager = LinearLayoutManager(requireActivity())
    summary_recyclerView.layoutManager = mLayoutManager
    summary_recyclerView.setItemAnimator(DefaultItemAnimator())
    summaryAdapter = SummaryAdapter(productlist, requireActivity())
    summary_recyclerView.adapter = summaryAdapter
    summary_recyclerView.minimumHeight = 100
    summary_recyclerView.setHasFixedSize(true)
    summary_layout!!.addView(summary_recyclerView)
  }

}


