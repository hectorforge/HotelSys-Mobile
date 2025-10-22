package com.app.hotelsys.api

import com.app.hotelsys.models.HabitacionResponse
import retrofit2.Call
import retrofit2.http.GET

interface HabitacionApiService {
    @GET("/api/habitaciones")
    fun getHabitaciones(): Call<List<HabitacionResponse>>
}
