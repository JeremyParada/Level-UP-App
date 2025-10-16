package com.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.model.CartItem
import com.levelup.data.model.Product
import com.levelup.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val itemCount: Int = 0
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = cartRepository.cartItems
        .map { items ->
            CartUiState(
                items = items,
                total = cartRepository.getTotal(),
                itemCount = cartRepository.getItemCount()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState()
        )

    fun addToCart(product: Product, quantity: Int = 1) {
        cartRepository.addToCart(product, quantity)
    }

    fun removeFromCart(productId: String) {
        cartRepository.removeFromCart(productId)
    }

    fun updateQuantity(productId: String, quantity: Int) {
        cartRepository.updateQuantity(productId, quantity)
    }

    fun clearCart() {
        cartRepository.clearCart()
    }
}