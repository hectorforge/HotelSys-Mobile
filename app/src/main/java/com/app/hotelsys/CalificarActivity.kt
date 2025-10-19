package com.app.hotelsys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide

data class Reseña(
    val nombre: String,
    val estrellas: Float,
    val fecha: String,
    val comentario: String,
    val likes: Int
)

class CalificarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.dialog_calificar)

        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarReseña)
        val ivRoom = findViewById<ImageView>(R.id.ivRoom)
        val tvRoomName = findViewById<TextView>(R.id.tvRoomName)

        val nombre = intent.getStringExtra("nombreHabitacion")
        val imagen = intent.getStringExtra("imagenHabitacion")
        val posicion = intent.getIntExtra("posicionHabitacion", 0)

        tvRoomName.text = nombre ?: "Habitación"

        val fondos = listOf(
            R.drawable.fondo2,
            R.drawable.fondo3,
            R.drawable.fondo4,
            R.drawable.fondo5,
            R.drawable.fondo6,
            R.drawable.fondo7,
            R.drawable.fondo8
        )

        val fondoAsignado = fondos[posicion % fondos.size]

        if (!imagen.isNullOrBlank()) {
            if (imagen.startsWith("http")) {
                Glide.with(this)
                    .load(imagen)
                    .placeholder(fondoAsignado)
                    .error(fondoAsignado)
                    .into(ivRoom)
            } else {
                val resId = imagen.toIntOrNull() ?: fondoAsignado
                ivRoom.setImageResource(resId)
            }
        } else {
            ivRoom.setImageResource(fondoAsignado)
        }

        btnCerrar.setOnClickListener { finish() }

        val todasLasReseñas = mapOf(
            0 to listOf(
                Reseña("Carlos Cornejo", 5f, "Hace 2 días", "Excelente habitación, muy limpia y cómoda. El servicio fue excepcional.", 8),
                Reseña("María López", 4.5f, "Hace 1 semana", "La cama era muy cómoda y el desayuno estuvo delicioso. Volvería sin dudarlo.", 15)
            ),
            1 to listOf(
                Reseña("Ángel Sánchez", 4f, "Hace 4 días", "Muy buena ubicación y cómodas, solo que el wifi podría ser más rápido.", 12),
                Reseña("Lucía Fernández", 5f, "Hace 6 días", "Todo impecable. La limpieza y el trato del personal fueron excelentes.", 20)
            ),
            2 to listOf(
                Reseña("Pedro Ramírez", 4f, "Hace 3 días", "Bonita vista desde la habitación. El baño podría estar más iluminado.", 9),
                Reseña("Javier Torres", 3.5f, "Hace 1 semana", "Buena atención, pero el aire acondicionado hacía un poco de ruido.", 6)
            ),
            3 to listOf(
                Reseña("Sofía Herrera", 4.8f, "Hace 1 mes", "Me encantó la decoración y la tranquilidad del lugar. Ideal para descansar.", 17),
                Reseña("Andrés Gutiérrez", 3.8f, "Hace 1 mes", "Buena relación calidad-precio, aunque la piscina estaba cerrada.", 5)
            ),
            4 to listOf(
                Reseña("Verónica Díaz", 4.6f, "Hace 2 semanas", "El desayuno incluido fue excelente y el personal muy atento.", 14),
                Reseña("José Medina", 4.2f, "Hace 3 semanas", "La habitación es amplia, aunque el check-in fue un poco lento.", 10)
            ),
            5 to listOf(
                Reseña("Camila Ortiz", 5f, "Hace 5 días", "Todo perfecto, desde la atención hasta los detalles de limpieza.", 22),
                Reseña("Miguel Castro", 4.4f, "Hace 1 semana", "Muy buena opción para viajes de trabajo. WiFi estable y rápido.", 11)
            ),
            6 to listOf(
                Reseña("Valentina Ruiz", 4.7f, "Hace 3 días", "Hermosa vista desde el balcón. Muy recomendable para parejas.", 19),
                Reseña("Fernando Aguilar", 4.1f, "Hace 2 semanas", "La ubicación es excelente, cerca de todo. Volvería sin dudar.", 13)
            )
        )

        val reseñas = todasLasReseñas[posicion] ?: todasLasReseñas[0]!!

        val card1 = findViewById<CardView>(R.id.cardCarlos)
        val card2 = findViewById<CardView>(R.id.cardAngel)

        actualizarCard(card1, reseñas[0], 0)
        actualizarCard(card2, reseñas[1], 1)
    }

    private fun actualizarCard(card: CardView, reseña: Reseña, index: Int) {
        val nombre = card.findViewById<TextView>(
            if (index == 0) R.id.tvNombre else R.id.tvNombre1
        )
        val rating = card.findViewById<RatingBar>(
            if (index == 0) R.id.rbCalificacion else R.id.rbCalificacion1
        )
        val fecha = card.findViewById<TextView>(
            if (index == 0) R.id.tvFecha else R.id.tvFecha1
        )
        val comentario = card.findViewById<TextView>(
            if (index == 0) R.id.tvComentario else R.id.tvComentario1
        )
        val likes = card.findViewById<TextView>(
            if (index == 0) R.id.tvLikes else R.id.tvLikes1
        )

        nombre.text = reseña.nombre
        rating.rating = reseña.estrellas
        fecha.text = reseña.fecha
        comentario.text = reseña.comentario
        likes.text = reseña.likes.toString()
    }
}
