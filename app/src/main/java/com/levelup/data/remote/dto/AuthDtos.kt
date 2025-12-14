package com.levelup.data.remote.dto

data class LoginDTO(
    val email: String,
    val password: String
)

data class RegistroDTO(
    val nombre: String,
    val apellido: String = "", // Backend require apellido but UI might not have it yet, default to empty
    val email: String,
    val password: String,
    val telefono: String = "",
    val fechaNacimiento: String = "", // "yyyy-MM-dd"
    // Optional address fields for registration
    val calle: String = "",
    val ciudad: String = "",
    val comuna: String = "",
    val region: String = "",
    val numero: String = "",
    val codigoPostal: String = "",
    val pais: String = "Chile",
    val codigoReferido: String? = null
)

data class JwtResponseDTO(
    val token: String, // JSON field is "token"
    val id: Long,
    val username: String, // JSON field is "username"
    val email: String,
    val type: String // JSON field "type"
)
