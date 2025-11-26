package com.levelup.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("idProducto")
    val idProducto: Int,

    @SerializedName("categoria")
    val categoria: Category,

    @SerializedName("codigoProducto")
    val codigoProducto: String,

    @SerializedName("nombreProducto")
    val nombreProducto: String,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("estadoProducto")
    val estadoProducto: String,

    @SerializedName("fechaCreacion")
    val fechaCreacion: String,

    @SerializedName("imagen")
    val imagen: String
)
