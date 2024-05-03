package com.zotto.kds.ui.itemmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.repository.ItemManagementRepository

class ItemManagementViewModel(var itemManagementRepository: ItemManagementRepository) :
  ViewModel() {
  val orderlivedata: LiveData<List<Order>> get() = itemManagementRepository!!.orderlivedata
  val productlivedata: LiveData<List<Product>> get() = itemManagementRepository!!.productlivedata

  init {
    itemManagementRepository.getAllOrderFromLocal()
  }
}