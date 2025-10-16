package com.levelup.utils

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    
    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return format.format(amount)
    }

    fun formatDiscount(discount: Int): String {
        return "-$discount%"
    }

    fun formatRating(rating: Double): String {
        return String.format(Locale.getDefault(), "%.1f", rating)
    }
}