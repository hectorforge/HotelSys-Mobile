package com.app.hotelsys.retrofit

import com.app.hotelsys.interfaces.ReservaApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitReserva {
    private const val BASE_URL = "http://10.0.2.2:8081/api/"

    val api: ReservaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReservaApiService::class.java)
    }
}
