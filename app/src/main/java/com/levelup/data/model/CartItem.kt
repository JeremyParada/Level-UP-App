package com.levelup.data.model

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val subtotal: Double
        get() = product.precio * quantity
}