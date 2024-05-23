package com.zotto.kds.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.repository.SettingRepository

class SettingViewModelFactory(var settingRepository: SettingRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return SettingViewModel(settingRepository) as T
    }
}