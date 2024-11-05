package com.zotto.kds.ui.itemmanagement

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.adapter.ItemManagementAdapter
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.CategoryDao
import com.zotto.kds.database.dao.DisabledCategoryDao
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.table.CategoryTable
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.databinding.ItemManagementBinding
import com.zotto.kds.repository.ItemManagementRepository
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.Singleton

class ItemManagement : AppCompatActivity() {
  companion object {
    var binding: ItemManagementBinding? = null
    lateinit var restId: String
    var itemManagementViewModel: ItemManagementViewModel? = null
    var appDatabase: AppDatabase? = null
    var allData: List<Product>? = null
    var allCatData: List<CategoryTable>? = null
    private var recyclerView: RecyclerView? = null
    var sectionDataList: ArrayList<Any> = ArrayList()
    var disabledProductDao: DisabledCategoryDao? = null
    var categoryDao: CategoryDao? = null
    var itemManagementAdapter: ItemManagementAdapter? = null
    var typeList: List<String>? = null
    var orderList = ArrayList<Order>()
    var productList = ArrayList<Product>()
    var itemManagementRepository: ItemManagementRepository? = null
    var apiServices: ApiServices = RetroClient.getApiService()!!
  }

  fun onClickEvent() {
    Log.e("onClickEvent", " Clicked now")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.item_management)
    binding!!.lifecycleOwner = this
    restId = SessionManager.getRestaurantId(this)
    appDatabase = DatabaseClient.getInstance(this)!!.getAppDatabase()
    disabledProductDao = appDatabase!!.disableProductDao()
    var orderDao: OrderDao? = appDatabase!!.orderDao()
    categoryDao = appDatabase!!.categoryDao()
    var apiServices: ApiServices? = RetroClient.getApiService()
    itemManagementRepository = ItemManagementRepository(this, apiServices!!, categoryDao!!)

    itemManagementViewModel = ViewModelProvider(
      this, ItemManagementViewModelFactory(
        itemManagementRepository!!
      )
    ).get(ItemManagementViewModel::class.java)
    binding!!.viewModel = itemManagementViewModel
    binding!!.itemPage = this
    recyclerView = binding!!.recyclerView
    typeList = ArrayList()

    orderList = ArrayList()
    productList = ArrayList()

    itemManagementAdapter = ItemManagementAdapter(restId, this, disabledProductDao,apiServices)
    val layoutManager = GridLayoutManager(this, 2)

    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        return if (itemManagementAdapter!!.getItemViewType(position) == itemManagementAdapter!!.VIEW_TYPE_HEADER) 2 else 1
      }
    }

    binding!!.recyclerView.layoutManager = layoutManager
    binding!!.recyclerView.adapter = itemManagementAdapter
    binding!!.backBtn.setOnClickListener {
      if (MainActivity.refreshFragment) {
        orderDao!!.updateALlDisableOrder()
      }
      Singleton.ordertype = "active"
      Singleton.isactiveclicked = true
      finish()
    }

    itemManagementViewModel!!.categorylivedata.observe(this) {
      Log.e("observe data", " ${it}")
      allCatData = it
      setUpCatList(allCatData!!)
    }

  }

  fun setUpCatList(items: List<CategoryTable>) {
    if (itemManagementAdapter != null) {
      itemManagementAdapter!!.submitList(items)
      itemManagementAdapter!!.notifyDataSetChanged()
    }
  }

  @RequiresApi(Build.VERSION_CODES.M)
  fun isOnline(context: Context): Boolean {
    val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
      val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
      if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
          Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
          return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
          Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
          return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
          Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
          return true
        }
      }
    }
    return false
  }
}