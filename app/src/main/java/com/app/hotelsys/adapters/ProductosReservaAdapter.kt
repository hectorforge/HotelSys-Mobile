package com.example.repaso_recycler.adaptador

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.ProductoReservaVH
import com.app.hotelsys.databinding.ItemProductoReservaBinding
import com.app.hotelsys.models.ProductoReservaRequest

class ProductosReservaAdapter(
    var lista: List<ProductoReservaRequest>,
    val onDisminuirClick: (ProductoReservaRequest) -> Unit,
    val onAumentarClick: (ProductoReservaRequest) -> Unit,
    val onEliminarClick:(ProductoReservaRequest) -> Unit
): RecyclerView.Adapter<ProductoReservaVH>() {

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): ProductoReservaVH {
        val binding = ItemProductoReservaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoReservaVH(binding)
    }

    override fun onBindViewHolder(
        holder: ProductoReservaVH,
        position: Int
    ) {
        val productoReserva = lista[position]
        holder.completarInformacionProducto(productoReserva, onDisminuirClick, onAumentarClick, onEliminarClick)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}
