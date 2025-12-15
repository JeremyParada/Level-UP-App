package com.levelup.data.model

data class Direccion(
    val idDireccion: Long? = null,
    val idUsuario: Long? = null,
    val tipoDireccion: String = "CASA", // CASA, TRABAJO, OTRO
    val calle: String,
    val numero: String,
    val comuna: String,
    val ciudad: String,
    val region: String,
    val codigoPostal: String,
    val esPrincipal: Int = 0 // 0 = no, 1 = sí
)

