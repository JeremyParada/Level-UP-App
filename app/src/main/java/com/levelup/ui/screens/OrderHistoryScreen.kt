package com.levelup.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.levelup.viewmodel.OrderHistoryUiState
import com.levelup.viewmodel.OrderViewModel

@Composable
fun OrderHistoryScreen(
    navController: NavController,
    userId: Long,
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    val orderHistoryState by orderViewModel.orderHistoryState.collectAsState()

    LaunchedEffect(userId) {
        orderViewModel.getOrdersByUser(userId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Pedidos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        when (orderHistoryState) {
            is OrderHistoryUiState.Loading -> {
                CircularProgressIndicator()
            }
            is OrderHistoryUiState.Error -> {
                Text((orderHistoryState as OrderHistoryUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            is OrderHistoryUiState.Success -> {
                val orders = (orderHistoryState as OrderHistoryUiState.Success).orders
                if (orders.isEmpty()) {
                    Text("No tienes pedidos previos.")
                } else {
                    orders.forEach { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { /* navController.navigate("orderDetail/${order.idPedido}") */ },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Pedido #${order.idPedido}", style = MaterialTheme.typography.titleMedium)
                                Text("Fecha: ${order.fechaPedido ?: "-"}")
                                Text("Total: $${order.totalNeto}")
                                Text("Estado: ${order.estadoPedido}")
                            }
                        }
                    }
                }
            }
        }
    }
}
