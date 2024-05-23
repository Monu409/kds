package com.zotto.kds.ui.itemmanagement

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.R
import com.zotto.kds.adapter.ItemManagementAdapter
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.ProductDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.databinding.ItemManagementBinding
import com.zotto.kds.model.Summary
import com.zotto.kds.repository.ItemManagementRepository

class ItemManagement : AppCompatActivity() {
  companion object {
    private var binding: ItemManagementBinding? = null
    var itemManagementViewModel: ItemManagementViewModel? = null
    var appDatabase: AppDatabase? = null
    var orderDao: OrderDao? = null
    var productDao: ProductDao? = null
    var itemManagementAdapter: ItemManagementAdapter? = null
    var dishList: ArrayList<Summary>? = null
    var drinkList: ArrayList<Summary>? = null
    var pizzaList: ArrayList<Summary>? = null
    var orderList = ArrayList<Order>()
    var productList = ArrayList<Product>()
    var itemManagementRepository: ItemManagementRepository? = null
    var dishqty = 0
    var isDishes = true
    var isDrink = true
    var isPizza = true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.item_management)
    appDatabase = DatabaseClient.getInstance(this)!!.getAppDatabase()
    orderDao = appDatabase!!.orderDao()
    productDao = appDatabase!!.productDao()
    itemManagementRepository = ItemManagementRepository(this, orderDao!!)
    itemManagementViewModel = ViewModelProvider(
      this, ItemManagementViewModelFactory(
        itemManagementRepository!!
      )
    ).get(ItemManagementViewModel::class.java)
    binding!!.lifecycleOwner = this
    dishList = ArrayList()
    drinkList = ArrayList()
    pizzaList = ArrayList()
    orderList = ArrayList()
    productList = ArrayList<Product>()
    findViewById<ImageView>(R.id.back_btn).setOnClickListener {
      onBackPressed()
    }
  }
}