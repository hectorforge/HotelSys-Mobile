package com.app.hotelsys.models

import com.google.gson.annotations.SerializedName

data class TipoHabitacionCalificar(
    val id: Int,
    val descripcion: String,
    @SerializedName("precioBaseNoche")
    val precio: Double
)