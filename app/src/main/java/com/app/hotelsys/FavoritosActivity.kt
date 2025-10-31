package com.app.hotelsys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionAdapter
import com.app.hotelsys.helper.AlertaHelper
import com.app.hotelsys.repository.FavoritosRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritosActivity : AppCompatActivity() {

    private lateinit var favoritosRepository: FavoritosRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        // === Configurar RecyclerView ===
        val recycler = findViewById<RecyclerView>(R.id.recyclerFavoritos)
        recycler.layoutManager = LinearLayoutManager(this)


        // === Configurar favoritosRepository ===
        favoritosRepository = FavoritosRepository(this)
        val favoritos = favoritosRepository.obtenerFavoritos()

        if (favoritos.isEmpty()) {
            AlertaHelper.mostrarAlertaToast("No tienes habitaciones favoritas.", this)
        }

        recycler.adapter = HabitacionAdapter(
            favoritos.toMutableList(),
            onClickCalificar = { habitacion, imagen, position ->
                AlertaHelper.mostrarAlertaToast("Abrir reseñas de ${habitacion.nombre}", this)
            },
            onClickReservar = { habitacion ->
                AlertaHelper.mostrarAlertaToast("Reservar ${habitacion.nombre}", this)
            },
            onClickFavorito = { habitacion ->
                favoritosRepository.eliminarFavorito(habitacion.idHabitacion)
                AlertaHelper.mostrarAlertaToast("Eliminado de favoritos 💔", this)
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

}
