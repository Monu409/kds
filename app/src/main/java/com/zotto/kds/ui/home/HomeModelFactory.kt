package com.zotto.kds.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.repository.HomeRepository
import com.zotto.kds.repository.LoginRepository

class HomeModelFactory(val homeRepository: HomeRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(homeRepository!!) as T
    }
}