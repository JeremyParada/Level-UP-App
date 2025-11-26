package com.levelup.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import com.levelup.data.model.Category
import com.levelup.data.model.Product
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class ProductsGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sampleProducts(): List<Product> {
        return listOf(
            Product(
                idProducto = 1,
                categoria = Category(id = 3, nombre = "Consolas", descripcion = "Consolas de videojuegos"),
                codigoProducto = "CON001",
                nombreProducto = "Nintendo Switch",
                precio = 299990.0,
                descripcion = "Consola portátil",
                stock = 10,
                estadoProducto = "ACTIVO",
                fechaCreacion = "2025-11-20T06:18:44",
                imagen = "/assets/img/con001.jpg"
            ),
            Product(
                idProducto = 2,
                categoria = Category(id = 6, nombre = "Mouse", descripcion = "Mouse gaming"),
                codigoProducto = "MOU001",
                nombreProducto = "Mouse Gamer Logitech",
                precio = 39990.0,
                descripcion = "Mouse con RGB",
                stock = 30,
                estadoProducto = "ACTIVO",
                fechaCreacion = "2025-11-20T06:18:44",
                imagen = "/assets/img/mou001.jpg"
            )
        )
    }

    @Test
    fun products_are_displayed_in_grid() {
        val mockNavController = mockk<NavController>(relaxed = true)
        val products = sampleProducts()

        composeTestRule.setContent {
            ProductsGrid(
                products = products,
                navController = mockNavController
            )
        }

        // Validar que exista cada nombre de producto
        composeTestRule.onNodeWithText("Nintendo Switch").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mouse Gamer Logitech").assertIsDisplayed()

        // Validar que exista cada categoría
        composeTestRule.onNodeWithText("Consolas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mouse").assertIsDisplayed()

        // Validar que aparezcan los precios
        composeTestRule.onNode(hasText("299", substring = true)).assertExists()
        composeTestRule.onNode(hasText("39", substring = true)).assertExists()

        // Validar que el número de nodos que contienen los nombres coincide con la lista
        composeTestRule
            .onAllNodes(hasAnySibling(hasText("Nintendo Switch")) or hasAnySibling(hasText("Mouse Gamer Logitech")))
            .apply {
                // al menos los dos nombres deben existir; usamos assertCountEquals sobre los nombres directos:
                composeTestRule.onAllNodes(hasText("Nintendo Switch") or hasText("Mouse Gamer Logitech"))
                    .assertCountEquals(2)
            }
    }
}
