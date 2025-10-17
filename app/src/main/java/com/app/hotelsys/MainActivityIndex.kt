package com.app.hotelsys

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionAdapter
import com.app.hotelsys.models.Habitacion
import com.google.android.material.appbar.MaterialToolbar

class MainActivityIndex : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val recyclerHabitaciones = findViewById<RecyclerView>(R.id.recyclerHabitaciones)
        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        val tvCantidad = findViewById<TextView>(R.id.tvCantidadHabitaciones)

        // ====== MENÚ TOOLBAR ======
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_toolbar, popupMenu.menu)
        btnMenu.setOnClickListener { popupMenu.show() }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_micuenta -> {
                    Toast.makeText(this, "Mi cuenta", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_favoritos -> {
                    Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        // ====== RECYCLER VIEW ======
        recyclerHabitaciones.layoutManager = LinearLayoutManager(this)

        val listaHabitaciones = listOf(
            Habitacion(
                "Suite Ejecutiva",
                "Amplia suite con vista panorámica de la ciudad.",
                "$150 / noche",
                "⭐ 4.8 (124 reseñas)",
                "https://www.hotelboutiqueperu.com/wp-content/uploads/2020/07/habitacion-suite-ejecutiva.jpg"
            ),
            Habitacion(
                "Habitación Doble",
                "Cómoda y luminosa, ideal para dos personas.",
                "$90 / noche",
                "⭐ 4.6 (98 reseñas)",
                "https://cf.bstatic.com/xdata/images/hotel/max1024x768/123456789.jpg"
            ),
            Habitacion(
                "Suite Presidencial",
                "Lujo total con jacuzzi y sala privada.",
                "$250 / noche",
                "⭐ 5.0 (210 reseñas)",
                "https://images.trvl-media.com/hotels/1000000/30000/24400/24316/24316_321.jpg"
            )
        )

        val adapter = HabitacionAdapter(
            listaHabitaciones,
            onClickCalificar = { habitacion ->
                // 👉 Abrir CalificarActivity
                val intent = Intent(this, CalificarActivity::class.java)
                intent.putExtra("nombreHabitacion", habitacion.nombre)
                intent.putExtra("imagenHabitacion", habitacion.imagenUrl)
                startActivity(intent)
            },
            onClickReservar = { habitacion ->
                Toast.makeText(this, "Reservar ${habitacion.nombre}", Toast.LENGTH_SHORT).show()
            },
            onClickFavorito = { habitacion ->
                Toast.makeText(this, "Favorito ${habitacion.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerHabitaciones.adapter = adapter

        // ====== FILTRO DE BÚSQUEDA ======
        etBuscar.doOnTextChanged { texto, _, _, _ ->
            val query = texto.toString().trim().lowercase()

            val filtradas = listaHabitaciones.filter {
                it.nombre.lowercase().contains(query) || it.precio.lowercase().contains(query)
            }

            adapter.updateData(filtradas)
            tvCantidad.text = "${filtradas.size} encontradas"
        }
    }
}
