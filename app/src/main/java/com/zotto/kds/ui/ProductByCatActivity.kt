package com.zotto.kds.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zotto.kds.R
import com.zotto.kds.database.table.DeviceTable
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.restapi.RetroClient
import com.zotto.kds.utils.SessionManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.Gson
import com.zotto.kds.adapter.ProdByCatAdapter
import com.zotto.kds.adapter.ProdByCatAdapterOffline
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.m_table.MProduct
import com.zotto.kds.model.OnllineProduct
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProductByCatActivity: AppCompatActivity() {
    lateinit var apiServices: ApiServices
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prod_by_cat_activity)
        apiServices = RetroClient.getApiService()!!
        var intent = intent
        var mName = intent.getStringExtra("Username")
        var catId = intent.getStringExtra("cat_id")
        Log.e("name is",mName!!)
        Log.e("catId is",catId!!)
        var prodCatRcylr = findViewById<RecyclerView>(R.id.prod_cat_rcylr)
        var backImg = findViewById<ImageView>(R.id.back_btn)
        backImg.setOnClickListener {
            onBackPressed()
        }
        prodCatRcylr.layoutManager = LinearLayoutManager(this)
        Log.e("test",SessionManager.getSelectedPort(this)?.isEmpty().toString())
        var selectPort = SessionManager.getSelectedPort(this)
        var selectIp = SessionManager.getSelectedIp(this)
        if(selectPort.equals("")){
            selectPort = "0"
        }
        if(isOnline(this)){
            GetWeatherTask(catId,prodCatRcylr,selectPort!!.toInt(),selectIp!!).execute()
        }
        else{
            var database =
                Room.databaseBuilder(this@ProductByCatActivity, AppDatabase::class.java, "zotto_kds")
                    .addMigrations(AppDatabase.MIGRATION)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            val apiResponseDao = database.mProductDao()
            runBlocking {
                var mProductByCat = apiResponseDao.getProductById(catId)
//                var mProductByCat = apiResponseDao.getAllProducts()
                Log.e("producttttttrgege",""+mProductByCat)
                var mAdapter = ProdByCatAdapterOffline(mProductByCat as List<MProduct>, this@ProductByCatActivity,selectPort!!.toInt(),selectIp!!)
                prodCatRcylr?.adapter = mAdapter
            }
        }
    }

    inner class GetWeatherTask(catId: String, mRcylr: RecyclerView,port: Int,ip: String) : AsyncTask<Unit, Unit, String>() {
        val innerCatId: String = catId
        val mPort: Int = port
        val mIp: String = ip
        val localRcylr: RecyclerView? = mRcylr
        override fun doInBackground(vararg params: Unit?): String? {
            var restaurantId = SessionManager.getRestaurantId(this@ProductByCatActivity)
            val url = URL("https://demopay.z-pay.co.uk/api/getProductList/${restaurantId}/${innerCatId}")
            val httpClient = url.openConnection() as HttpURLConnection
            if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    val stream = BufferedInputStream(httpClient.inputStream)
                    val data: String = readStream(inputStream = stream)
                    return data
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    httpClient.disconnect()
                }
            } else {
                println("ERROR ${httpClient.responseCode}")
            }
            return null
        }

        fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }

        @JsonIgnore
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Log.e("dsff",result!!)
            var gson = Gson()
            var fullModelFromJson = gson.fromJson(result, OnllineProduct::class.java)
            Log.e("dsff",fullModelFromJson.toString())
            var mAdapter = ProdByCatAdapter(fullModelFromJson.data, this@ProductByCatActivity,mPort,mIp)
            localRcylr?.adapter = mAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}