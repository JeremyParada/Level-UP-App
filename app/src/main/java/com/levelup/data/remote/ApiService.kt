package com.levelup.data.remote

import com.levelup.data.model.Direccion // Changed from Address
import com.levelup.data.remote.dto.LoginDTO
import com.levelup.data.remote.dto.RegistroDTO
import com.levelup.data.remote.dto.JwtResponseDTO
import com.levelup.data.model.CartItem
import com.levelup.data.model.Category
import com.levelup.data.model.Product
import com.levelup.data.model.User
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Path

interface ApiService {

    // --- AUTH ---
    @POST("api/v1/auth/login")
    suspend fun login(@Body login: LoginDTO): Response<JwtResponseDTO>

    @POST("api/v1/auth/register")
    suspend fun register(@Body registro: RegistroDTO): Response<Map<String, String>>

    @GET("api/v1/usuarios/me/{id}")
    suspend fun getUserProfile(@Path("id") id: Long): Response<User>

    // --- CART ---
    @GET("api/v1/carrito/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): Response<List<CartItem>>

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
    suspend fun clearCart(@Path("userId") userId: Long): Response<Unit>

    // --- DIRECCIONES ---
    @GET("api/v1/direcciones/usuario/{userId}")
    suspend fun getAddressesByUser(@Path("userId") userId: Long): Response<List<Direccion>>

    @POST("api/v1/direcciones")
    suspend fun createAddress(@Body address: Direccion): Response<Direccion>

    @PUT("api/v1/direcciones/{id}")
    suspend fun updateAddress(@Path("id") id: Long, @Body address: Direccion): Response<Direccion>

    @DELETE("api/v1/direcciones/{id}")
    suspend fun deleteAddress(@Path("id") id: Long): Response<Void>

    // --- PRODUCTOS ---
    @GET("api/v1/productos")
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("api/v1/productos/{codigo}")
    suspend fun getProductByCode(@Path("codigo") codigo: Int): Response<Product>

    @GET("api/v1/productos/categorias")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/v1/productos/categoria/{idCategoria}")
    suspend fun getProductsByCategory(@Path("idCategoria") idCategoria: String): Response<List<Product>>

    // --- PEDIDOS ---
    @POST("api/v1/pedidos")
    suspend fun createOrder(@Body order: com.levelup.data.model.OrderRequest): Response<Map<String, Any>>

    @GET("api/v1/pedidos/usuario/{id}")
    suspend fun getOrdersByUser(@Path("id") userId: Long): Response<List<com.levelup.data.model.Order>>
}