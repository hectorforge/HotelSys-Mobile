package com.app.hotelsys.repository

import com.app.hotelsys.api.ReservarApiService
import com.app.hotelsys.models.ReservaRequest
import com.app.hotelsys.retrofit.RetrofitClient
import retrofit2.Retrofit

class ReservaRepository {

    private val api = RetrofitClient.instance.create(ReservarApiService::class.java)

    suspend fun obtenerClientePorCorreo(correo: String) = api.obtenerClientePorCorreo(correo)

    suspend fun obtenerProductos() = api.obtenerProductos()

    suspend fun crearReserva(reserva: ReservaRequest) = api.crearReserva(reserva)

}