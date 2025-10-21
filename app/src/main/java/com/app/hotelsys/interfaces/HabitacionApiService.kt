package com.app.hotelsys.interfaces

import com.app.hotelsys.models.HabitacionResponse
import retrofit2.Call
import retrofit2.http.GET

interface HabitacionApiService {
    @GET("habitaciones")
    fun getHabitaciones(): Call<List<HabitacionResponse>>
}
