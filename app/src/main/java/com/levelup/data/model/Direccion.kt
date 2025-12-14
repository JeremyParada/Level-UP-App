package com.levelup.data.model

import com.google.gson.annotations.SerializedName

data class Direccion(
    @SerializedName("idDireccion")
    val id: Long? = null,
    val calle: String,
    val numero: String,
    val comuna: String,
    val ciudad: String,
    val region: String,
    val codigoPostal: String,
    val idUsuario: Long? = null
)
