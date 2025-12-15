package com.levelup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.levelup.data.model.Order
import com.levelup.data.model.OrderDetail

@Composable
fun OrderDetailScreen(order: Order) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalle del Pedido #${order.idPedido}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Fecha: ${order.fechaPedido ?: "-"}")
        Text("Dirección: ${order.direccionEnvio?.calle ?: "-"} ${order.direccionEnvio?.numero ?: ""}")
        Text("Método de pago: ${order.metodoPago}")
        Text("Estado: ${order.estadoPedido}")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Productos:", style = MaterialTheme.typography.titleMedium)
        order.detalles.forEach { detail ->
            OrderProductRow(detail)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: $${order.totalNeto}", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun OrderProductRow(detail: OrderDetail) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(detail.producto.nombreProducto)
        Text("x${detail.cantidad}")
        Text("$${detail.precioUnitario * detail.cantidad}")
    }
}
