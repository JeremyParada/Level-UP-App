package com.levelup.data.model

data class User(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val avatarUrl: String? = null
)