package com.levelup.data.repository

import com.levelup.data.model.CartItem
import com.levelup.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: Flow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: Product, quantity: Int = 1) {
        val currentCart = _cartItems.value.toMutableList()
        // CORREGIDO: usar codigo en lugar de id
        val existingItem = currentCart.find { it.product.codigo == product.codigo }

        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            currentCart.add(CartItem(product, quantity))
        }

        _cartItems.value = currentCart
    }

    fun removeFromCart(productId: String) {
        // CORREGIDO: usar codigo en lugar de id
        _cartItems.value = _cartItems.value.filter { it.product.codigo != productId }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val currentCart = _cartItems.value.toMutableList()
        // CORREGIDO: usar codigo en lugar de id
        val item = currentCart.find { it.product.codigo == productId }
        
        if (item != null) {
            if (quantity <= 0) {
                removeFromCart(productId)
            } else {
                item.quantity = quantity
                _cartItems.value = currentCart
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getTotal(): Double {
        return _cartItems.value.sumOf { it.subtotal }
    }

    fun getItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
}