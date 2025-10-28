package com.app.hotelsys

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionAdapter
import com.app.hotelsys.api.RetrofitClient
import com.app.hotelsys.models.Habitacion
import com.app.hotelsys.models.HabitacionResponse
import com.app.hotelsys.ui.PerfilActivity
import com.app.hotelsys.ui.ReservasActivity
import com.app.hotelsys.ui.calificacion.CalificacionFragment
import com.app.hotelsys.ui.reserva.ReservaFragment
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

        // ====== Inicialización de vistas ======
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        recyclerHabitaciones = findViewById(R.id.recyclerHabitaciones)
        etBuscar = findViewById(R.id.etBuscar)
        tvCantidad = findViewById(R.id.tvCantidadHabitaciones)

        // Marcar ítem actual
        bottomNav.selectedItemId = R.id.nav_buscar

        // ====== Usuario actual ======
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val emailUsuario = usuarioActual?.email ?: "Invitado"

        // ====== Menú del toolbar ======
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_toolbar, popupMenu.menu)
        popupMenu.menu.findItem(R.id.nav_micuenta).title = "Mi cuenta ($emailUsuario)"
        btnMenu.setOnClickListener { popupMenu.show() }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_micuenta -> {
                    Toast.makeText(this, "Usuario: $emailUsuario", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // ====== Bottom Navigation ======
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_buscar -> true
                R.id.nav_favoritos -> {
                    startActivity(Intent(this, FavoritosActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_reservas -> {
                    startActivity(Intent(this, ReservasActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        // ====== Configurar RecyclerView ======
        recyclerHabitaciones.layoutManager = LinearLayoutManager(this)
        adapter = HabitacionAdapter(
            listOf(),
            onClickCalificar = { habitacion, imagenUrl, _ ->
                val calificacionFragment = CalificacionFragment.newInstance(
                    habitacionId = habitacion.idHabitacion,
                    habitacionNumero = habitacion.nombre,
                    habitacionTipo = habitacion.descripcion,
                    habitacionImagenUrl = imagenUrl ?: ""
                )
                calificacionFragment.show(supportFragmentManager, "CalificacionFragment")
            },
            onClickReservar = { habitacion ->
                val dialogo = ReservaFragment()
                val bundle = Bundle().apply {
                    putInt("idHabitacion", habitacion.idHabitacion)
                    putString("nombreHabitacion", habitacion.nombre)
                    putString("descripcionHabitacion", habitacion.descripcion)
                    putString("precioHabitacion", habitacion.precio)
                    putString("imagenHabitacion", habitacion.imagenUrl)
                }
                dialogo.arguments = bundle
                dialogo.show(supportFragmentManager, "fragment_reserva")
            },
            onClickFavorito = { habitacion -> botonFavorito(habitacion) }
        )
        recyclerHabitaciones.adapter = adapter

        // ====== Cargar habitaciones ======
        cargarHabitacionesDesdeAPI()

        // ====== Búsqueda personalizada ======
        etBuscar.doOnTextChanged { texto, _, _, _ ->
            val query = texto.toString().trim().lowercase()
            val filtradas = listaHabitaciones.filter { habitacion ->
                val coincideNombre = habitacion.nombre.lowercase().contains(query)
                val numero = habitacion.nombre
                    .lowercase()
                    .substringAfter("habitación ")
                    .substringBefore(" ")
                    .trim()
                val coincideNumero = numero == query
                val precioTexto = habitacion.precio
                    .lowercase()
                    .replace("s/", "")
                    .replace("/ noche", "")
                    .replace(" ", "")
                    .trim()
                val coincidePrecio = query.toDoubleOrNull()?.let { q ->
                    precioTexto.toDoubleOrNull()?.let { p -> p == q } ?: false
                } ?: false

                coincideNombre || coincideNumero || coincidePrecio
            }
            adapter.updateData(filtradas)
            tvCantidad.text = "${filtradas.size} encontradas"
        }
    }

    // ====== Añadir o eliminar favoritos ======
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

    // ====== Cargar datos desde API ======
    private fun cargarHabitacionesDesdeAPI() {
        val call = RetrofitClient.instance.getHabitaciones()
        call.enqueue(object : Callback<List<HabitacionResponse>> {
            override fun onResponse(
                call: Call<List<HabitacionResponse>>,
                response: Response<List<HabitacionResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val habitacionesApi = response.body() ?: emptyList()
                    listaHabitaciones = habitacionesApi.map { h ->
                        Habitacion(
                            idHabitacion = h.id,
                            nombre = "Habitación ${h.numero} (${h.tipoHabitacion.descripcion})",
                            descripcion = h.imagenes.firstOrNull()?.descripcion ?: "Sin descripción",
                            precio = "S/ ${h.tipoHabitacion.precioBaseNoche} / noche",
                            calificacion = "⭐ ${h.estadoHabitacion.descripcion}",
                            imagenUrl = "http://10.0.2.2:8081${h.imagenes.firstOrNull()?.url ?: ""}",
                            fondoResId = 0
                        )
                    }
                    adapter.updateData(listaHabitaciones)
                    tvCantidad.text = "${listaHabitaciones.size} encontradas"
                } else {
                    Toast.makeText(
                        this@MainActivityIndex,
                        "Error de servidor (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<HabitacionResponse>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivityIndex,
                    "Error de conexión: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
