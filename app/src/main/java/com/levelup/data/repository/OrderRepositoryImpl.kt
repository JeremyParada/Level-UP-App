package com.levelup.data.repository

import com.levelup.data.model.Order
import com.levelup.data.model.OrderRequest
import com.levelup.data.remote.ApiService
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OrderRepository {
    override suspend fun createOrder(order: OrderRequest): Result<Map<String, Any>> {
        return try {
            val response = apiService.createOrder(order)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al crear pedido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrdersByUser(userId: Long): Result<List<Order>> {
        return try {
            val response = apiService.getOrdersByUser(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al obtener pedidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
