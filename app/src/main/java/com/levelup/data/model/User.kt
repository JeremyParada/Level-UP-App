package com.levelup.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val password: String,
    val direccion: String = "",
    val avatarUrl: String = ""
)