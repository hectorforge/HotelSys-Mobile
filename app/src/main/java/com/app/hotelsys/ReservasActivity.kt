package com.app.hotelsys.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hotelsys.MainActivityIndex
import com.app.hotelsys.adapters.RecyclerViewAdapterReserva
import com.app.hotelsys.databinding.ActivityRecyclerReservasBinding
import com.app.hotelsys.helper.AlertaHelper
import com.app.hotelsys.models.ReservaResponse
import com.app.hotelsys.retrofit.RetrofitReserva
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch

/**
 * Activity que muestra la lista de reservas de un cliente en un RecyclerView.
 *
 * Responsabilidades principales:
 * - Inicializar la UI (edge-to-edge, binding)
 * - Configurar el RecyclerView y su adaptador
 * - Consultar al backend las reservas del usuario autenticado
 * - Permitir volver al índice principal
 */
class ReservasActivity : AppCompatActivity() {

    // ViewBinding para acceder a las vistas del layout de forma segura
    private lateinit var binding: ActivityRecyclerReservasBinding

    // Adaptador del RecyclerView que muestra las reservas
    private lateinit var adapter: RecyclerViewAdapterReserva

    // Lista mutable que actúa como fuente de datos para el adaptador
    private val reservasList = mutableListOf<ReservaResponse>()

    // Instancia de FirebaseAuth para obtener información del usuario actual
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate del layout con ViewBinding
        binding = ActivityRecyclerReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Habilita el comportamiento edge-to-edge (dibuja bajo barras de sistema)
        enableEdgeToEdge()
        // Ajusta padding según insets del sistema para evitar solapamientos
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa FirebaseAuth para obtener el email del usuario autenticado
        auth = FirebaseAuth.getInstance()

        // Inicializa el adaptador y lo configura. Observa que el adaptador
        // tiene un método llamado `RecyclerViewAdapterReserva(...)` que actúa
        // como inicializador/configurador (patrón usado en ese adaptador).
        adapter = RecyclerViewAdapterReserva()
        adapter.RecyclerViewAdapterReserva(
            this,
            reservasList,
            { obtenerReservasCliente() }, // callback que el adaptador puede invocar (ej: después de cancelar una reserva)
            auth.currentUser?.email ?: "" // email del cliente (si existe)
        )

        // Configuración estándar del RecyclerView
        binding.recyclerReservas.setHasFixedSize(true) // optimización cuando el tamaño es fijo
        binding.recyclerReservas.layoutManager = LinearLayoutManager(this)
        binding.recyclerReservas.adapter = adapter

        // Botón para volver al índice principal (MainActivityIndex)
        binding.btnVolverIndex.setOnClickListener {
            startActivity(Intent(this, MainActivityIndex::class.java))
            finish()
        }

        // Carga las reservas del cliente actual al iniciar la activity
        obtenerReservasCliente()
    }

    /**
     * Realiza la petición al backend para obtener las reservas del cliente autenticado.
     * Usa corrutinas con lifecycleScope para ejecutar la llamada de forma asíncrona
     * y evitar bloquear el hilo principal.
     */
    private fun obtenerReservasCliente() {
        // Si no hay usuario autenticado, se sale (no hace petición)
        val email = auth.currentUser?.email ?: return

        // Lanzar corrutina ligada al ciclo de vida de la Activity
        lifecycleScope.launch {
            try {
                // Llamada a la API Retrofit definida en RetrofitReserva
                val response = RetrofitReserva.api.getReservasPorEmail(email)
                if (response.isSuccessful) {
                    // Actualiza la lista de reservas y notifica al adaptador
                    reservasList.clear()
                    reservasList.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()

                    val gson = GsonBuilder().setPrettyPrinting().create()
                    Log.i("ReservasActivity", "JSON Recibido: ${gson.toJson(reservasList)}")

                } else {
                    // Manejo básico de errores HTTP
                    AlertaHelper.mostrarAlerta("Error al obtener reservas", "Código de error: ${response.code()}", this@ReservasActivity)
                }
            } catch (e: Exception) {
                // Manejo de excepción de red u otros errores
                AlertaHelper.mostrarAlerta("Error de conexión", "No se pudo conectar con el servidor. \nError: ${e.localizedMessage}", this@ReservasActivity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // No hay limpieza adicional necesaria aquí porque usamos ViewBinding local
        // y FirebaseAuth no requiere cierre. Si hubieran listeners o binding global,
        // se deberían liberar aquí.
    }
}
