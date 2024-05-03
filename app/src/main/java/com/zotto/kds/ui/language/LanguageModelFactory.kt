package com.zotto.kds.ui.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.repository.LanguageRepository

class LanguageModelFactory(var languageRepository: LanguageRepository) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return LanguageViewModel(languageRepository) as T
    }
}