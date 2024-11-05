package com.zotto.kds.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zotto.kds.LoginResultCallBacks
import com.zotto.kds.R
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.RestaurantDao
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.databinding.LoginActivityBinding
import com.zotto.kds.model.BaseUrlModel
import com.zotto.kds.printing.HPRTPrinterPrinting
import com.zotto.kds.repository.LoginRepository
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.SyncDataActivity
import com.zotto.kds.ui.home.HomeFragment
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginActivity : AppCompatActivity(), LoginResultCallBacks {
  private var binding: LoginActivityBinding? = null
  private var loginViewModel: LoginViewModel? = null
  private var apiServices: ApiServices? = null
  private var appDatabase: AppDatabase? = null
  private var restaurantDao: RestaurantDao? = null
  private val items = listOf(
    Item(1, "Select Domain"),
    Item(2, "Development"),
    Item(3, "Production"),
  )
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    LocaleHelper.setLocale(this, SessionManager.getLanguage(this))
    binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
    val spinner: Spinner = findViewById(R.id.spinner)
//    val spinnerView: ConstraintLayout = findViewById(R.id.spin_view)
    val loginView: ConstraintLayout = findViewById(R.id.login_view)
    RetroClient.setupRestClient()
    apiServices = RetroClient.getApiService()
//    getBaseUrls()
    val itemNames = items.map { it.name }
    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemNames)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
        // Get the selected item from the model based on the position
        val selectedItem = items[position]
        Log.e("item","${selectedItem.name}")
//        if(selectedItem.id!=1){
//          spinnerView.visibility = View.GONE
//          loginView.visibility = View.VISIBLE
//        }

//        selectedItemTextView.text = "Selected Item: ${selectedItem.name}"
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle no selection case
      }
    }

    appDatabase = DatabaseClient.getInstance(this!!)!!.getAppDatabase()
    restaurantDao = appDatabase!!.restaurantDao()
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
    var intent = Intent(this, SyncDataActivity::class.java)
    startActivity(intent)
    finish()
  }

  override fun onError(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
  }

  fun getBaseUrls() {
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices!!.getBaseUrl().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({ response -> baseUrlResponse(response) },
          { t -> onSingleOrderFailure(t) })
    )
  }

  fun baseUrlResponse(response: BaseUrlModel) {
    try {
      if (response.status==1) {

      } else {

      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    // }

  }

  private fun onSingleOrderFailure(t: Throwable) {
    t.printStackTrace()
  }
}

data class Item(val id: Int, val name: String)