package com.zotto.kds.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.LoginResultCallBacks
import com.zotto.kds.R
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.RestaurantDao
import com.zotto.kds.databinding.LoginActivityBinding
import com.zotto.kds.repository.LoginRepository
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.LocaleHelper
import com.zotto.kds.utils.SessionManager

class LoginActivity : AppCompatActivity(), LoginResultCallBacks {
  private var binding: LoginActivityBinding? = null
  private var loginViewModel: LoginViewModel? = null
  private var apiServices: ApiServices? = null
  private var appDatabase: AppDatabase? = null
  private var restaurantDao: RestaurantDao? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    LocaleHelper.setLocale(this, SessionManager.getLanguage(this))
    binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
    RetroClient.setupRestClient()
    appDatabase = DatabaseClient.getInstance(this!!)!!.getAppDatabase()
    restaurantDao = appDatabase!!.restaurantDao()
    apiServices = RetroClient.getApiService()
    val loginRepository = LoginRepository(this, restaurantDao!!, apiServices!!, this)
    loginViewModel = ViewModelProvider(
      this,
      LoginViewModelFactory(loginRepository)
    ).get(LoginViewModel::class.java)
    binding!!.loginviewmodel = loginViewModel
    binding!!.lifecycleOwner = this

  }

  override fun onSuccess(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    var intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }

  override fun onError(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
  }
}