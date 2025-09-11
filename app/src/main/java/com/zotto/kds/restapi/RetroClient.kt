package com.zotto.kds.restapi

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetroClient {
    companion object{
        private val ROOT_TEST_URL = "https://paymentz.z-pay.co.uk/"
//        const val ROOT_LIVE_URL = "https://api.ciboapp.com/"
//        const val ROOT_LIVE_URL = "https://demopay.z-pay.co.uk/"
        const val ROOT_LIVE_URL = "https://api.opushospitalitymanager.com/"
        private var restAdapter: Retrofit? = null
        private  var apiService: ApiServices? = null
        fun setupRestClient() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
            okHttpClient.addInterceptor(logging)
            okHttpClient.addInterceptor { chain ->
                val request =
                    chain.request().newBuilder().addHeader("Accept", "application/json").build()
                chain.proceed(request)
            }
            restAdapter = Retrofit.Builder()
                .baseUrl(ROOT_LIVE_URL)
                .client(okHttpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
        }



        fun getApiService(): ApiServices? {
            if (apiService == null) {
                apiService = restAdapter!!.create(ApiServices::class.java)
            }
            return apiService
        }

    }
}