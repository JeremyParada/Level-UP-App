package com.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.model.Product
import com.levelup.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(val products: List<Product>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            repository.getAllProducts()
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { error ->
                    _uiState.value = ProductUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun filterByCategory(category: String?) {
        viewModelScope.launch {
            _selectedCategory.value = category
            _uiState.value = ProductUiState.Loading

            if (category == null) {
                loadProducts()
            } else {
                repository.getProductsByCategory(category)
                    .onSuccess { products ->
                        _uiState.value = ProductUiState.Success(products)
                    }
                    .onFailure { error ->
                        _uiState.value = ProductUiState.Error(error.message ?: "Error desconocido")
                    }
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            repository.searchProducts(query)
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { error ->
                    _uiState.value = ProductUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }
}