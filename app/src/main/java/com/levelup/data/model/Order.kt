package com.levelup.data.model

import java.util.Date

data class Order(
    val idPedido: Long,
    val usuario: User?,
    val direccionEnvio: Direccion?,
    val fechaPedido: String?,
    val totalBruto: Double,
    val descuentoAplicado: Double,
    val totalNeto: Double,
    val estadoPedido: String,
    val metodoPago: String,
    val detalles: List<OrderDetail>
)

data class OrderDetail(
    val idDetallePedido: Long,
    val producto: Product,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double?
)
