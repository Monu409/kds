package com.zotto.kds.ui.itemmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zotto.kds.database.table.CategoryTable
import com.zotto.kds.repository.ItemManagementRepository

class ItemManagementViewModel(var itemManagementRepository: ItemManagementRepository) :
  ViewModel() {
  var selectedItem = 0
  val categorylivedata: LiveData<List<CategoryTable>> get() = itemManagementRepository.categorylivedata

  init {
    getAllCategories()
  }

  fun getAllCategories() {
    selectedItem = 0
    ItemManagement.binding!!.invalidateAll()
    itemManagementRepository.getAllCategories()
  }

  fun getAvailableCategories() {
    selectedItem = 1
    ItemManagement.binding!!.invalidateAll()
    itemManagementRepository.getAvailableCategoryLocal()
  }

  fun getUnavailableCategories() {
    selectedItem = 2
    ItemManagement.binding!!.invalidateAll()
    itemManagementRepository.getUnavailableCategoryLocal()
  }
}