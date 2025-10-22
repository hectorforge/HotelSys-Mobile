package com.app.hotelsys.interfaces

import com.app.hotelsys.models.ReservaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservaApiService {

    @GET("reservas/cliente")
    suspend fun getReservasPorEmail(
        @Query("email") email: String
    ): Response<List<ReservaResponse>>

    @PATCH("reservas/{idRes}/cancelar/{emailCli}")
    suspend fun cancelarReservaPorIdYEmail(
        @Path("idRes") idRes: Int,
        @Path("emailCli") emailCli: String
    ): Response<ReservaResponse>

}
