package com.app.hotelsys.models.calificacion

data class HabitacionCalificar(
    val id: Int,
    val numero: String,
    val tipoHabitacion: TipoHabitacionCalificar,
    val estadoHabitacion: EstadoHabitacionCalificar,
    val imagenes: List<ImagenHabitacionCalificar>
)