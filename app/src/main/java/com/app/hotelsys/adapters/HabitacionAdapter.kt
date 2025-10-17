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

class HabitacionAdapter(
    private var listaHabitaciones: List<Habitacion>,
    private val onClickCalificar: (Habitacion) -> Unit,
    private val onClickReservar: (Habitacion) -> Unit,
    private val onClickFavorito: (Habitacion) -> Unit
) : RecyclerView.Adapter<HabitacionAdapter.HabitacionViewHolder>() {

    private val favoritos = mutableSetOf<Int>()

    inner class HabitacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHabitacion: ImageView = itemView.findViewById(R.id.imgHabitacion)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreHabitacion)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionHabitacion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioHabitacion)
        val tvCalificacion: TextView = itemView.findViewById(R.id.tvCalificacion)
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

        holder.tvNombre.text = habitacion.nombre
        holder.tvDescripcion.text = habitacion.descripcion
        holder.tvPrecio.text = habitacion.precio
        holder.tvCalificacion.text = habitacion.calificacion

        Glide.with(context)
            .load(habitacion.imagenUrl)
            .placeholder(R.drawable.fondo4)
            .centerCrop()
            .into(holder.imgHabitacion)

        holder.btnCalificar.setOnClickListener { onClickCalificar(habitacion) }
        holder.btnReservar.setOnClickListener { onClickReservar(habitacion) }

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

    fun updateData(nuevaLista: List<Habitacion>) {
        listaHabitaciones = nuevaLista
        notifyDataSetChanged()
    }
}
