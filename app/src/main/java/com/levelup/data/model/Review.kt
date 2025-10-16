package com.levelup.data.model

import java.util.Date

data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: Date,
    val helpful: Int = 0
)