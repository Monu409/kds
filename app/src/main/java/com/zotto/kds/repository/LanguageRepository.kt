package com.zotto.kds.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zotto.kds.R
import com.zotto.kds.database.table.Order
import com.zotto.kds.model.Language

class LanguageRepository(var context: Context) {
    private var languageListMutableLiveData= MutableLiveData<List<Language>>()
    private val languagelivedata: LiveData<List<Language>> get() = languageListMutableLiveData


    fun getLanguageList(): MutableLiveData<List<Language>>
    {
        var language=Language()
        language.languagename="English"
        language.lang_code="en-gb"
        language.countryname="United Kingdom"
        language.countryimage=context.resources.getDrawable(R.drawable.uk_flag)
        var language1=Language()
        language1.languagename="Danish"
        language1.lang_code="da"
        language1.countryname="Denmark"
        language1.countryimage=context.resources.getDrawable(R.drawable.denmark_flag)
        languageListMutableLiveData.value= mutableListOf(language,language1)

        return languageListMutableLiveData!!
    }
}