package com.zotto.kds.ui.language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.zotto.kds.R
import com.zotto.kds.adapter.LanguageAdapter
import com.zotto.kds.adapter.OrderAdapter
import com.zotto.kds.databinding.LanguageActivityBinding
import com.zotto.kds.model.Language
import com.zotto.kds.repository.LanguageRepository
import com.zotto.kds.ui.login.LoginActivity
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.LocaleHelper
import com.zotto.kds.utils.SessionManager

class LanguageActiviity: AppCompatActivity(),LanguageAdapter.LanguageOnClickListner{
    private var binding:LanguageActivityBinding?=null
    private var languageViewModel:LanguageViewModel?=null
    private var languageAdapter: LanguageAdapter? = null
    private var language_recycleview: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.language_activity)
        var languageRepository=LanguageRepository(this)
        languageViewModel=ViewModelProvider(this,LanguageModelFactory(languageRepository)).get(LanguageViewModel::class.java)
        binding!!.languageviewmodel=languageViewModel
        binding!!.lifecycleOwner=this
        language_recycleview= binding!!.languageRecycleview
        language_recycleview!!.layoutManager = GridLayoutManager(this , 4)
        language_recycleview!!.setItemAnimator(DefaultItemAnimator())
        languageViewModel!!.languagelivedata.observe(this, Observer {
            Log.e("size =",it.size.toString()+"--")
            languageAdapter= LanguageAdapter(it,this,this)
            languageAdapter!!.submitList(it)
            language_recycleview!!.adapter=languageAdapter
            languageAdapter!!.notifyDataSetChanged()
        })
    }

    override fun selectLangauage(language: Language) {
        LocaleHelper.setLocale(this,language.lang_code)
        SessionManager.setLanguage(this,language.lang_code)
        if (!SessionManager.isLoggedIn(this)){
            var intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            SessionManager.setChangeLanguage(this,false)
            var intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}