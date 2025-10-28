package com.app.hotelsys.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hotelsys.MainActivityIndex
import com.app.hotelsys.adapters.RecyclerViewAdapterReserva
import com.app.hotelsys.databinding.ActivityRecyclerReservasBinding
import com.app.hotelsys.models.ReservaResponse
import com.app.hotelsys.retrofit.RetrofitReserva
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ReservasActivity : AppCompatActivity() {

    // === Binding y componentes UI ===
    private lateinit var binding: ActivityRecyclerReservasBinding
    private lateinit var adapter: RecyclerViewAdapterReserva

    // === Datos y estado ===
    private val reservasList = mutableListOf<ReservaResponse>()

    // === Dependencias externas ===
    private lateinit var auth: FirebaseAuth

    // === Lifecycle: Configuración inicial de la actividad ===
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar binding y layout
        binding = ActivityRecyclerReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar comportamiento de pantalla completa
        configurarEdgeToEdge()

        // Inicializar dependencias
        inicializarDependencias()

        // Configurar componentes UI
        configurarRecyclerView()
        configurarListeners()

        // Cargar datos iniciales
        obtenerReservasCliente()
    }

    // === Setup: Configurar pantalla completa y márgenes del sistema ===
    private fun configurarEdgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // === Setup: Inicializar Firebase Auth ===
    private fun inicializarDependencias() {
        auth = FirebaseAuth.getInstance()
    }

    // === Setup: Configurar RecyclerView con adaptador y layout ===
    private fun configurarRecyclerView() {
        adapter = RecyclerViewAdapterReserva()
        adapter.RecyclerViewAdapterReserva(
            this,
            reservasList,
            { obtenerReservasCliente() },
            auth.currentUser?.email ?: ""
        )

        binding.recyclerReservas.setHasFixedSize(true)
        binding.recyclerReservas.layoutManager = LinearLayoutManager(this)
        binding.recyclerReservas.adapter = adapter
    }

    // === Setup: Configurar listeners de botones ===
    private fun configurarListeners() {
        binding.btnVolverIndex.setOnClickListener {
            startActivity(Intent(this, MainActivityIndex::class.java))
            finish()
        }
    }

    // === Red: Obtener reservas del cliente autenticado desde API ===
    private fun obtenerReservasCliente() {
        val email = auth.currentUser?.email ?: return

        lifecycleScope.launch {
            try {
                val response = RetrofitReserva.api.getReservasPorEmail(email)
                if (response.isSuccessful) {
                    reservasList.clear()
                    reservasList.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@ReservasActivity,
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ReservasActivity,
                    "Error de conexión: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // === Lifecycle: Limpieza al destruir actividad ===
    override fun onDestroy() {
        super.onDestroy()
    }
}
