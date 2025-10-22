package com.app.hotelsys

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionAdapter
import com.app.hotelsys.models.Habitacion
import com.google.android.material.bottomnavigation.BottomNavigationView
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
            Toast.makeText(this, "No tienes habitaciones favoritas.", Toast.LENGTH_SHORT).show()
        }

        recycler.adapter = HabitacionAdapter(
            favoritos,
            onClickCalificar = { habitacion, imagen, position ->
                Toast.makeText(this, "Abrir reseñas de ${habitacion.nombre}", Toast.LENGTH_SHORT).show()
            },
            onClickReservar = { habitacion ->
                Toast.makeText(this, "Reservar ${habitacion.nombre}", Toast.LENGTH_SHORT).show()
            },
            onClickFavorito = { habitacion ->
                eliminarFavorito(habitacion)
                recreate()
            }
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
                    Toast.makeText(this, "Abrir sección de reservas", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Eliminado de favoritos ❤️", Toast.LENGTH_SHORT).show()
    }
}
