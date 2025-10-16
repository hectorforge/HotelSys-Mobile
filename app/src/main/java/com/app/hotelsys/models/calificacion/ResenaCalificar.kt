package com.app.hotelsys.models.calificacion

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ResenaCalificar(
    val nombreUsuario: String = "",
    val calificacion: Float = 0.0f,
    val comentario: String = "",
    @ServerTimestamp // Para indicar a Firestore que ponga la fecha del servidor aquí
    val fecha: Date? = null
)