package com.zotto.kds.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.repository.SummaryRepository

class SummaryViewModelFactory(val summaryRepository: SummaryRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return SummaryViewModel(summaryRepository) as T
    }
}