package com.app.hotelsys.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.R
import com.app.hotelsys.models.Habitacion
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.widget.LinearLayout
import android.widget.RatingBar
import com.app.hotelsys.repository.CalificacionRepository

class HabitacionAdapter(
    var listaHabitaciones: List<Habitacion>,
    private val onClickCalificar: (Habitacion, String?, Int) -> Unit, // enviamos posición + imagen
    private val onClickReservar: (Habitacion) -> Unit,
    private val onClickFavorito: (Habitacion) -> Unit
) : RecyclerView.Adapter<HabitacionAdapter.HabitacionViewHolder>() {

    private val favoritos = mutableSetOf<Int>() // posiciones favoritas
    private val calificacionRepository = CalificacionRepository()

    inner class HabitacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHabitacion: ImageView = itemView.findViewById(R.id.imgHabitacion)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreHabitacion)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionHabitacion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioHabitacion)
        val layoutCalificacion: LinearLayout = itemView.findViewById(R.id.layoutCalificacion)
        val ratingBarItem: RatingBar = itemView.findViewById(R.id.ratingBarItem)
        val tvCalificacionItem: TextView = itemView.findViewById(R.id.tvCalificacionItem)
        val btnCalificar: Button = itemView.findViewById(R.id.btnCalificar)
        val btnReservar: Button = itemView.findViewById(R.id.btnReservar)
        val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habitacion, parent, false)
        return HabitacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitacionViewHolder, position: Int) {
        val habitacion = listaHabitaciones[position]
        val context = holder.itemView.context

        // Textos
        holder.tvNombre.text = habitacion.nombre
        holder.tvDescripcion.text = habitacion.descripcion
        holder.tvPrecio.text = habitacion.precio

        calificacionRepository.obtenerCalificacionesPorHabitacion(
            habitacionId = habitacion.idHabitacion,
            onSuccess = { calificaciones ->
                if (calificaciones.isEmpty()) {
                    holder.ratingBarItem.visibility = View.GONE
                    holder.tvCalificacionItem.text = "Sin calificaciones"
                } else {
                    holder.ratingBarItem.visibility = View.VISIBLE
                    val promedio = calificaciones.map { it.calificacion }.average()
                    holder.ratingBarItem.rating = promedio.toFloat()
                    holder.tvCalificacionItem.text = "${String.format("%.1f", promedio)} (${calificaciones.size} reseñas)"
                }
            },
            onFailure = {
                holder.tvCalificacionItem.text = "No disponible"
            }
        )

        // === Fondos disponibles ===
        val fondos = listOf(
            R.drawable.fondo2,
            R.drawable.fondo3,
            R.drawable.fondo4,
            R.drawable.fondo5,
            R.drawable.fondo6,
            R.drawable.fondo7,
            R.drawable.fondo8
        )

        // Asignar fondo solo si no tiene ya asignado
        if (habitacion.fondoResId == 0) {
            habitacion.fondoResId = fondos[position % fondos.size]
        }

        // Construir URL de imagen si existe
        val imagenUrlCompleta = habitacion.imagenUrl?.takeIf { it.isNotBlank() }?.let {
            if (it.startsWith("http")) it else "http://10.0.2.2:8081/api/$it"
        }

        // --- Construye imagen de carga ---
        val circularProgressDrawable = androidx.swiperefreshlayout.widget.CircularProgressDrawable(context).apply {
            strokeWidth = 5f  // Grosor de la línea
            centerRadius = 30f // Radio del círculo
            start()           // ¡Importante! Inicia la animación
        }

        // Cargar imagen o fondo
        Glide.with(context)
            .load(imagenUrlCompleta)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.placeholder_habitacion)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imgHabitacion)

        // Botón Calificar → enviamos posición + imagen
        holder.btnCalificar.setOnClickListener {
            val imagenParaEnviar = imagenUrlCompleta ?: habitacion.fondoResId.toString()
            onClickCalificar(habitacion, imagenParaEnviar, position)
        }

        // Botón Reservar
        holder.btnReservar.setOnClickListener { onClickReservar(habitacion) }

        // Favoritos
        val esFavorito = favoritos.contains(position)
        actualizarIconoFavorito(holder, esFavorito)

        holder.btnFavorito.setOnClickListener {
            if (esFavorito) favoritos.remove(position)
            else favoritos.add(position)

            onClickFavorito(habitacion)
            notifyItemChanged(position)
        }
    }

    private fun actualizarIconoFavorito(holder: HabitacionViewHolder, esFavorito: Boolean) {
        val context = holder.itemView.context
        if (esFavorito) {
            holder.btnFavorito.setImageResource(R.drawable.ic_favorite_filled)
            holder.btnFavorito.setColorFilter(
                ContextCompat.getColor(context, android.R.color.holo_red_light),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border)
            holder.btnFavorito.setColorFilter(
                ContextCompat.getColor(context, android.R.color.white),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    override fun getItemCount(): Int = listaHabitaciones.size

    // Actualizar datos desde API o búsqueda
    fun updateData(nuevaLista: List<Habitacion>) {
        listaHabitaciones = nuevaLista
        notifyDataSetChanged()
    }
}
