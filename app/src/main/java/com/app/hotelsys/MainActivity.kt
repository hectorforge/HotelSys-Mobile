package com.app.hotelsys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionCalificarAdapter
import com.app.hotelsys.models.calificacion.EstadoHabitacionCalificar
import com.app.hotelsys.models.calificacion.HabitacionCalificar
import com.app.hotelsys.models.calificacion.ImagenHabitacionCalificar
import com.app.hotelsys.models.calificacion.TipoHabitacionCalificar
import com.app.hotelsys.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.app.hotelsys.ui.calificacion.CalificacionFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitacionCalificarAdapter: HabitacionCalificarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // 1. Verificar si el usuario está logueado
        if (auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return // Importante: Salir del onCreate si no hay usuario
        }

        // 2. Configurar el RecyclerView
        setupRecyclerView()

        // 3. Cargar los datos (por ahora, de prueba)
        loadDummyData()

        // 4. Lógica para el botón de logout
        val fabLogout: FloatingActionButton = findViewById(R.id.fabLogout)
        fabLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Enlazar la variable del RecyclerView con el componente del layout
        recyclerView = findViewById(R.id.recyclerViewHabitaciones)
        // Decirle al RecyclerView que muestre los ítems en una lista vertical
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadDummyData() {
        val habitacionesDePrueba = createDummyHabitaciones()

        habitacionCalificarAdapter = HabitacionCalificarAdapter(habitacionesDePrueba) { habitacion ->
            val imageUrl = if (habitacion.imagenes.isNotEmpty()) habitacion.imagenes[0].url else ""
            val calificacionFragment = CalificacionFragment.newInstance(
                habitacion.id,
                habitacion.numero,
                habitacion.tipoHabitacion.descripcion,
                imageUrl
            )
            calificacionFragment.show(supportFragmentManager, "CalificacionFragment")
        }

        recyclerView.adapter = habitacionCalificarAdapter
    }

    private fun createDummyHabitaciones(): List<HabitacionCalificar> {
        // Datos de prueba con URLs HTTPS
        return listOf(
            HabitacionCalificar(
                id = 1,
                numero = "101",
                tipoHabitacion = TipoHabitacionCalificar(1, "Simple", 100.0),
                estadoHabitacion = EstadoHabitacionCalificar(1, "Disponible"),
                imagenes = listOf(ImagenHabitacionCalificar(1, "https://images.unsplash.com/photo-1618773928121-c32242e63f39?q=80&w=2070&auto=format&fit=crop", "Vista 101", ""))
            ),
            HabitacionCalificar(
                id = 3,
                numero = "201",
                tipoHabitacion = TipoHabitacionCalificar(2, "Doble", 180.0),
                estadoHabitacion = EstadoHabitacionCalificar(1, "Disponible"),
                imagenes = listOf(ImagenHabitacionCalificar(2, "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?q=80&w=2070&auto=format&fit=crop", "Vista 201", ""))
            ),
            HabitacionCalificar(
                id = 5,
                numero = "301",
                tipoHabitacion = TipoHabitacionCalificar(3, "Matrimonial", 220.0),
                estadoHabitacion = EstadoHabitacionCalificar(2, "Ocupada"),
                imagenes = listOf(ImagenHabitacionCalificar(3, "https://images.unsplash.com/photo-1566665797739-1674de7a421a?q=80&w=1974&auto=format&fit=crop", "Vista 301", ""))
            )
        )
    }
}
