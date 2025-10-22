package com.app.hotelsys.models

data class Habitacion (
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val calificacion: String,
    val imagenUrl: String,
    var fondoResId: Int

)
