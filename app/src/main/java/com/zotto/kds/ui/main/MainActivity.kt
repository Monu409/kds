package com.zotto.kds.ui.main

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.zotto.kds.R
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.ProductDao
import com.zotto.kds.databinding.NavigationDrawerLayoutBinding
import com.zotto.kds.localIP.ChatClient
import com.zotto.kds.localIP.ChatServer
//import com.zotto.kds.localIP.ChatServer
import com.zotto.kds.printing.HPRTPrinterPrinting
import com.zotto.kds.rabbitmq.RabbitmqService
import com.zotto.kds.repository.MainReprository
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.ui.completedorders.CompletedOrders
import com.zotto.kds.ui.home.HomeFragment
import com.zotto.kds.ui.itemmanagement.ItemManagement
import com.zotto.kds.ui.language.LanguageActiviity
import com.zotto.kds.ui.settings.SettingFragment
import com.zotto.kds.utils.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var binding: NavigationDrawerLayoutBinding? = null
    private var mainViewModel: MainViewModel? = null
    private var apiServices: ApiServices? = null
    private var appDatabase: AppDatabase? = null
    private var orderDao: OrderDao? = null
    private var productDao: ProductDao? = null
    private var isAllFabsVisible = false
    private var fabOpen: Animation? = null
    private var fabClose: Animation? = null
    private var rotateForward: Animation? = null
    private var rotateBackward: Animation? = null
    private var isOpen = false
    private var chatServer: ChatServer? = null

    companion object {
        var activeOrders: TextView? = null
        var completedOrders: TextView? = null
        var rootfragment: FrameLayout? = null
        var refreshFragment: Boolean = false
        private var drawerLayout: DrawerLayout? = null
        private var hamburger_menu: ImageView? = null
        private var isDrawerOpened = false
        private var navView: NavigationView? = null
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Local ip")
        builder.setMessage(Utils.getIPAddress(true))
        Log.e("dfg", Utils.getIPAddress(true))
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(
                applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(
                applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(
                applicationContext,
                "Maybe", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.setLocale(this, SessionManager.getLanguage(this))
        binding =
            DataBindingUtil.setContentView(this, com.zotto.kds.R.layout.navigation_drawer_layout)
        RetroClient.setupRestClient()
        FirebaseAnalytics.getInstance(this)
        chatServer = ChatServer(
            Utils.getIPAddress(true),
            this,
            this,
            8383,
            SessionManager.getSelectedIp(this@MainActivity)
        )
        chatServer!!.start()
        Log.e("Server status", "Server status")
        FirebaseCrashlytics.getInstance()
        FirebaseApp.getInstance()
        Singleton.fragmentManager = supportFragmentManager
        appDatabase = DatabaseClient.getInstance(this)!!.getAppDatabase()
        orderDao = appDatabase!!.orderDao()
        productDao = appDatabase!!.productDao()
        orderDao!!.deleteAllActiveOrder()
        productDao!!.deleteAllProduct()
        apiServices = RetroClient.getApiService()
        showDialog()
        val mainReprository = MainReprository(this, orderDao!!, apiServices!!)
        mainViewModel =
            ViewModelProvider(
                this,
                MainModelFactory(mainReprository)
            ).get(MainViewModel::class.java)
        binding!!.mainviewmodel = mainViewModel
        binding!!.lifecycleOwner = this
        hamburger_menu = binding!!.appBarMain.hamburgerMenu
        fabOpen = AnimationUtils.loadAnimation(this, com.zotto.kds.R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(this, com.zotto.kds.R.anim.fab_close)
        rotateForward = AnimationUtils.loadAnimation(this, com.zotto.kds.R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(this, com.zotto.kds.R.anim.rotate_backward)

        binding!!.appBarMain.refreshTxt.setOnClickListener{
//            finish();
//            startActivity(intent);
            supportFragmentManager
                .beginTransaction()
                .replace(
                    com.zotto.kds.R.id.root_container,
                    HomeFragment(),
                    HomeFragment::class.java.simpleName
                )
                .commit()
        }

        binding!!.appBarMain.robotoFab.visibility = View.GONE
        binding!!.appBarMain.waiterFab.visibility = View.GONE
        binding!!.appBarMain.chargingFab.visibility = View.GONE

        binding!!.appBarMain.goBackTxt.visibility = View.GONE
        binding!!.appBarMain.robotoTxt.visibility = View.GONE
        binding!!.appBarMain.chargingTxt.visibility = View.GONE
        binding!!.appBarMain.waiterTxt.visibility = View.GONE
        var hprtPrinterPrinting = HPRTPrinterPrinting(this)
        hprtPrinterPrinting.printerPermission(this)
        hprtPrinterPrinting.openUSBPrintingPort()
        binding!!.appBarMain.fab.setOnClickListener { view ->
            isAllFabsVisible = !isAllFabsVisible
            if (isAllFabsVisible) {
                binding!!.appBarMain.robotoFab.show()
                binding!!.appBarMain.waiterFab.show()
                binding!!.appBarMain.chargingFab.show()
                binding!!.appBarMain.robotoFab.visibility = View.VISIBLE
                binding!!.appBarMain.waiterFab.visibility = View.VISIBLE
                binding!!.appBarMain.chargingFab.visibility = View.VISIBLE

                binding!!.appBarMain.goBackTxt.visibility = View.VISIBLE
                binding!!.appBarMain.robotoTxt.visibility = View.VISIBLE
                binding!!.appBarMain.chargingTxt.visibility = View.VISIBLE
                binding!!.appBarMain.waiterTxt.visibility = View.VISIBLE
            } else {
                binding!!.appBarMain.robotoFab.hide()
                binding!!.appBarMain.waiterFab.hide()
                binding!!.appBarMain.chargingFab.hide()
                binding!!.appBarMain.robotoFab.visibility = View.GONE
                binding!!.appBarMain.waiterFab.visibility = View.GONE
                binding!!.appBarMain.chargingFab.visibility = View.GONE

                binding!!.appBarMain.goBackTxt.visibility = View.GONE
                binding!!.appBarMain.robotoTxt.visibility = View.GONE
                binding!!.appBarMain.chargingTxt.visibility = View.GONE
                binding!!.appBarMain.waiterTxt.visibility = View.GONE
            }
        }
        binding!!.appBarMain.robotoFab.setOnClickListener { view ->
            if (SessionManager.isRobotoAble(this)) {
                Utility.robotLog("callrobot=" + SessionManager.getRestaurantId(this))
                mainReprository.rabbitRequest("callrobot", SessionManager.getRestaurantId(this), "")
            }
        }
        binding!!.appBarMain.chargingFab.setOnClickListener { view ->
            if (SessionManager.isRobotoAble(this)) {
                Utility.robotLog("chargerobot=" + SessionManager.getRestaurantId(this))
                mainReprository.rabbitRequest(
                    "chargerobot",
                    SessionManager.getRestaurantId(this),
                    ""
                )
            }
        }
        binding!!.appBarMain.waiterFab.setOnClickListener { view ->

        }
        activeOrders = binding!!.appBarMain.activeOrders
        completedOrders = binding!!.appBarMain.completedOrders
        Singleton.isactiveclicked = false
        binding!!.appBarMain.activeOrders.setOnClickListener {
            Singleton.ordertype = "active"
            Singleton.isactiveclicked = true

            supportFragmentManager
                .beginTransaction()
                .replace(
                    com.zotto.kds.R.id.root_container,
                    HomeFragment(),
                    HomeFragment::class.java.simpleName
                )
                .commit()
        }
        binding!!.appBarMain.completedOrders.setOnClickListener {
            Singleton.ordertype = "completed"
            Singleton.isactiveclicked = false

            var intent = Intent(this, CompletedOrders::class.java)
            startActivity(intent)
            // finish()
        }

        /*  binding!!.appBarMain.summary.setOnClickListener {
              rootfragment!!.visibility=View.VISIBLE
              supportFragmentManager!!.beginTransaction()
                  .replace(R.id.summary_container, SummaryDailog())
                  .addToBackStack("SummaryDailog")
                  .commit()
          }*/
        if (isServiceClosed(RabbitmqService::class.java)) {
            ContextCompat.startForegroundService(this, Intent(this, RabbitmqService::class.java))
        }


        drawerLayout = binding!!.drawerLayout
        hamburger_menu!!.setOnClickListener {
            isDrawerOpened = !isDrawerOpened
            if (isDrawerOpened) {
                drawerLayout!!.openDrawer(GravityCompat.START)
            } else {
                drawerLayout!!.closeDrawers()
            }
        }

        navView = binding!!.navView

        navView!!.findViewById<RelativeLayout>(com.zotto.kds.R.id.logout).setOnClickListener {
            showPopup()
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            null,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        navView!!.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.nav_item -> {
                    drawerLayout!!.closeDrawers()
                    startActivity(Intent(this, ItemManagement::class.java))
                }

                R.id.nav_setting -> {
                    drawerLayout!!.closeDrawers()
                    startActivity(Intent(this, SettingFragment::class.java))
                }

//                R.id.nav_chat -> {
//                    drawerLayout!!.closeDrawers()
//                    startActivity(Intent(this, ChatClient::class.java))
//                }
            }
            if (menuItem.isChecked) {
                menuItem.isChecked = false
            } else {
                menuItem.isChecked = true
            }
            menuItem.isChecked = true
            true
        })

        /*supportFragmentManager!!.beginTransaction()
            .replace(R.id.summary_container, SummaryDailog())
            .addToBackStack("SummaryDailog")
            .commit()*/
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_container, HomeFragment(), HomeFragment::class.java.simpleName)
            .addToBackStack("homefragment")
            .commit()

        Utility.robotLog("start log......")
    }

    private fun animateFab() {
        if (isOpen) {
            binding!!.appBarMain.fab.startAnimation(rotateForward)
            binding!!.appBarMain.robotoFab.startAnimation(fabClose)
            binding!!.appBarMain.waiterFab.startAnimation(fabClose)
            binding!!.appBarMain.chargingFab.startAnimation(fabClose)
            binding!!.appBarMain.robotoFab.setClickable(false)
            binding!!.appBarMain.waiterFab.setClickable(false)
            binding!!.appBarMain.waiterFab.setClickable(false)
            binding!!.appBarMain.waiterFab.setClickable(false)
            isOpen = false
        } else {
            binding!!.appBarMain.fab.startAnimation(rotateBackward)
            binding!!.appBarMain.robotoFab.startAnimation(fabOpen)
            binding!!.appBarMain.waiterFab.startAnimation(fabOpen)
            binding!!.appBarMain.chargingFab.startAnimation(fabOpen)
            binding!!.appBarMain.robotoFab.setClickable(true)
            binding!!.appBarMain.waiterFab.setClickable(true)
            binding!!.appBarMain.waiterFab.setClickable(true)
            binding!!.appBarMain.waiterFab.setClickable(true)
            isOpen = true
        }
    }

    private fun showPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.zotto.kds.R.layout.logout_dialog)
        dialog.window!!.setGravity(Gravity.CENTER)
        val tit =
            dialog.findViewById<View>(com.zotto.kds.R.id.order_item_add_delete_edit_title) as TextView
        val dess =
            dialog.findViewById<View>(com.zotto.kds.R.id.order_item_add_delete_edit_desc) as TextView
        val img =
            dialog.findViewById<View>(com.zotto.kds.R.id.order_item_add_delete_edit_image) as ImageView

        tit.text = resources.getString(com.zotto.kds.R.string.logout_txt)
        dess.setText(resources.getString(com.zotto.kds.R.string.logout_msg))
        img.setBackgroundResource(com.zotto.kds.R.drawable.logout)

        val okay =
            dialog.findViewById<View>(com.zotto.kds.R.id.order_item_add_delete_edit_okay) as Button
        okay.setText(resources.getString(com.zotto.kds.R.string.logout_txt))
        val cencle =
            dialog.findViewById<View>(com.zotto.kds.R.id.order_item_add_delete_edit_cancle) as Button
        okay.setOnClickListener {
            SessionManager.clearpreferences(this)
            orderDao!!.deleteAllOrder()
            productDao!!.deleteAllProduct()
            val intent1 = Intent(this, LanguageActiviity::class.java)
            startActivity(intent1)
            finish()
            dialog.dismiss()
        }
        cencle.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, RabbitmqService::class.java))
    }

    private fun isServiceClosed(serviceClass: Class<*>): Boolean {
        val manager = (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        for (service in Objects.requireNonNull(manager).getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) return false
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
    }
}