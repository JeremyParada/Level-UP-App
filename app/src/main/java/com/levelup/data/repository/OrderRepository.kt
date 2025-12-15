package com.levelup.data.repository

import com.levelup.data.model.Order
import com.levelup.data.model.OrderRequest

interface OrderRepository {
    suspend fun createOrder(order: OrderRequest): Result<Map<String, Any>>
    suspend fun getOrdersByUser(userId: Long): Result<List<Order>>
}
