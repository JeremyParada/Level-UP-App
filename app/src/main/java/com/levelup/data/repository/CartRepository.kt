package com.levelup.data.repository

import com.levelup.data.model.CartItem
import com.levelup.data.model.Product
import com.levelup.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val apiService: ApiService
) {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: Flow<List<CartItem>> = _cartItems.asStateFlow()

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
    }
}