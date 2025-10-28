package com.levelup.data.auth

import com.levelup.data.model.User
import kotlinx.coroutines.flow.Flow

sealed class AuthResult {
    data class Success(val user: User): AuthResult()
    data class Error(val message: String): AuthResult()
}

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(user: User, password: String): AuthResult
    suspend fun getUserById(id: String): User?
    fun getAllUsers(): List<User>
    suspend fun updateUser(user: User): AuthResult
    suspend fun deleteUserData(userId: String): AuthResult
}
