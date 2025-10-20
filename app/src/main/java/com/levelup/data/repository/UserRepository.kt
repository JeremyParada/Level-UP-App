package com.levelup.data.repository

import com.levelup.data.UserDao
import com.levelup.data.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    suspend fun registerUser(user: User): Boolean {
        return try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                false
            } else {
                userDao.insertUser(user) > 0
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }

    suspend fun initializeSampleUsers() {
        if (userDao.getUserCount() == 0) {
            val sampleUsers = listOf(
                User(
                    nombre = "Ana",
                    apellido = "Gómez",
                    email = "ana@levelup.com",
                    telefono = "123456789",
                    password = "password123",
                    direccion = "Calle Principal 123",
                    avatarUrl = ""
                ),
                User(
                    nombre = "Carlos",
                    apellido = "Rodríguez",
                    email = "carlos@levelup.com",
                    telefono = "987654321",
                    password = "gamer2024",
                    direccion = "Avenida Central 456",
                    avatarUrl = ""
                ),
                User(
                    nombre = "María",
                    apellido = "López",
                    email = "maria@levelup.com",
                    telefono = "555666777",
                    password = "levelup123",
                    direccion = "Plaza Mayor 789",
                    avatarUrl = ""
                )
            )
            userDao.insertAll(sampleUsers)
        }
    }
}