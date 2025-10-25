package com.app.hotelsys.api

import com.app.hotelsys.models.ClienteFiltradoResponse
import com.app.hotelsys.models.ProductoResponse
import com.app.hotelsys.models.ReservaRequest
import com.app.hotelsys.models.ReservaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReservarApiService {

    // TRAER ID DE CLIENTE
    @GET("clientes/email")
    suspend fun obtenerClientePorCorreo(
        @Query("email") correo: String
    ): Response<Int>

    //TRAER LISTA DE PRODUCTOS
    @GET("productos")
    suspend fun obtenerProductos(): Response<List<ProductoResponse>>

    //CREAR RESERVA
    @POST("reservas")
    suspend fun crearReserva(
        @Body reservaRequest: ReservaRequest
    ): Response<ReservaResponse>


}