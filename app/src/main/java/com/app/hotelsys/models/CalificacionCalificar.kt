package com.app.hotelsys.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CalificacionCalificar(
    val id: String = "", // ID del documento en Firestore
    val habitacionId: Int = 0,
    val usuarioId: String = "",
    val calificacion: Float = 0.0f,
    val comentario: String = "",
    @ServerTimestamp
    val fecha: Date? = null,
    // Datos del usuario para mostrar en la UI
    val nombreUsuario: String = ""
)
