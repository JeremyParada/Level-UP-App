package com.levelup.data.model

data class OrderRequest(
    val idUsuario: Long,
    val idDireccion: Long,
    val metodoPago: String,
    val productos: List<OrderProductRequest>
)

data class OrderProductRequest(
    val codigo: String,
    val cantidad: Int
)
