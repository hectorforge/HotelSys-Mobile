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

    private lateinit var binding: ActivityRecyclerReservasBinding
    private lateinit var adapter: RecyclerViewAdapterReserva
    private val reservasList = mutableListOf<ReservaResponse>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

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

        binding.btnVolverIndex.setOnClickListener {
            startActivity(Intent(this, MainActivityIndex::class.java))
            finish()
        }

        obtenerReservasCliente()
    }

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

    override fun onDestroy() {
        super.onDestroy()
    }
}
