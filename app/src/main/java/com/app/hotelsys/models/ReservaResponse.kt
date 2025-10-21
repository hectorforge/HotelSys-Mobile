package com.app.hotelsys.models

data class ReservaResponse(
    val id: Int,
    val fechaReserva: String,
    val fechaCheckIn: String,
    val fechaCheckOut: String,
    val descuento: Double,
    val montoTotalCalculado: Double,
    val activo: Boolean,
    val estadoReserva: EstadoReserva,
    val cliente: ClienteResponse,
    val habitaciones: List<HabitacionReserva>,
    val productos: List<ProductoReserva>
)

data class EstadoReserva(
    val id: Int,
    val descripcion: String
)

data class ClienteResponse(
    val id: Int,
    val nombreCompleto: String,
    val numeroDocumento: String,
    val email: String,
    val telefono: String
)

data class HabitacionReserva(
    val id: Int,
    val precioNocheGrabado: Double,
    val habitacion: HabitacionDetalle
)

data class HabitacionDetalle(
    val id: Int,
    val numero: String,
    val tipoHabitacion: TipoHabitacion
)

data class TipoHabitacion(
    val id: Int,
    val descripcion: String,
    val precioBaseNoche: Double
)

data class ProductoReserva(
    val id: Int,
    val cantidad: Int,
    val precioUnitarioGrabado: Double,
    val producto: ProductoDetalle
)

data class ProductoDetalle(
    val id: Int,
    val nombreProducto: String,
    val precio: Double
)
