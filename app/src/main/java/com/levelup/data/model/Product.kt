// filepath: app/src/main/java/com/levelup/data/model/Product.kt
package com.levelup.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("codigo")
    val codigo: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("imagen")
    val imagen: String,

    @SerializedName("categoria")
    val categoria: String,

    @SerializedName("stock")
    val stock: Int = 0,

    @SerializedName("descuento")
    val descuento: Int? = null,

    @SerializedName("valoracion")
    val valoracion: Double? = null
) {
    // Propiedad calculada para mantener compatibilidad con código que use 'id'
    val id: String
        get() = codigo
}