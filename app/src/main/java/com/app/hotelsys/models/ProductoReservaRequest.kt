package com.app.hotelsys.models

data class ProductoReservaRequest(
    val productoId: Int,
    var cantidad: Int,

    // Adicionales para facilitar la vista;
    // Valores por defecto para permitir crear solo con productoId y cantidad
    val precioUnitarioGrabado: Double? = null,
    val nombreProducto: String? = null
) {
    // Constructor secundario opcional (útil para llamadas desde Java, que no soportan parámetros por defecto de Kotlin)
    constructor(productoId: Int, cantidad: Int) : this(productoId, cantidad, null, null)
}
