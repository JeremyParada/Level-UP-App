package com.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.model.Order
import com.levelup.data.model.OrderRequest
import com.levelup.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrderUiState {
    object Idle : OrderUiState()
    object Loading : OrderUiState()
    data class Success(val message: String, val idPedido: Long? = null, val puntos: Int? = null) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

sealed class OrderHistoryUiState {
    object Loading : OrderHistoryUiState()
    data class Success(val orders: List<Order>) : OrderHistoryUiState()
    data class Error(val message: String) : OrderHistoryUiState()
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _orderUiState = MutableStateFlow<OrderUiState>(OrderUiState.Idle)
    val orderUiState: StateFlow<OrderUiState> = _orderUiState

    private val _orderHistoryState = MutableStateFlow<OrderHistoryUiState>(OrderHistoryUiState.Loading)
    val orderHistoryState: StateFlow<OrderHistoryUiState> = _orderHistoryState

    fun createOrder(order: OrderRequest) {
        _orderUiState.value = OrderUiState.Loading
        viewModelScope.launch {
            repository.createOrder(order)
                .onSuccess { map ->
                    val idPedido = (map["idPedido"] as? Number)?.toLong()
                    val puntos = (map["puntos"] as? Number)?.toInt()
                    _orderUiState.value = OrderUiState.Success(
                        message = map["mensaje"] as? String ?: "Pedido creado",
                        idPedido = idPedido,
                        puntos = puntos
                    )
                }
                .onFailure { e ->
                    _orderUiState.value = OrderUiState.Error(e.message ?: "Error al crear pedido")
                }
        }
    }

    fun getOrdersByUser(userId: Long) {
        _orderHistoryState.value = OrderHistoryUiState.Loading
        viewModelScope.launch {
            repository.getOrdersByUser(userId)
                .onSuccess { orders ->
                    _orderHistoryState.value = OrderHistoryUiState.Success(orders)
                }
                .onFailure { e ->
                    _orderHistoryState.value = OrderHistoryUiState.Error(e.message ?: "Error al obtener pedidos")
                }
        }
    }
}
