package com.levelup.data.remote

import com.levelup.data.model.Address
import com.levelup.data.model.CartItem
import com.levelup.data.model.Category
import com.levelup.data.model.Product
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Path

interface ApiService {

    @GET("api/v1/carrito/{userId}")
    suspend fun getCart(@Path("userId") userId: String): Response<List<CartItem>>

    @POST("api/v1/carrito")
    suspend fun addToCart(
        @Body body: Map<String, Any>
    ): Response<Unit>

    @PUT("api/v1/carrito/{cartItemId}")
    suspend fun updateCartItemQuantity(
        @Path("cartItemId") cartItemId: String,
        @Body body: Map<String, Any>
    ): Response<Unit>

    @DELETE("api/v1/carrito/{cartItemId}")
    suspend fun removeCartItem(@Path("cartItemId") cartItemId: String): Response<Unit>

    @DELETE("api/v1/carrito/usuario/{userId}")
    suspend fun clearCart(@Path("userId") userId: String): Response<Unit>

    @GET("api/v1/direcciones/{userId}")
    suspend fun getAddressesByUser(@Path("userId") userId: String): Response<List<Address>>

    @POST("api/v1/direcciones")
    suspend fun createAddress(@Body address: Address): Response<Unit>

    // Obtener todos los productos
    @GET("api/v1/productos")
    suspend fun getAllProducts(): Response<List<Product>>

    // Obtener producto por código
    @GET("api/v1/productos/{codigo}")
    suspend fun getProductByCode(@Path("codigo") codigo: Int): Response<Product>

    // Obtener todas las categorías
    @GET("api/v1/productos/categorias")
    suspend fun getCategories(): Response<List<Category>>

    // Obtener productos por categoría
    @GET("api/v1/productos/categoria/{idCategoria}")
    suspend fun getProductsByCategory(@Path("idCategoria") idCategoria: String): Response<List<Product>>
}