package com.levelup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.levelup.data.model.Product
import com.levelup.data.repository.ProductRepository
import com.levelup.ui.theme.LevelUpPrimary
import com.levelup.ui.theme.LevelUpSecondary
import com.levelup.utils.Formatters
import com.levelup.viewmodel.CartViewModel
import com.levelup.viewmodel.ProductViewModel
import com.levelup.viewmodel.ProductViewModel.ProductDetailUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    viewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    var quantity by remember { mutableStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val productDetailState by viewModel.productDetailState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LevelUpPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (productDetailState is ProductDetailUiState.Success) {
                val prod = (productDetailState as ProductDetailUiState.Success).product
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatCurrency(prod.precio * quantity),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = LevelUpPrimary
                            )
                        }

                        Button(
                            onClick = {
                                cartViewModel.addToCart(prod, quantity)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Producto agregado al carrito",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LevelUpPrimary
                            )
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar al Carrito")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when (productDetailState) {
            is ProductDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProductDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (productDetailState as ProductDetailUiState.Error).message ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ProductDetailUiState.Success -> {
                val product = (productDetailState as ProductDetailUiState.Success).product
                ProductDetailContent(
                    product = product,
                    quantity = quantity,
                    onQuantityChange = { newQuantity ->
                        if (newQuantity > 0) quantity = newQuantity
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Imagen del producto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(LevelUpPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = product.nombre.take(3).uppercase(),
                style = MaterialTheme.typography.displayLarge,
                color = LevelUpPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Categoría
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = LevelUpSecondary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = product.categoria,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = LevelUpSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre del producto
            Text(
                text = product.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Código del producto
            Text(
                text = "Código: ${product.codigo}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Precio
            Text(
                text = Formatters.formatCurrency(product.precio),
                style = MaterialTheme.typography.headlineLarge,
                color = LevelUpPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de cantidad
            Text(
                text = "Cantidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(quantity - 1) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = LevelUpPrimary.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Disminuir",
                        tint = LevelUpPrimary
                    )
                }

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                IconButton(
                    onClick = { onQuantityChange(quantity + 1) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = LevelUpPrimary
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Aumentar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descripción
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.5f)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}