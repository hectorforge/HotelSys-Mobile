package com.app.hotelsys.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.R
import com.app.hotelsys.models.HabitacionCalificar
import com.bumptech.glide.Glide

class HabitacionAdapter(
    private var habitaciones: List<HabitacionCalificar>,
    private val onCalificarClicked: (HabitacionCalificar) -> Unit
) : RecyclerView.Adapter<HabitacionAdapter.HabitacionViewHolder>() {

    inner class HabitacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.imageViewHabitacion)
        val numero: TextView = itemView.findViewById(R.id.textViewNumeroHabitacion)
        val tipo: TextView = itemView.findViewById(R.id.textViewTipoHabitacion)
        val botonCalificar: Button = itemView.findViewById(R.id.buttonCalificar)

        fun bind(habitacion: HabitacionCalificar) {
            numero.text = "Habitación ${habitacion.numero}"
            tipo.text = habitacion.tipoHabitacion.descripcion

            // Usamos Glide para cargar la primera imagen
            if (habitacion.imagenes.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(habitacion.imagenes[0].url)
                    .centerCrop()
                    .into(imagen)
            }

            botonCalificar.setOnClickListener {
                onCalificarClicked(habitacion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habitacion, parent, false)
        return HabitacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitacionViewHolder, position: Int) {
        holder.bind(habitaciones[position])
    }

    override fun getItemCount(): Int = habitaciones.size
}
