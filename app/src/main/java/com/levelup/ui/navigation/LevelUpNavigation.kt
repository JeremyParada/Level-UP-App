package com.levelup.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.levelup.ui.screens.*
import com.levelup.viewmodel.AuthViewModel

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpNavigation(startAtHome: Boolean, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, Icons.Default.Home, "Inicio"),
        BottomNavItem(Screen.Products.route, Icons.Default.Store, "Productos"),
        BottomNavItem(Screen.Cart.route, Icons.Default.ShoppingCart, "Carrito"),
        BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Perfil")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            // Solo permitir acceder a Perfil si está logeado
                            if (item.route == Screen.Profile.route && !authViewModel.isLoggedIn.value) {
                                navController.navigate(Screen.Login.route)
                                return@NavigationBarItem
                            }

                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (startAtHome) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }

            composable(Screen.Products.route) {
                ProductsScreen(navController)
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                ProductDetailScreen(
                    productId = productId ?: "",
                    navController = navController
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(navController)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }

            composable(Screen.Community.route) {
                CommunityScreen(navController)
            }

            composable(Screen.Reviews.route) {
                ReviewsScreen(navController)
            }

            // ✅ Pantallas de autenticación usan hiltViewModel() aquí
            composable(Screen.Register.route) {
                val authViewModel = hiltViewModel<com.levelup.viewmodel.AuthViewModel>()
                RegisterScreen(navController, authViewModel)
            }

            composable(Screen.Login.route) {
                val authViewModel = hiltViewModel<com.levelup.viewmodel.AuthViewModel>()
                LoginScreen(navController, authViewModel)
            }
        }
    }
}
