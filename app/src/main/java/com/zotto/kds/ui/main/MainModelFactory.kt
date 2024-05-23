package com.zotto.kds.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.repository.MainReprository

class MainModelFactory(var mainReprository: MainReprository) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(mainReprository) as T
    }
}