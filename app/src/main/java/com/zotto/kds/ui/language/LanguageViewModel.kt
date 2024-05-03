package com.zotto.kds.ui.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zotto.kds.model.Language
import com.zotto.kds.repository.LanguageRepository

class LanguageViewModel(var languageRepository: LanguageRepository):ViewModel() {
     var languageListMutableLiveData= MutableLiveData<List<Language>>()
     val languagelivedata: LiveData<List<Language>> get() = languageListMutableLiveData
    init {
        languageListMutableLiveData=languageRepository.getLanguageList()
    }

}