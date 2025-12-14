package com.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.model.Direccion
import com.levelup.data.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddressUiState {
    object Loading : AddressUiState()
    data class Success(val addresses: List<Direccion>) : AddressUiState()
    data class Error(val message: String) : AddressUiState()
}

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddressUiState>(AddressUiState.Loading)
    val uiState: StateFlow<AddressUiState> = _uiState

    fun loadAddresses(userId: Long) {
        viewModelScope.launch {
            _uiState.value = AddressUiState.Loading
            try {
                val addresses = repository.getAddresses(userId)
                _uiState.value = AddressUiState.Success(addresses)
            } catch (e: Exception) {
                _uiState.value = AddressUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createAddress(direccion: Direccion) {
        viewModelScope.launch {
            // Logic to create address and reload would usually happen here
            // dependent on userId availability. 
            // For now just calling repository
            try {
                repository.createAddress(direccion)
                // Need userId to reload. Assuming caller manages flow or we store userId.
                // For simplicity, just relying on success.
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}