package com.app.hotelsys.models

data class ReservaRequest(
    val clienteId: Int,
    val fechaCheckIn: String,
    val fechaCheckOut: String,
    val descuento: String,
    val estadoReservaId: Int,
    val habitacionIds: List<Int>,
    val productos: List<ProductoReservaRequest>
)