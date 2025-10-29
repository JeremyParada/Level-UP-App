package com.levelup.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id_categoria")
    val id: String,

    @SerializedName("nombre_categoria")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("total_productos")
    val totalProductos: Int
)