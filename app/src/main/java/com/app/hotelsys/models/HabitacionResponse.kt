package com.app.hotelsys.models

data class HabitacionResponse(
    val id: Int,
    val numero: String,
    val requiereLimpieza: Boolean,
    val activo: Boolean,
    val tipoHabitacion: TipoHabitacion,
    val estadoHabitacion: EstadoHabitacion,
    val imagenes: List<ImagenHabitacion>
)

data class TipoHabitacion(
    val id: Int,
    val descripcion: String,
    val precioBaseNoche: Double
)

data class EstadoHabitacion(
    val id: Int,
    val descripcion: String
)

data class ImagenHabitacion(
    val id: Int,
    val url: String,
    val alt: String,
    val descripcion: String,
    val orden: Int
)
