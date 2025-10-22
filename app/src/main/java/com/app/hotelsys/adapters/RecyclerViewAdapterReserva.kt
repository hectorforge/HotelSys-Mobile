package com.app.hotelsys.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.R
import com.app.hotelsys.databinding.ItemReservaBinding
import com.app.hotelsys.models.ReservaResponse
import com.app.hotelsys.retrofit.RetrofitReserva
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class RecyclerViewAdapterReserva :
    RecyclerView.Adapter<RecyclerViewAdapterReserva.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var reservas: MutableList<ReservaResponse>
    private lateinit var onReservaCancelada: () -> Unit
    private lateinit var emailCliente: String

    fun RecyclerViewAdapterReserva(
        context: Context,
        reservas: MutableList<ReservaResponse>,
        onReservaCancelada: () -> Unit,
        emailCliente: String
    ) {
        this.context = context
        this.reservas = reservas
        this.onReservaCancelada = onReservaCancelada
        this.emailCliente = emailCliente
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemReservaBinding.bind(view)
        val btnCancelar: MaterialButton = binding.btnCancelar

        init {
            view.setOnClickListener {
                Toast.makeText(
                    context,
                    "Reserva: ${binding.tvHabitacion.text}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_reserva, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = reservas[position]
        val binding = holder.binding

        binding.tvEstado.text = reserva.estadoReserva.descripcion
        binding.tvFecha.text = "${reserva.fechaCheckIn} → ${reserva.fechaCheckOut}"
        binding.tvMonto.text = "Total: S/ ${String.format("%.2f", reserva.montoTotalCalculado)}"

        val habitacion = reserva.habitaciones.firstOrNull()
        binding.tvHabitacion.text = habitacion?.let {
            "Hab. ${it.habitacion.numero} - ${it.habitacion.tipoHabitacion.descripcion}"
        } ?: ""

        val productosTexto = reserva.productos.takeIf { it.isNotEmpty() }
            ?.joinToString(", ") { it.producto.nombreProducto } ?: ""
        binding.tvProductos.text =
            if (productosTexto.isNotEmpty()) "Productos: $productosTexto" else ""

        val colorEstado = when (reserva.estadoReserva.descripcion.lowercase()) {
            "pendiente" -> R.color.estado_pendiente
            "confirmada" -> R.color.estado_confirmada
            "cancelada" -> R.color.estado_cancelada
            "finalizada" -> R.color.estado_finalizada
            else -> R.color.hotel_azul
        }
        binding.tvEstado.setTextColor(ContextCompat.getColor(context, colorEstado))

        holder.btnCancelar.visibility =
            if (reserva.estadoReserva.descripcion.equals("Pendiente", ignoreCase = true))
                View.VISIBLE else View.GONE

        holder.btnCancelar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Cancelar Reserva")
                .setMessage("¿Estás seguro de cancelar esta reserva?")
                .setPositiveButton("Sí") { dialog, _ ->
                    cancelarReservaPorIdYEmail(reserva.id, emailCliente, position)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun cancelarReservaPorIdYEmail(idReserva: Int, emailCliente: String, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitReserva.api.cancelarReservaPorIdYEmail(idReserva, emailCliente)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Reserva cancelada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        reservas[position].estadoReserva.descripcion = "Cancelada"
                        notifyItemChanged(position)
                        onReservaCancelada()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                        Toast.makeText(
                            context,
                            "Error: ${response.code()} - $errorMsg",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return if (::reservas.isInitialized) reservas.size else 0
    }
}
