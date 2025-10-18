package com.app.hotelsys.models

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val fechaNacimiento: String = "",
    val email: String = "",
    val fechaRegistro: String = "",

    val nombreCompleto : String = "$nombre $apellido",
    val telefono : String = "",
    val dni : String = "",

    val numeroDocumento : String = dni,
    val tipoDocumentoId : Int = 1,
    val activo : Boolean = true
)