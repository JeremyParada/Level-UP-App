package com.levelup.data.repository

import com.levelup.data.model.Product
import com.levelup.data.model.Category

interface ProductRepository {
    suspend fun getAllProducts(): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
    suspend fun getProductsByCategory(category: String): Result<List<Product>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun searchProducts(query: String): Result<List<Product>>
}