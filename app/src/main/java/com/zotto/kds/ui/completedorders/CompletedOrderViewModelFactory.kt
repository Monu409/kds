package com.zotto.kds.ui.completedorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.repository.CompletedOrdersRepository

class CompletedOrderViewModelFactory(val completedOrdersRepository: CompletedOrdersRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return CompletedOrderViewModel(completedOrdersRepository) as T
    }

}