package com.app.hotelsys.repository

import com.app.hotelsys.models.calificacion.CalificacionCalificar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.jvm.java

class CalificacionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val calificacionCollection = db.collection("calificaciones")

    fun guardarCalificacion(
        calificacion: CalificacionCalificar,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        calificacionCollection.add(calificacion)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun obtenerCalificacionesPorHabitacion(
        habitacionId: Int,
        onSuccess: (List<CalificacionCalificar>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        calificacionCollection
            .whereEqualTo("habitacionId", habitacionId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    val calificaciones = querySnapshot.toObjects(CalificacionCalificar::class.java)
                    onSuccess(calificaciones)
                } catch (e: Exception) {
                    onFailure(e)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}