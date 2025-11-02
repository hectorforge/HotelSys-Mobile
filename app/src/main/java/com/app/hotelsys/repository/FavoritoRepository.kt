package com.app.hotelsys.repository

import android.content.Context
import com.app.hotelsys.helper.FavoritosDbHelper
import com.app.hotelsys.models.Habitacion

class FavoritosRepository(context: Context) {

    private val dbHelper = FavoritosDbHelper(context)

    fun agregarFavorito(habitacion: Habitacion) {
        dbHelper.agregarFavorito(habitacion)
    }

    fun eliminarFavorito(idHabitacion: Int) {
        dbHelper.eliminarFavorito(idHabitacion)
    }

    fun obtenerFavoritos(): List<Habitacion> {
        return dbHelper.obtenerFavoritos()
    }

    fun esFavorito(idHabitacion: Int): Boolean {
        return dbHelper.esFavorito(idHabitacion)
    }
}
