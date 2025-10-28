package com.levelup.data.repository

import com.levelup.data.model.CartItem
import com.levelup.data.model.Product
<<<<<<< HEAD
import com.levelup.data.remote.ApiService
=======
>>>>>>> 132744ef48180587ae3e16a0568bb51586656182
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
<<<<<<< HEAD
class CartRepository @Inject constructor(
    private val apiService: ApiService
) {
=======
class CartRepository @Inject constructor() {
>>>>>>> 132744ef48180587ae3e16a0568bb51586656182

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: Flow<List<CartItem>> = _cartItems.asStateFlow()

<<<<<<< HEAD
    suspend fun getCart(userId: String): Result<List<CartItem>> {
        return try {
            val response = apiService.getCart(userId)
            if (response.isSuccessful && response.body() != null) {
                val cart = response.body()!!
                _cartItems.value = cart
                Result.success(cart)
            } else {
                Result.failure(Exception("Error al obtener el carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(userId: String, productId: String, quantity: Int): Result<Unit> {
        return try {
            val response = apiService.addToCart(userId, productId, quantity)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al agregar al carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            val response = apiService.updateCartItemQuantity(cartItemId, quantity)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar cantidad: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeCartItem(cartItemId: String): Result<Unit> {
        return try {
            val response = apiService.removeCartItem(cartItemId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar producto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val response = apiService.clearCart(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al vaciar carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
=======
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
>>>>>>> 132744ef48180587ae3e16a0568bb51586656182
    }
}