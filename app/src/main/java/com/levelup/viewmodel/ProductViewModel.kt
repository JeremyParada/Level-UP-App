package com.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.model.Category
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

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(val categories: List<Category>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryUiState: StateFlow<CategoryUiState> = _categoryUiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _productDetailState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val productDetailState: StateFlow<ProductDetailUiState> = _productDetailState.asStateFlow()

    init {
        loadProducts()
        loadCategories()
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

    fun loadCategories() {
        viewModelScope.launch {
            _categoryUiState.value = CategoryUiState.Loading
            repository.getCategories()
                .onSuccess { categories ->
                    _categoryUiState.value = CategoryUiState.Success(categories)
                }
                .onFailure { error ->
                    _categoryUiState.value = CategoryUiState.Error(error.message ?: "Error desconocido")
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

    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _productDetailState.value = ProductDetailUiState.Loading
            repository.getProductById(productId)
                .onSuccess { product ->
                    _productDetailState.value = ProductDetailUiState.Success(product)
                }
                .onFailure { error ->
                    _productDetailState.value = ProductDetailUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    sealed class ProductDetailUiState {
        object Loading : ProductDetailUiState()
        data class Success(val product: Product) : ProductDetailUiState()
        data class Error(val message: String) : ProductDetailUiState()
    }
}