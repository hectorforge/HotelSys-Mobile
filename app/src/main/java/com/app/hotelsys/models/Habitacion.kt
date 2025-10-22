package com.app.hotelsys.models

data class Habitacion (
    val idHabitacion: Int,
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val calificacion: String,
    val imagenUrl: String,
    var fondoResId: Int
)
