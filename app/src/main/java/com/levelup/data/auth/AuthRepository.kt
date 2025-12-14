package com.levelup.data.auth

import com.levelup.data.model.User
import kotlinx.coroutines.flow.Flow

sealed class AuthResult {
    data class Success(val user: User): AuthResult()
    data class Error(val message: String): AuthResult()
}

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(registro: com.levelup.data.remote.dto.RegistroDTO): AuthResult
    suspend fun getUserById(id: Long): User?
    // getAllUsers removed
    suspend fun updateUser(id: Long, nombre: String, telefono: String?): AuthResult
    // deleteUserData logic might still be relevant
    suspend fun deleteUserData(userId: Long): AuthResult
}
