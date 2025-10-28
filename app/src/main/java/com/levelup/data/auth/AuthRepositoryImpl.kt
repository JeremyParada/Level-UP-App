package com.levelup.data.auth

import com.levelup.data.model.User
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación simple en memoria con 3 usuarios precargados para pruebas.
 * NOTA: las contraseñas están en texto claro solo para pruebas locales.
 * En producción usar un backend y hashing seguro.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(): AuthRepository {

    // mapa email -> Pair<User, password>
    private val users = mutableMapOf<String, Pair<User, String>>().apply {
        put("alice@test.com", Pair(User(id = "u1", nombre = "Alice", email = "alice@test.com", telefono = "099111111", direccion = "Calle A 1", avatarUrl = null), "password1"))
        put("bob@test.com", Pair(User(id = "u2", nombre = "Bob", email = "bob@test.com", telefono = "099222222", direccion = "Calle B 2", avatarUrl = null), "password2"))
        put("carla@test.com", Pair(User(id = "u3", nombre = "Carla", email = "carla@test.com", telefono = "099333333", direccion = "Calle C 3", avatarUrl = null), "password3"))
    }

    override suspend fun login(email: String, password: String): AuthResult {
        val entry = users[email.lowercase()]
        return if (entry != null && entry.second == password) {
            AuthResult.Success(entry.first)
        } else {
            AuthResult.Error("Correo o contraseña incorrectos")
        }
    }

    override suspend fun register(user: User, password: String): AuthResult {
        val emailKey = user.email.lowercase()
        if (users.containsKey(emailKey)) {
            return AuthResult.Error("Ya existe una cuenta con ese correo")
        }
        val id = UUID.randomUUID().toString()
        val newUser = user.copy(id = id)
        users[emailKey] = Pair(newUser, password)
        return AuthResult.Success(newUser)
    }

    override suspend fun getUserById(id: String): User? {
        return users.values.map { it.first }.firstOrNull { it.id == id }
    }

    override fun getAllUsers(): List<User> {
        return users.values.map { it.first }
    }

    override suspend fun updateUser(user: User): AuthResult {
        val existingEntry = users.values.firstOrNull { it.first.id == user.id }
        return if (existingEntry != null) {
            val emailKey = existingEntry.first.email.lowercase()
            val password = users[emailKey]?.second ?: "password"
            users[emailKey] = Pair(user, password)
            AuthResult.Success(user)
        } else {
            AuthResult.Error("Usuario no encontrado")
        }
    }

    override suspend fun deleteUserData(userId: String): AuthResult {
        val emailKey = users.entries.find { it.value.first.id == userId }?.key
        return if (emailKey != null) {
            val user = users[emailKey]?.first
            val password = users[emailKey]?.second
            // Eliminar teléfono y dirección (mantener cuenta y correo)
            if (user != null) {
                val updatedUser = user.copy(telefono = null, direccion = null)
                users[emailKey] = Pair(updatedUser, password ?: "")
                AuthResult.Success(updatedUser)
            } else {
                AuthResult.Error("Usuario no encontrado")
            }
        } else {
            AuthResult.Error("Usuario no encontrado")
        }
    }


}
