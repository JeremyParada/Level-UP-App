package com.levelup.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.levelup.data.model.Product
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : ProductRepository {

    private val _productsFlow = MutableStateFlow<List<Product>>(emptyList())
    private var cachedProducts: List<Product>? = null

    override suspend fun getAllProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            if (cachedProducts == null) {
                val jsonString = context.assets.open("productos.json")
                    .bufferedReader()
                    .use { it.readText() }
                
                val type = object : TypeToken<List<Product>>() {}.type
                cachedProducts = gson.fromJson(jsonString, type)
                _productsFlow.value = cachedProducts!!
            }
            Result.success(cachedProducts!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: String): Result<Product> {
        return try {
            val products = getAllProducts().getOrThrow()
            // CORREGIDO: usar codigo en lugar de id
            val product = products.find { it.codigo == id }
            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return try {
            val products = getAllProducts().getOrThrow()
            val filtered = products.filter { it.categoria.equals(category, ignoreCase = true) }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val products = getAllProducts().getOrThrow()
            val filtered = products.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                it.descripcion.contains(query, ignoreCase = true)
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeProducts(): Flow<List<Product>> = _productsFlow.asStateFlow()
}