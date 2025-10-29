package com.levelup.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Products : Screen("products")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Community : Screen("community")
    object Reviews : Screen("reviews")
    object Register : Screen("register")
    object Login : Screen("login")
    object PersonalInfo : Screen("personal_info")
}