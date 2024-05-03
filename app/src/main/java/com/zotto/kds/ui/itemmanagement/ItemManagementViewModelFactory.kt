package com.zotto.kds.ui.itemmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.repository.ItemManagementRepository

class ItemManagementViewModelFactory(var itemManagementRepository: ItemManagementRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return ItemManagementViewModel(itemManagementRepository!!) as T
    }
}