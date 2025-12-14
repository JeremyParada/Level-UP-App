package com.levelup.data.model

data class User(
    val idUsuario: Long, // Renamed from id to match JSON key exactly
    val nombre: String,
    val email: String,
    val telefono: String? = null
    // Address is now a separate entity
    // AvatarUrl removed if not used or add back if needed
)