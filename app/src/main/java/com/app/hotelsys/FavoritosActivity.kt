package com.app.hotelsys

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionAdapter
import com.app.hotelsys.helper.AlertaHelper
import com.app.hotelsys.models.Habitacion
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        // === Configurar RecyclerView ===
        val recycler = findViewById<RecyclerView>(R.id.recyclerFavoritos)
        recycler.layoutManager = LinearLayoutManager(this)

        val favoritos = obtenerFavoritos()

        if (favoritos.isEmpty()) {
            AlertaHelper.mostrarAlertaToast("No tienes habitaciones favoritas.", this)
        }

        recycler.adapter = HabitacionAdapter(
            favoritos,
            onClickCalificar = { habitacion, imagen, position ->
                AlertaHelper.mostrarAlertaToast("Abrir reseñas de ${habitacion.nombre}", this)
            },
            onClickReservar = { habitacion ->
                AlertaHelper.mostrarAlertaToast("Reservar ${habitacion.nombre}", this)
            },
            onClickFavorito = { habitacion ->
                eliminarFavorito(habitacion)
                recreate()
            },
            mostrarBotones = false
        )


        // === Configurar Bottom Navigation ===
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomFavoritos)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_volver -> {
                    startActivity(Intent(this, MainActivityIndex::class.java))
                    finish()
                    true
                }
                R.id.nav_reservar -> {
                    AlertaHelper.mostrarAlertaToast("Abrir sección de reservas", this)
                    true
                }
                else -> false
            }
        }
    }

    // === Obtener favoritos guardados ===
    private fun obtenerFavoritos(): MutableList<Habitacion> {
        val prefs = getSharedPreferences("favoritos", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("lista", "[]")
        val type = object : TypeToken<MutableList<Habitacion>>() {}.type
        return gson.fromJson(json, type)
    }

    // === Eliminar habitación de favoritos ===
    private fun eliminarFavorito(habitacion: Habitacion) {
        val prefs = getSharedPreferences("favoritos", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("lista", "[]")
        val type = object : TypeToken<MutableList<Habitacion>>() {}.type
        val lista: MutableList<Habitacion> = gson.fromJson(json, type)

        lista.removeAll { it.nombre == habitacion.nombre }

        prefs.edit().putString("lista", gson.toJson(lista)).apply()
        AlertaHelper.mostrarAlertaToast("Eliminado de favoritos ❤️", this)
    }
}
