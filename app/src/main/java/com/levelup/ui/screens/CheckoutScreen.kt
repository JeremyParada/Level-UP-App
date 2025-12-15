package com.levelup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.levelup.data.model.Direccion
import com.levelup.data.model.OrderProductRequest
import com.levelup.data.model.OrderRequest
import com.levelup.viewmodel.CartViewModel
import com.levelup.viewmodel.OrderUiState
import com.levelup.viewmodel.OrderViewModel
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
        navController: NavController,
        cartViewModel: CartViewModel = hiltViewModel(),
        orderViewModel: OrderViewModel = hiltViewModel(),
        authViewModel: com.levelup.ui.auth.AuthViewModel = hiltViewModel()
) {
    val cartItems by cartViewModel.uiState.collectAsState()
    val addresses by authViewModel.addresses.collectAsState()
    val user by authViewModel.currentUser.collectAsState()
    var selectedAddress by remember { mutableStateOf<Direccion?>(null) }
    var paymentMethod by rememberSaveable { mutableStateOf("Efectivo") }
    val orderState by orderViewModel.orderUiState.collectAsState()
    val isLoading = orderState is OrderUiState.Loading
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(paddingValues)) {
            Text("Confirmar compra", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Dirección de envío:", style = MaterialTheme.typography.titleMedium)
            addresses.forEach { address ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                            selected = selectedAddress == address,
                            onClick = { selectedAddress = address }
                    )
                    Text("${address.calle} ${address.numero}, ${address.comuna}")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Método de pago:", style = MaterialTheme.typography.titleMedium)
            Row {
                RadioButton(
                        selected = paymentMethod == "Efectivo",
                        onClick = { paymentMethod = "Efectivo" }
                )
                Text("Efectivo")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                        selected = paymentMethod == "Tarjeta",
                        onClick = { paymentMethod = "Tarjeta" }
                )
                Text("Tarjeta")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Resumen del carrito:", style = MaterialTheme.typography.titleMedium)
            cartItems.items.forEach { item ->
                val total = item.product.precio * item.quantity
                Text("${item.product.nombreProducto} x${item.quantity} - \$${total}")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                    onClick = {
                        if (selectedAddress != null && user != null) {
                            // Validate stock and product status
                            val invalidItems =
                                    cartItems.items.filter { item ->
                                        item.quantity > item.product.stock ||
                                                item.product.estadoProducto != "ACTIVO"
                                    }

                            if (invalidItems.isNotEmpty()) {
                                coroutineScope.launch {
                                    val outOfStockItems =
                                            invalidItems.filter { it.quantity > it.product.stock }
                                    val inactiveItems =
                                            invalidItems.filter {
                                                it.product.estadoProducto != "ACTIVO"
                                            }

                                    val message =
                                            when {
                                                outOfStockItems.isNotEmpty() ->
                                                        "Algunos productos no tienen stock suficiente: ${outOfStockItems.joinToString { it.product.nombreProducto }}"
                                                inactiveItems.isNotEmpty() ->
                                                        "Algunos productos ya no están disponibles: ${inactiveItems.joinToString { it.product.nombreProducto }}"
                                                else ->
                                                        "Hay problemas con algunos productos en tu carrito"
                                            }
                                    snackbarHostState.showSnackbar(message)
                                }
                            } else {
                                val productos =
                                        cartItems.items.map {
                                            OrderProductRequest(
                                                    codigo = it.product.codigoProducto,
                                                    cantidad = it.quantity
                                            )
                                        }
                                val currentUser = user
                                val currentAddress = selectedAddress
                                if (currentUser != null && currentAddress?.idDireccion != null) {
                                    val order =
                                            OrderRequest(
                                                    idUsuario = currentUser.idUsuario,
                                                    idDireccion = currentAddress.idDireccion,
                                                    metodoPago = paymentMethod,
                                                    productos = productos
                                            )
                                    orderViewModel.createOrder(order)
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                        "Selecciona dirección y asegúrate de estar logueado."
                                )
                            }
                        }
                    },
                    enabled = !isLoading && selectedAddress != null && cartItems.items.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Confirmar pedido")
            }

            when (orderState) {
                is OrderUiState.Success -> {
                    LaunchedEffect(orderState) {
                        val successState = orderState as OrderUiState.Success
                        val message =
                                if (successState.puntos != null) {
                                    "${successState.message}. ¡Ganaste ${successState.puntos} puntos!"
                                } else {
                                    successState.message
                                }
                        snackbarHostState.showSnackbar(message)
                        cartViewModel.clearCart()
                        navController.navigate("order_history") {
                            popUpTo("cart") { inclusive = true }
                        }
                    }
                }
                is OrderUiState.Error -> {
                    LaunchedEffect(orderState) {
                        snackbarHostState.showSnackbar((orderState as OrderUiState.Error).message)
                    }
                }
                else -> {}
            }
        }
    }
}
