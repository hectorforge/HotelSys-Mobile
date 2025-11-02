package com.app.hotelsys.retrofit

import com.app.hotelsys.BuildConfig
import com.app.hotelsys.interfaces.ClienteApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "${BuildConfig.BASE_IP}/api/"

    val api: ClienteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClienteApiService::class.java)
    }
}