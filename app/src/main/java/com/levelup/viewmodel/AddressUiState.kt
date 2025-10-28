package com.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.model.Address
import com.levelup.data.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddressUiState {
    object Loading : AddressUiState()
    data class Success(val addresses: List<Address>) : AddressUiState()
    data class Error(val message: String) : AddressUiState()
}

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddressUiState>(AddressUiState.Loading)
    val uiState: StateFlow<AddressUiState> = _uiState

    fun loadAddresses(userId: String) {
        viewModelScope.launch {
            _uiState.value = AddressUiState.Loading
            repository.getAddressesByUser(userId)
                .onSuccess { addresses ->
                    _uiState.value = AddressUiState.Success(addresses)
                }
                .onFailure { error ->
                    _uiState.value = AddressUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun createAddress(address: Address) {
        viewModelScope.launch {
            repository.createAddress(address)
        }
    }
}