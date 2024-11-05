package com.zotto.kds.ui

import android.content.Intent
import com.zotto.kds.database.m_table.ApiResponse
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.R
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.m_table.MCategoryData
import com.zotto.kds.database.m_table.MProduct
import com.zotto.kds.database.m_table.RootProductModel
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SyncDataActivity: AppCompatActivity() {
    private lateinit var progressBarIndeterminate: ProgressBar
    private lateinit var progressBarDeterminate: ProgressBar
    private lateinit var startButton: Button

    private fun startIndeterminateProgress() {
        progressBarIndeterminate.visibility = ProgressBar.VISIBLE

        // Simulate some work with a delay
        Handler(Looper.getMainLooper()).postDelayed({
            progressBarIndeterminate.visibility = ProgressBar.GONE
        }, 100000) // Delay for 3 seconds
    }

    private fun startDeterminateProgress() {
        progressBarDeterminate.visibility = ProgressBar.VISIBLE
        progressBarDeterminate.progress = 0

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (progressBarDeterminate.progress < 100) {
                    progressBarDeterminate.progress += 1
                    handler.postDelayed(this, 5000) // Delay for 0.5 second
                } else {
                    progressBarDeterminate.visibility = ProgressBar.GONE
                }
            }
        }
        handler.post(runnable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_data)
        progressBarIndeterminate = findViewById(R.id.progressBarIndeterminate)
        progressBarDeterminate = findViewById(R.id.progressBarDeterminate)
        syncData()
        startIndeterminateProgress()
        startDeterminateProgress()
    }

    private fun syncData(){
        GetCategoryData().execute()
    }

    inner class GetCategoryData() : AsyncTask<Unit, Unit, String>() {
        override fun doInBackground(vararg params: Unit?): String? {
            var restaurantId = SessionManager.getRestaurantId(this@SyncDataActivity)
            val url = URL("https://demopay.z-pay.co.uk/api/getCategoryList/${restaurantId}")
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
            checkMate(result)
        }
    }
    inner class GetProductData(catId: String) : AsyncTask<Unit, Unit, String>() {
        val mCatId: String = catId
        override fun doInBackground(vararg params: Unit?): String? {
            var restaurantId = SessionManager.getRestaurantId(this@SyncDataActivity)
            val url = URL("https://demopay.z-pay.co.uk/api/getProductList/${restaurantId}/${mCatId}")
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
            Log.e("productdatatttttt",result!!)
            val apiResponse = Gson().fromJson(result, RootProductModel::class.java)
            Log.e("productdatatttttt",apiResponse.toString())
            try {
                for (x in apiResponse.data) {
                    val jsonString = Gson().toJson(x)
                    saveProductData(jsonString)
                }
            }
            catch (exp: java.lang.Exception){
                Log.e("java.lang.Exception",""+exp)
            }

        }
    }


    fun checkMate(json: String) {
        var database =
            Room.databaseBuilder(this@SyncDataActivity, AppDatabase::class.java, "zotto_kds")
                .addMigrations(AppDatabase.MIGRATION)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        val apiResponseDao = database.mCategoryDao()

        val gson = Gson()
        val apiResponse = gson.fromJson(json, ApiResponse::class.java)

        val apiResponseEntity = MCategoryData(
            status = apiResponse.status,
            data = apiResponse.data
        )

        runBlocking {
            apiResponseDao?.insertApiResponse(apiResponseEntity)
            runBlocking {
                val fetchedApiResponseEntity = apiResponseDao!!.getApiResponse(apiResponseEntity.id)
                Log.e("testttt", fetchedApiResponseEntity.data[0].category_id)
                for (i in 1 until fetchedApiResponseEntity.data.size) {
                    Log.e("all id",fetchedApiResponseEntity.data[i].category_id)
                    GetProductData(fetchedApiResponseEntity.data[i].category_id).execute()
                }
                val intent = Intent(this@SyncDataActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }


    fun saveProductData(jsonData: String) {
        val gson = Gson()
        val productType = object : TypeToken<MProduct>() {}.type
        val product = gson.fromJson<MProduct>(jsonData, productType)

        val db = Room.databaseBuilder(
            this@SyncDataActivity,
            AppDatabase::class.java, "zotto_kds"
        ).build()

        val productDao = db.mProductDao()

        runBlocking {
            productDao.insert(product)
        }
    }

}