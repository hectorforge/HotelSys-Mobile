package com.app.hotelsys.adapters.calificacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.R
import com.app.hotelsys.models.calificacion.ResenaCalificar

class ResenaCalificarAdapter(private val resenas: List<ResenaCalificar>) :
    RecyclerView.Adapter<ResenaCalificarAdapter.ResenaViewHolder>() {

    inner class ResenaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.textViewNombreUsuarioResena)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBarResena)
        val comentario: TextView = itemView.findViewById(R.id.textViewComentarioResena)

        fun bind(resenaCalificar: ResenaCalificar) {
            nombre.text = resenaCalificar.nombreUsuario
            rating.rating = resenaCalificar.calificacion
            comentario.text = resenaCalificar.comentario
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resena, parent, false)
        return ResenaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResenaViewHolder, position: Int) {
        holder.bind(resenas[position])
    }

    override fun getItemCount(): Int = resenas.size
}
