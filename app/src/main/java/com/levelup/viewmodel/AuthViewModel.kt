package com.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.data.model.User
import com.levelup.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        // Inicializar usuarios de muestra
        viewModelScope.launch {
            userRepository.initializeSampleUsers()
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val user = userRepository.loginUser(email, password)
        _currentUser.value = user
        _isLoggedIn.value = user != null
        return user != null
    }

    suspend fun registerUser(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        password: String
    ): Boolean {
        val user = User(
            nombre = nombre,
            apellido = apellido,
            email = email,
            telefono = telefono,
            password = password,
            direccion = "",
            avatarUrl = ""
        )
        val success = userRepository.registerUser(user)
        if (success) {
            _currentUser.value = user
            _isLoggedIn.value = true
        }
        return success
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun getCurrentUser(): User? {
        return _currentUser.value
    }
}