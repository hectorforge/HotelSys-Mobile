package com.app.hotelsys.models

data class ClienteRequest (
    val nombreCompleto: String,
    val numeroDocumento: String,
    val email: String,
    val telefono: String,
    val activo: Boolean = true,
    val tipoDocumentoId: Int = 1
)