package com.zotto.kds.ui.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.model.Summary
import com.zotto.kds.repository.SummaryRepository

class SummaryViewModel(var summaryRepository: SummaryRepository):ViewModel() {
    val orderlivedata: LiveData<List<Order>> get() = summaryRepository.orderlivedata
    val productlivedata: LiveData<List<Product>> get() = summaryRepository.productlivedata
    init {
        summaryRepository.getAllOrderFromLocal()
    }

}