package com.app.hotelsys.api

import com.app.hotelsys.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "${BuildConfig.BASE_IP}/api/"

    val instance: HabitacionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HabitacionApiService::class.java)
    }
}
