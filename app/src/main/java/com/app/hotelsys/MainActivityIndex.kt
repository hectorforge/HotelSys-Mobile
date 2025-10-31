package com.app.hotelsys

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionAdapter
import com.app.hotelsys.api.RetrofitClient
import com.app.hotelsys.helper.AlertaHelper
import com.app.hotelsys.models.Habitacion
import com.app.hotelsys.models.HabitacionResponse
import com.app.hotelsys.repository.FavoritosRepository
import com.app.hotelsys.ui.PerfilActivity
import com.app.hotelsys.ui.ReservasActivity
import com.app.hotelsys.ui.calificacion.CalificacionFragment
import com.app.hotelsys.ui.reserva.ReservaFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityIndex : AppCompatActivity() {

    private lateinit var adapter: HabitacionAdapter
    private lateinit var recyclerHabitaciones: RecyclerView
    private lateinit var tvCantidad: TextView
    private lateinit var etBuscar: EditText
    private var listaHabitaciones: List<Habitacion> = emptyList()
    private lateinit var favoritosRepository: FavoritosRepository


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
        bottomNav.selectedItemId = R.id.nav_habitaciones

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
                    AlertaHelper.mostrarAlertaToast("Usuario: $emailUsuario", this)
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
                R.id.nav_habitaciones -> true
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

        // ====== Inicializar FavoritosRepository ======
        favoritosRepository = FavoritosRepository(this)
    }

    // ====== Añadir o eliminar favoritos ======
    private fun botonFavorito(habitacion: Habitacion) {
        if (favoritosRepository.esFavorito(habitacion.idHabitacion)) {
            favoritosRepository.eliminarFavorito(habitacion.idHabitacion)
            AlertaHelper.mostrarAlertaToast("Eliminado de favoritos 💔", this)
        } else {
            favoritosRepository.agregarFavorito(habitacion)
            AlertaHelper.mostrarAlertaToast("Agregado a favoritos ❤️", this)
        }
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
                            imagenUrl = "${BuildConfig.BASE_IP}${h.imagenes.firstOrNull()?.url ?: ""}",
                            fondoResId = 0
                        )
                    }
                    adapter.updateData(listaHabitaciones)
                    tvCantidad.text = "${listaHabitaciones.size} encontradas"
                } else {
                    AlertaHelper.mostrarAlerta("Error", "No se pudieron cargar las habitaciones. \nCódigo de error: ${response.code()}", this@MainActivityIndex)
                }
            }

            override fun onFailure(call: Call<List<HabitacionResponse>>, t: Throwable) {
                AlertaHelper.mostrarAlerta("Error de conexión", "No se pudo conectar con el servidor. \nError: ${t.localizedMessage}", this@MainActivityIndex)
            }
        })
    }
}
