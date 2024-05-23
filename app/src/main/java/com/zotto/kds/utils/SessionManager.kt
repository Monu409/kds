package com.zotto.kds.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager {
  constructor()

  companion object {
    private val TAG: String = SessionManager::class.java.getSimpleName()
    private var VM_preference: SharedPreferences? = null
    private var _context: Context? = null
    private val KEY_DEFAULT_PREFERENCE = "com.zotto.kds"
    private val ISLOGIN = "islogin"
    private val TOKEN = "token"
    private val COMPANY_LOGO = "companylogo"
    private var editor: SharedPreferences.Editor? = null
    private var sessionManager: SessionManager? = null
    private var RESTAURANTID = "restaurantid"
    private var ISINSHOPORDER = "isinshoporder"
    private var ISONLINERDER = "isonlineorder"
    private var ISALLORDER = "isallorder"
    private var PRODUCTTYPE = "producttype"
    private var ISAUTOMATICTIMER = "isautomatictimer"
    private var ISROBOTABLE = "isrobotable"
    private var ISAUTOPRINT = "isautoprint"
    private var ISPRINT = "isprint"
    private val LANGUAGE = "language"
    private val ISCHANGELANGUAGE = "islanguage"
    private val ISSELECTEDPIZZA = "isselectedpizza"
    private val ISSELECTEDDRINK = "isselecteddrink"
    private val ISDISPLAYDISH = "isdisplaydish"
    private val ISDISPLAYALLPRODUCT = "isdisplayallproduct"
    private val DEVICE_ID = "device_id"
    private val SELECTED_IP = "selected_ip"
    private val SELECTED_PORT = "selected_port"
    private val SELECTED_DEVICE = "selected_device"

    fun getToken(context: Context?): String? {
      return getDefaultPref(context!!)!!.getString(TOKEN, "")
    }

    fun setToken(context: Context?, token: String?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(TOKEN, token)
      editor.commit()
    }

    fun isDisplayAllProduct(context: Context?): Boolean? {
      return getDefaultPref(context!!)!!.getBoolean(ISDISPLAYALLPRODUCT, false)
    }

    fun setDisplayAllProduct(context: Context?, isdisplayallproduct: Boolean?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISDISPLAYALLPRODUCT, isdisplayallproduct!!)
      editor.commit()
    }

    fun isDisplayDish(context: Context?): Boolean? {
      return getDefaultPref(context!!)!!.getBoolean(ISDISPLAYDISH, false)
    }

    fun setDisplayDish(context: Context?, isdisplaydish: Boolean?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISDISPLAYDISH, isdisplaydish!!)
      editor.commit()
    }

    fun isSelectedPizza(context: Context?): Boolean? {
      return getDefaultPref(context!!)!!.getBoolean(ISSELECTEDPIZZA, false)
    }

    fun setSelectedPizza(context: Context?, ispizza: Boolean?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISSELECTEDPIZZA, ispizza!!)
      editor.commit()
    }

    fun isSelectedDrink(context: Context?): Boolean? {
      return getDefaultPref(context!!)!!.getBoolean(ISSELECTEDDRINK, false)
    }

    fun setSelectedDrink(context: Context?, isdrink: Boolean?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISSELECTEDDRINK, isdrink!!)
      editor.commit()
    }

    fun getChangeLanguage(context: Context?): Boolean? {
      return getDefaultPref(context!!)!!.getBoolean(ISCHANGELANGUAGE, false)
    }

    fun setChangeLanguage(context: Context?, laguage: Boolean?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISCHANGELANGUAGE, laguage!!)
      editor.commit()
    }

    fun getLanguage(context: Context?): String? {
      return getDefaultPref(context!!)!!.getString(LANGUAGE, "")
    }

    fun setLanguage(context: Context?, laguage: String?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(LANGUAGE, laguage)
      editor.commit()
    }


    @JvmStatic  fun getSelectedIp(context: Context?): String? {
      return getDefaultPref(context!!)!!.getString(SELECTED_IP, "")
    }

    @JvmStatic  fun setSelectedIp(context: Context?, laguage: String?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(SELECTED_IP, laguage)
      editor.commit()
    }

    @JvmStatic  fun getDeviceName(context: Context?): String? {
      return getDefaultPref(context!!)!!.getString(SELECTED_DEVICE, "")
    }

    @JvmStatic  fun setDeviceName(context: Context?, laguage: String?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(SELECTED_DEVICE, laguage)
      editor.commit()
    }

    @JvmStatic  fun getSelectedPort(context: Context?): String? {
      return getDefaultPref(context!!)!!.getString(SELECTED_PORT, "")
    }

    @JvmStatic  fun setSelectedPort(context: Context?, laguage: String?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(SELECTED_PORT, laguage)
      editor.commit()
    }
    fun getDevice_id(context: Context?): String? {
      return getDefaultPref(context!!)!!.getString(DEVICE_ID, "")
    }

    fun setDevice_id(context: Context?, device_id: String?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(DEVICE_ID, device_id)
      editor.commit()
    }
    fun isPrint(context: Context): Boolean {
      return getDefaultPref(context!!)!!.getBoolean(ISPRINT, false)!!
    }

    fun setPrint(context: Context, isprint: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISPRINT, isprint)
      editor.commit()
    }

    fun isAutoPrint(context: Context): Boolean {
      return getDefaultPref(context!!)!!.getBoolean(ISAUTOPRINT, false)!!
    }

    fun setAutoPrint(context: Context, isautoprint: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISAUTOPRINT, isautoprint)
      editor.commit()
    }

    fun isRobotoAble(context: Context): Boolean {
      return getDefaultPref(context!!)!!.getBoolean(ISROBOTABLE, false)!!
    }

    fun setRobotoAble(context: Context, isrobotoable: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISROBOTABLE, isrobotoable)
      editor.commit()
    }

    fun isAutomaticTimer(context: Context): Boolean {
      return getDefaultPref(context!!)!!.getBoolean(ISAUTOMATICTIMER, false)!!
    }

    fun setAutomaticTimer(context: Context, isautomatictimer: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISAUTOMATICTIMER, isautomatictimer)
      editor.commit()
    }

    fun isAllOrder(context: Context): Boolean {
      return getDefaultPref(context!!)!!.getBoolean(ISALLORDER, false)!!
    }

    fun setAllOrder(context: Context, isonlineorder: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISALLORDER, isonlineorder)
      editor.commit()
    }

    fun isOnlineOrder(context: Context): Boolean {
      return getDefaultPref(context!!)!!.getBoolean(ISONLINERDER, false)!!
    }

    fun setOnlineOrder(context: Context, isonlineorder: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISONLINERDER, isonlineorder)
      editor.commit()
    }

    fun isInShopOrder(context: Context): Boolean {
      return getDefaultPref(context!!)!!.getBoolean(ISINSHOPORDER, false)!!
    }

    fun setInShopOrder(context: Context, inshopordering: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putBoolean(ISINSHOPORDER, inshopordering)
      editor.commit()
    }

    fun getProductType(context: Context): String {
      return getDefaultPref(context!!)!!.getString(PRODUCTTYPE, "")!!
    }

    fun setProductType(context: Context, productType: String) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(PRODUCTTYPE, productType)
      editor.commit()
    }

      @JvmStatic fun getRestaurantId(context: Context): String {
      return getDefaultPref(context!!)!!.getString(RESTAURANTID, "")!!
    }

    fun setRestaurantId(context: Context, restaurantid: String) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.putString(RESTAURANTID, restaurantid)
      editor.commit()
    }

    fun getDefaultPref(context: Context): SharedPreferences? {
      if (VM_preference == null) {
        VM_preference = context.getSharedPreferences(
          KEY_DEFAULT_PREFERENCE, Context.MODE_PRIVATE
        )
      }
      return VM_preference
    }

    fun clearpreferences(context: Context?) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor.clear()
      editor.commit()
    }

    fun setLogin(context: Context?, isLoggedIn: Boolean) {
      val editor: SharedPreferences.Editor = getDefaultPref(context!!)!!.edit()
      editor!!.putBoolean(ISLOGIN, isLoggedIn)
      editor!!.commit()

    }

    fun isLoggedIn(context: Context?): Boolean {
      VM_preference = context!!.getSharedPreferences(
        KEY_DEFAULT_PREFERENCE, Context.MODE_PRIVATE
      )
      if (VM_preference != null) {
        return VM_preference!!.getBoolean(
          ISLOGIN,
          false
        )
      } else {
        return false
      }
    }

    fun SessionManager(context: Context): SessionManager {
      _context = context
      VM_preference = _context!!.getSharedPreferences(
        KEY_DEFAULT_PREFERENCE,
        Context.MODE_PRIVATE
      )
      editor = VM_preference!!.edit()
      sessionManager = SessionManager(context)
      return sessionManager!!
    }
  }


}