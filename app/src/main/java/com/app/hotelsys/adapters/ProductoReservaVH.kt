package com.app.hotelsys.adapters

import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.databinding.ItemProductoReservaBinding
import com.app.hotelsys.models.ProductoReservaRequest

class ProductoReservaVH(private val binding: ItemProductoReservaBinding) : RecyclerView.ViewHolder(binding.root) {

    fun completarInformacionProducto(
        producto: ProductoReservaRequest,
        onDisminuirClick: (ProductoReservaRequest) -> Unit,
        onAumentarClick: (ProductoReservaRequest) -> Unit,
        onEliminarClick: (ProductoReservaRequest) -> Unit
    ) {        
        binding.tvProductoId.text = producto.productoId.toString()
        binding.tvProductoNombre.text = producto.nombreProducto
//        binding.tvProductoPrecio.text = producto.precioUnitarioGrabado.toString()
        binding.tvProductoPrecio.text = "S/ ${"%.2f".format(producto.precioUnitarioGrabado)}"
        binding.tvCantidad.text = producto.cantidad.toString()

        binding.btnCantidadAumentar.setOnClickListener {
            onAumentarClick(producto)
        }

        binding.btnCantidadDisminuir.setOnClickListener {
            onDisminuirClick(producto)
        }

        binding.btnCantidadEliminar.setOnClickListener {
            onEliminarClick(producto)
        }

    }

}