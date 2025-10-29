package com.levelup.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.auth.AuthRepository
import com.levelup.data.auth.AuthResult
import com.levelup.data.model.User
import com.levelup.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Expose current logged user as a state flow by combining DataStore userId and repository
    val currentUser: StateFlow<User?> = sessionManager.userIdFlow
        .mapLatest { id ->
            if (id == null) null else authRepository.getUserById(id)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val res = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    sessionManager.saveUserId(res.user.id)
                    _uiState.update { it.copy(isLoading = false, user = res.user) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            }
        }
    }

    fun register(user: User, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val res = authRepository.register(user, password)) {
                is AuthResult.Success -> {
                    sessionManager.saveUserId(res.user.id)
                    _uiState.update { it.copy(isLoading = false, user = res.user) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiState.update { AuthUiState() }
        }
    }

    // === NUEVO CÓDIGO DENTRO DE AuthViewModel.kt ===

    fun updateUserInfo(
        userId: String,
        nombre: String,
        telefono: String?,
        direccion: String?
    ) {
        viewModelScope.launch {
            val current = authRepository.getUserById(userId)
            if (current != null) {
                val updatedUser = current.copy(
                    nombre = nombre,
                    telefono = telefono,
                    direccion = direccion
                )
                authRepository.updateUser(updatedUser)
                _uiState.update { it.copy(user = updatedUser) }
            }
        }
    }

    fun deleteUserInfo(userId: String) {
        viewModelScope.launch {
            val result = authRepository.deleteUserData(userId)
            if (result is AuthResult.Success) {
                _uiState.update { it.copy(user = result.user) }
            }
        }
    }


    fun getAllTestUsers() = authRepository.getAllUsers()
}
