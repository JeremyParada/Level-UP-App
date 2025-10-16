package com.levelup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.levelup.data.model.Product
import com.levelup.ui.navigation.Screen
import com.levelup.ui.theme.LevelUpPrimary
import com.levelup.ui.theme.LevelUpSecondary
import com.levelup.utils.Formatters
import com.levelup.viewmodel.ProductUiState
import com.levelup.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Productos",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = LevelUpPrimary,
                titleContentColor = Color.White
            )
        )

        // Barra de búsqueda
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        viewModel.searchProducts(it)
                    } else {
                        viewModel.loadProducts()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar productos...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        // Filtros por categoría
        CategoriesFilterSection(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                viewModel.filterByCategory(category)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de productos
        when (val state = uiState) {
            is ProductUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProductUiState.Success -> {
                if (state.products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron productos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    ProductsGrid(
                        products = state.products,
                        navController = navController
                    )
                }
            }
            is ProductUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error al cargar productos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { viewModel.loadProducts() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriesFilterSection(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    val categories = listOf(
        "Todos",
        "Juegos de Mesa",
        "Accesorios",
        "Consolas",
        "Computadores Gamers",
        "Sillas Gamers",
        "Mouse",
        "Mousepad",
        "Poleras Personalizadas"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = if (category == "Todos") {
                    selectedCategory == null
                } else {
                    selectedCategory == category
                },
                onClick = {
                    onCategorySelected(if (category == "Todos") null else category)
                },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LevelUpPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ProductsGrid(products: List<Product>, navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductGridItem(
                product = product,
                onClick = {
                    navController.navigate(Screen.ProductDetail.createRoute(product.codigo))
                }
            )
        }
    }
}

@Composable
fun ProductGridItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen del producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(LevelUpPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.nombre.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = LevelUpPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                // Categoría
                Text(
                    text = product.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = LevelUpSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Nombre del producto
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Precio
                Text(
                    text = Formatters.formatCurrency(product.precio),
                    style = MaterialTheme.typography.titleMedium,
                    color = LevelUpPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}