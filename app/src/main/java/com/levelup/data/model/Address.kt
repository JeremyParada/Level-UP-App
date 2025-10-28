package com.levelup.data.model

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("id_direccion")
    val id: String? = null,

    @SerializedName("id_usuario")
    val userId: String,

    @SerializedName("tipo_direccion")
    val type: String,

    @SerializedName("calle")
    val street: String,

    @SerializedName("numero")
    val number: String,

    @SerializedName("comuna")
    val district: String,

    @SerializedName("ciudad")
    val city: String,

    @SerializedName("region")
    val region: String,

    @SerializedName("codigo_postal")
    val postalCode: String,

    @SerializedName("es_principal")
    val isPrimary: Boolean
)