package com.zotto.kds.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.R
import com.zotto.kds.database.table.DeviceTable
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.MultiSpinner
import com.zotto.kds.utils.MultiSpinner.MultiSpinnerListener
import com.zotto.kds.utils.SessionManager
import com.zotto.kds.utils.SessionManager.Companion.getSelectedIp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SettingFragment : AppCompatActivity() {
  private var order_display_grp: RadioGroup? = null
  private var order_cooking_timer_grp: RadioGroup? = null
  private var robot_delivery_grp: RadioGroup? = null
  private var printing_grp: RadioGroup? = null
  lateinit var apiServices: ApiServices
  //  private var device_spnr: Spinner? = null
  private var device_spnr: MultiSpinner? = null

  var deviceItems: List<DeviceTable> = ArrayList()

  var default_device: DeviceTable = DeviceTable("None")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.setting_activity)
    apiServices = RetroClient.getApiService()!!
    order_display_grp = findViewById(R.id.order_display_grp)
    order_cooking_timer_grp = findViewById(R.id.order_cooking_timer_grp)
    robot_delivery_grp = findViewById(R.id.robot_delivery_grp)
    printing_grp = findViewById(R.id.printing_grp)

    device_spnr = findViewById(R.id.device_spnr)
    if (SessionManager.isInShopOrder(this)) {
      findViewById<RadioButton>(R.id.inshop_order_only).isChecked = true
      SessionManager.setInShopOrder(this, true)
    }
    if (SessionManager.isOnlineOrder(this)) {
      findViewById<RadioButton>(R.id.online_order_only).isChecked = true
      SessionManager.setOnlineOrder(this, true)
    }
    if (SessionManager.isAllOrder(this)) {
      findViewById<RadioButton>(R.id.all).isChecked = true
      SessionManager.setAllOrder(this, true)
    }
    if (!SessionManager.isInShopOrder(this) && !SessionManager.isOnlineOrder(this)
      && !SessionManager.isAllOrder(this)
    ) {
      findViewById<RadioButton>(R.id.all).isChecked = true
      SessionManager.setAllOrder(this, true)
    }


    if (SessionManager.isSelectedPizza(this)!!) {
      findViewById<CheckBox>(R.id.pizza).isChecked = true
      SessionManager.setSelectedPizza(this, true)
    }
    if (SessionManager.isSelectedDrink(this)!!) {
      findViewById<CheckBox>(R.id.drink).isChecked = true
      SessionManager.setSelectedDrink(this, true)
    }
    if (SessionManager.isDisplayDish(this)!!) {
      findViewById<CheckBox>(R.id.dish_display).isChecked = true
      SessionManager.setSelectedDrink(this, true)
    }
    if (SessionManager.isDisplayAllProduct(this)!!) {
      findViewById<CheckBox>(R.id.all_product).isChecked = true
      SessionManager.setDisplayAllProduct(this, true)
    }

    if (!SessionManager.isSelectedPizza(this)!! && !SessionManager.isSelectedDrink(this)!! && !SessionManager.isDisplayDish(
        this
      )!!
    ) {
      findViewById<CheckBox>(R.id.all_product).isChecked = true
      SessionManager.setDisplayAllProduct(this, true)
    }


    device_spnr!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {

      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
          SessionManager.setDevice_id(this@SettingFragment, "")
        } else {
          if (deviceItems.get(position).device_id != null)
            SessionManager.setDevice_id(
              this@SettingFragment,
              deviceItems.get(position).device_id.toString()
            )
        }
      }
    }
    order_display_grp!!.setOnCheckedChangeListener { radioGroup, i ->
      when (i) {
        R.id.inshop_order_only -> {
          SessionManager.setInShopOrder(this, true)
          SessionManager.setOnlineOrder(this, false)
          SessionManager.setAllOrder(this, false)
        }

        R.id.online_order_only -> {
          SessionManager.setOnlineOrder(this, true)
          SessionManager.setAllOrder(this, false)
          SessionManager.setInShopOrder(this, false)
        }

        R.id.all -> {
          SessionManager.setAllOrder(this, true)
          SessionManager.setInShopOrder(this, true)
          SessionManager.setOnlineOrder(this, true)
        }
      }
    }




    if (SessionManager.isAutomaticTimer(this)) {
      findViewById<RadioButton>(R.id.manual_timer).isChecked = true
    }
    if (!SessionManager.isAutomaticTimer(this)) {
      findViewById<RadioButton>(R.id.automatic_timer).isChecked = true
    }

    order_cooking_timer_grp!!.setOnCheckedChangeListener { radioGroup, i ->
      when (i) {
        com.zotto.kds.R.id.manual_timer -> {
          SessionManager.setAutomaticTimer(this, true)
        }

        com.zotto.kds.R.id.automatic_timer -> {
          SessionManager.setAutomaticTimer(this, false)
        }
      }
    }


    if (SessionManager.isRobotoAble(this)) {
      findViewById<RadioButton>(R.id.robot_able).isChecked = true
      SessionManager.setRobotoAble(this, true)
    }
    if (!SessionManager.isRobotoAble(this)) {
      findViewById<RadioButton>(R.id.robot_unable).isChecked = true
      SessionManager.setRobotoAble(this, false)
    }
    robot_delivery_grp!!.setOnCheckedChangeListener { radioGroup, i ->
      when (i) {
        R.id.robot_able -> {
          SessionManager.setRobotoAble(this, true)
        }

        R.id.robot_unable -> {
          SessionManager.setRobotoAble(this, false)
        }
      }
    }

    if (SessionManager.isAutoPrint(this)) {
      findViewById<RadioButton>(R.id.auto_print).isChecked = true
      SessionManager.setPrint(this, false)
      SessionManager.setAutoPrint(this, true)
    }
    if (SessionManager.isPrint(this)) {
      findViewById<RadioButton>(R.id.manual_print).isChecked = true
      SessionManager.setPrint(this, true)
      SessionManager.setAutoPrint(this, false)
    }
    if (!SessionManager.isAutoPrint(this) && !SessionManager.isPrint(this)) {
      findViewById<RadioButton>(R.id.auto_print).isChecked = true
      SessionManager.setAutoPrint(this, true)
    }
    printing_grp!!.setOnCheckedChangeListener { radioGroup, i ->
      when (i) {
        R.id.auto_print -> {
          SessionManager.setPrint(this, false)
          SessionManager.setAutoPrint(this, true)
        }

        R.id.manual_print -> {
          SessionManager.setPrint(this, true)
          SessionManager.setAutoPrint(this, false)
        }

        R.id.no_print -> {
          SessionManager.setAutoPrint(this, false)
          SessionManager.setPrint(this, false)
        }
      }
    }

    findViewById<ImageView>(R.id.back_btn).setOnClickListener {
      // startActivity(Intent(this,MainActivity::class.java))
      finish()
    }
    findViewById<AppCompatButton>(R.id.close_btn).setOnClickListener {
      // startActivity(Intent(this,MainActivity::class.java))
      finish()
    }
    findViewById<AppCompatButton>(R.id.save_btn).setOnClickListener {
      if (findViewById<CheckBox>(R.id.dish_display).isChecked) {
        SessionManager.setDisplayDish(this, true)
      } else {
        SessionManager.setDisplayDish(this, false)
      }
      if (findViewById<CheckBox>(R.id.pizza).isChecked) {
        SessionManager.setSelectedPizza(this, true)
      } else {
        SessionManager.setSelectedPizza(this, false)
      }
      if (findViewById<CheckBox>(R.id.drink).isChecked) {
        SessionManager.setSelectedDrink(this, true)
      } else {
        SessionManager.setSelectedDrink(this, false)
      }
      if (findViewById<CheckBox>(R.id.all_product).isChecked) {
        SessionManager.setDisplayAllProduct(this, true)
      } else {
        SessionManager.setDisplayAllProduct(this, false)
      }
      startActivity(Intent(this, MainActivity::class.java))
      // finish()
    }
    getDeviceList()
  }

  override fun onDestroy() {
    super.onDestroy()

  }

  fun getDeviceList() {
    Log.e("getDeviceList", "  run now")

    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(
      apiServices.getDeviceList(
        SessionManager.getToken(this),
//        "30692730"
        SessionManager.getRestaurantId(this)
      )
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe({ response ->
          onResponse(response)
          Log.e("jhg", response.toString())
        },
          { t -> onFailure(t) })
    )

  }

  private fun onResponse(response: GenericResponse<List<DeviceTable>>) {
    var defaultItems: MutableList<String> = ArrayList()
    val gson = Gson()
    val listType = object : TypeToken<ArrayList<String>>() {}.type
    var alreadySelected: String? = SessionManager.getDeviceName(this@SettingFragment)
    if(alreadySelected!!.isNotEmpty()){
      val iItemList: ArrayList<String> = gson.fromJson(alreadySelected,listType)
      Log.e("bgxdfg",iItemList.toString())
      defaultItems = iItemList
    }
    deviceItems = ArrayList()
    Log.e("DeviceModel onResponse", "${response}")
    try {
      if (response.getStatus().equals("200")) {
        Log.e("DeviceModel Responce== ", "" + response.getData().toString())
        if (response.getData()!!.size > 0) {
          deviceItems = response.getData()!!

          deviceItems= deviceItems.filter { it -> it.device_name != null && !it.device_name!!.toLowerCase().contains("kds") }

          device_spnr!!.setItems(
            deviceItems,
            defaultItems,
            "Select Device"
          ) { selected ->
            val slectedIps: MutableList<String> = ArrayList()
            val slectedPorts: MutableList<String> = ArrayList()
            val slectedNames: MutableList<String> = ArrayList()
            for (i in selected.indices) {
              if (selected[i] && deviceItems.get(i).port != null) {
                slectedIps.add(deviceItems.get(i).ip_address!!)
                slectedPorts.add(deviceItems.get(i).port!!)
                slectedNames.add(deviceItems.get(i).device_name!!)
              }
            }
            val gson = Gson()

            //                Log.e("ips", "" + gson.toJson(slectedIps))
            SessionManager.setDeviceName(this@SettingFragment, gson.toJson(slectedNames))
            //                SessionManager.setSelectedPort(this@SettingFragment, gson.toJson(slectedPorts))

            var ipAddress = gson.fromJson(
              getSelectedIp(this@SettingFragment),
              java.util.ArrayList::class.java
            )


            Log.e("ips", "" + ipAddress)
            print(ipAddress)
          }

//          device_spnr?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//            override fun onItemSelected(
//              parent: AdapterView<*>?,
//              view: View?,
//              position: Int,
//              id: Long
//            ) {
//              Log.e("test", "" + position)
//              Log.e("test", "" + deviceItems[position])
//              SessionManager.setSelectedIp(this@SettingFragment, deviceItems[position].ip_address)
//              SessionManager.setSelectedPort(this@SettingFragment, deviceItems[position].port)
//            }
//          }

        }
      }

    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun onFailure(t: Throwable?) {
    Log.e("onFailure Device", "${t!!.message}")
  }
}