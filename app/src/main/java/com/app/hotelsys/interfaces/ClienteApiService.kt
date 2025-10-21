package com.app.hotelsys.interfaces

import com.app.hotelsys.models.ClienteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ClienteApiService {
    @POST("clientes")
    suspend fun createCliente(@Body request: ClienteRequest): Response<Void>
}
