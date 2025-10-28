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

    // === Lifecycle: Configuración inicial de la actividad ===
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        // Configurar lista de favoritos
        configurarRecyclerViewFavoritos()

        // Configurar navegación inferior
        configurarBottomNavigation()
    }

    // === Setup: Configurar RecyclerView con habitaciones favoritas ===
    private fun configurarRecyclerViewFavoritos() {
        val recycler = findViewById<RecyclerView>(R.id.recyclerFavoritos)
        recycler.layoutManager = LinearLayoutManager(this)

        val favoritos = obtenerFavoritos()

        // Mostrar mensaje si no hay favoritos
        if (favoritos.isEmpty()) {
            Toast.makeText(this, "No tienes habitaciones favoritas.", Toast.LENGTH_SHORT).show()
        }

        // Configurar adaptador con callbacks para acciones
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
    }

    // === Setup: Configurar navegación inferior con acciones ===
    private fun configurarBottomNavigation() {
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

    // === Storage: Obtener lista de habitaciones favoritas desde SharedPreferences ===
    private fun obtenerFavoritos(): MutableList<Habitacion> {
        val prefs = getSharedPreferences("favoritos", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("lista", "[]")
        val type = object : TypeToken<MutableList<Habitacion>>() {}.type
        return gson.fromJson(json, type)
    }

    // === Storage: Eliminar habitación de favoritos y actualizar SharedPreferences ===
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
