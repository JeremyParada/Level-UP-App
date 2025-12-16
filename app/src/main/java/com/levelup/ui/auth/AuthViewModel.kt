package com.levelup.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.auth.AuthRepository
import com.levelup.data.auth.AuthResult
import com.levelup.data.model.Direccion
import com.levelup.data.model.User
import com.levelup.data.remote.dto.RegistroDTO
import com.levelup.data.repository.AddressRepository
import com.levelup.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthUiState(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModel
@Inject
constructor(
        private val authRepository: AuthRepository,
        private val sessionManager: SessionManager,
        private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Events for UI (Navigation, Toasts)
    sealed class AuthEvent {
        object RegisterSuccess : AuthEvent()
        object LoginSuccess : AuthEvent()
        data class Error(val message: String) : AuthEvent()
    }

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent: SharedFlow<AuthEvent> = _authEvent.asSharedFlow()

    // Address State
    private val _addresses = MutableStateFlow<List<Direccion>>(emptyList())
    val addresses: StateFlow<List<Direccion>> = _addresses.asStateFlow()

    // Current User Flow
    // Fixed: Combine userId and authToken to ensure both are present before fetching user.
    // This prevents the 401 error where userId is ready but token is not yet in the flow.
    // Flag to indicate if we are initially checking the session
    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession.asStateFlow()

    // Current User Flow
    // Fixed: Combine userId and authToken to ensure both are present before fetching user.
    // This prevents the 401 error where userId is ready but token is not yet in the flow.
    val currentUser: StateFlow<User?> =
            combine(sessionManager.userIdFlow, sessionManager.authTokenFlow) { userId, token ->
                        if (userId != null && !token.isNullOrEmpty()) {
                            userId
                        } else {
                            null
                        }
                    }
                    .onEach {
                        // Once the flow emits (even null), we have checked the session source
                        // We might want to wait for the repo fetch too?
                        // Actually, onEach runs before mapLatest.
                        // Let's rely on the collection to update loading.
                    }
                    .distinctUntilChanged()
                    .mapLatest { id ->
                        if (id == null) {
                            _isCheckingSession.value = false // No user, check done
                            null
                        } else {
                            val user = authRepository.getUserById(id)
                            _isCheckingSession.value = false // User fetched (or null), check done
                            user
                        }
                    }
                    .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Load addresses when user changes
    init {
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    loadAddresses(user.idUsuario)
                    // Sync UI state user with fetched user
                    _uiState.update { it.copy(user = user) }
                } else {
                    _addresses.value = emptyList()
                    _uiState.update { it.copy(user = null) }
                }
            }
        }
    }

    fun loadAddresses(userId: Long) {
        viewModelScope.launch {
            try {
                _addresses.value = addressRepository.getAddresses(userId)
            } catch (e: Exception) {
                // Handle error gracefully or log
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val res = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    // Session is saved in Repository. UI will update via currentUser flow.
                    _uiState.update { it.copy(isLoading = false, user = res.user) }
                    _authEvent.emit(AuthEvent.LoginSuccess)
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                    _authEvent.emit(AuthEvent.Error(res.message))
                }
            }
        }
    }

    fun register(registro: RegistroDTO) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val res = authRepository.register(registro)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                    _authEvent.emit(AuthEvent.RegisterSuccess)
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                    _authEvent.emit(AuthEvent.Error(res.message))
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            // State updates handled by flow collection
        }
    }

    // Address Management
    fun addAddress(direccion: Direccion) {
        val user = currentUser.value
        android.util.Log.d("AuthViewModel", "addAddress called. CurrentUser: ${user?.idUsuario}")
        if (user == null) {
            android.util.Log.e("AuthViewModel", "Cannot add address: User is null")
            return
        }
        viewModelScope.launch {
            val success =
                    addressRepository.createAddress(direccion.copy(idUsuario = user.idUsuario))
            if (success) loadAddresses(user.idUsuario)
        }
    }

    fun updateAddress(direccion: Direccion) {
        val user = currentUser.value ?: return
        val id = direccion.idDireccion ?: return
        viewModelScope.launch {
            val success = addressRepository.updateAddress(id, direccion)
            if (success) loadAddresses(user.idUsuario)
        }
    }

    fun deleteAddress(addressId: Long) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val success = addressRepository.deleteAddress(addressId)
            if (success) loadAddresses(user.idUsuario)
        }
    }

    fun updateUserInfo(userId: Long, nombre: String, telefono: String?) {
        viewModelScope.launch {
            // Placeholder: AuthRepository.updateUser likely returns a result
            // Assuming simplified calling for now as per previous code
            authRepository.updateUser(userId, nombre, telefono)
        }
    }

    fun deleteUserInfo(userId: Long) {
        viewModelScope.launch {
            authRepository.deleteUserData(userId)
            logout()
        }
    }
}
