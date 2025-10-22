package com.app.hotelsys

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.app.hotelsys.api.RetrofitClient
import com.app.hotelsys.models.Habitacion
import com.app.hotelsys.models.HabitacionResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityIndex : AppCompatActivity() {

    private lateinit var adapter: HabitacionAdapter
    private lateinit var recyclerHabitaciones: RecyclerView
    private lateinit var tvCantidad: TextView
    private lateinit var etBuscar: EditText
    private var listaHabitaciones: List<Habitacion> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        // ====== Vistas ======

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        recyclerHabitaciones = findViewById(R.id.recyclerHabitaciones)
        etBuscar = findViewById(R.id.etBuscar)
        tvCantidad = findViewById(R.id.tvCantidadHabitaciones)

        // Marcar ítem actual en BottomNav
        bottomNav.selectedItemId = R.id.nav_buscar

        // ====== Usuario actual ======
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val emailUsuario = usuarioActual?.email ?: "Invitado"

        // ====== Menú del toolbar ======
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_toolbar, popupMenu.menu)
        btnMenu.setOnClickListener { popupMenu.show() }
        popupMenu.menu.findItem(R.id.nav_micuenta).title = "Mi cuenta ($emailUsuario)"

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_micuenta -> {
                    Toast.makeText(this, "Usuario: $emailUsuario", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }



        // ====== Bottom Navigation ======
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_buscar -> {
                    //
                    true
                }
                R.id.nav_favoritos -> {
                    startActivity(Intent(this, FavoritosActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_reservas -> {
                    Toast.makeText(this, "Próximamente: Reservas", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_perfil -> {
                    Toast.makeText(this, "Mis Perfiles ", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // ====== RecyclerView ======
        recyclerHabitaciones.layoutManager = LinearLayoutManager(this)
        adapter = HabitacionAdapter(
            listOf(),
            onClickCalificar = { habitacion, imagen, position ->
                val intent = Intent(this, CalificarActivity::class.java)
                intent.putExtra("nombreHabitacion", habitacion.nombre)
                intent.putExtra("imagenHabitacion", imagen)
                intent.putExtra("posicionHabitacion", position)
                startActivity(intent)
            },
            onClickReservar = { habitacion ->
                Toast.makeText(this, "Reservar ${habitacion.nombre}", Toast.LENGTH_SHORT).show()
                Log.i("PRUEBA", "ID HABITACIÓN: ${habitacion.idHabitacion}")
            },
            onClickFavorito = { habitacion ->
                botonFavorito(habitacion)
            }
        )
        recyclerHabitaciones.adapter = adapter

        // ====== Cargar habitaciones ======
        cargarHabitacionesDesdeAPI()

        // ====== Búsqueda ======
        etBuscar.doOnTextChanged { texto, _, _, _ ->
            val query = texto.toString().trim().lowercase()
            val filtradas = listaHabitaciones.filter {
                it.nombre.lowercase().contains(query) ||
                        it.descripcion.lowercase().contains(query) ||
                        it.precio.lowercase().contains(query)
            }
            adapter.updateData(filtradas)
            tvCantidad.text = "${filtradas.size} encontradas"
        }
    }

    // === Guardar o eliminar favorito ===
    private fun botonFavorito(habitacion: Habitacion) {
        val prefs = getSharedPreferences("favoritos", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("lista", "[]")
        val type = object : TypeToken<MutableList<Habitacion>>() {}.type
        val lista: MutableList<Habitacion> = gson.fromJson(json, type)

        if (lista.any { it.nombre == habitacion.nombre }) {
            lista.removeAll { it.nombre == habitacion.nombre }
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
        } else {
            lista.add(habitacion)
            Toast.makeText(this, "Agregado a favoritos ❤️", Toast.LENGTH_SHORT).show()
        }

        prefs.edit().putString("lista", gson.toJson(lista)).apply()
    }

    // === Cargar habitaciones desde API ===
    private fun cargarHabitacionesDesdeAPI() {
        val call = RetrofitClient.instance.getHabitaciones()
        call.enqueue(object : Callback<List<HabitacionResponse>> {
            override fun onResponse(
                call: Call<List<HabitacionResponse>>,
                response: Response<List<HabitacionResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val habitacionesApi = response.body() ?: emptyList()

                    listaHabitaciones = habitacionesApi.mapIndexed { index, h ->
                        Habitacion(
                            idHabitacion = h.id,
                            nombre = "Habitación ${h.numero} (${h.tipoHabitacion.descripcion})",
                            descripcion = h.imagenes.firstOrNull()?.descripcion ?: "Sin descripción",
                            precio = "S/ ${h.tipoHabitacion.precioBaseNoche} / noche",
                            calificacion = "⭐ ${h.estadoHabitacion.descripcion}",
                            imagenUrl = "http://10.0.2.2:8081/api/${h.imagenes.firstOrNull()?.url ?: ""}",
                            fondoResId = 0 // <-- valor inicial
                        )
                    }

                    adapter.updateData(listaHabitaciones)
                    tvCantidad.text = "${listaHabitaciones.size} encontradas"

                    Log.i("API_RESPONSE", "Habitaciones cargadas: ${listaHabitaciones.size}")
                } else {
                    Toast.makeText(
                        this@MainActivityIndex,
                        "Error en respuesta del servidor (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_ERROR", "Código de error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<HabitacionResponse>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivityIndex,
                    "Error de conexión: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("API_ERROR", "Fallo al conectar con API", t)
            }
        })
    }
}
