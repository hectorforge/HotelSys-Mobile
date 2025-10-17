package com.app.hotelsys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class CalificarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        // Carga el layout corregido
        setContentView(R.layout.dialog_calificar)

        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarReseña)
        val ivRoom = findViewById<ImageView>(R.id.ivRoom)
        val tvRoomName = findViewById<TextView>(R.id.tvRoomName)

        val nombre = intent.getStringExtra("nombreHabitacion")
        val imagen = intent.getStringExtra("imagenHabitacion")

        tvRoomName.text = nombre ?: "Habitación"

        // Evita error si no hay imagen
        if (!imagen.isNullOrBlank()) {
            Glide.with(this)
                .load(imagen)
                .placeholder(R.drawable.foto45)
                .error(R.drawable.foto45)
                .into(ivRoom)
        } else {
            ivRoom.setImageResource(R.drawable.foto45)
        }


        btnCerrar.setOnClickListener {
            finish()
        }
    }
}
