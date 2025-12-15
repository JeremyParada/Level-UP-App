package com.levelup.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.levelup.data.model.Direccion
import com.levelup.data.model.User
import org.junit.Rule
import org.junit.Test

class AddressScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleUser = User(
        idUsuario = 1L,
        nombre = "Juan Perez",
        email = "juan@example.com",
        telefono = "123456789"
    )

    private val sampleAddresses = listOf(
        Direccion(
            id = 1L,
            calle = "Av. Siempre Viva",
            numero = "742",
            comuna = "Springfield",
            ciudad = "Springfield",
            region = "Metropolitana",
            codigoPostal = "12345",
            idUsuario = 1L
        ),
        Direccion(
            id = 2L,
            calle = "Calle Falsa",
            numero = "123",
            comuna = "Centro",
            ciudad = "Santiago",
            region = "Metropolitana",
            codigoPostal = "54321",
            idUsuario = 1L
        )
    )

    @Test
    fun displays_address_list_correctly() {
        composeTestRule.setContent {
            AddressContent(
                currentUser = sampleUser,
                addresses = sampleAddresses,
                onBackClick = {},
                onAddAddress = {},
                onUpdateAddress = {},
                onDeleteAddress = {}
            )
        }

        // Verify User Info Header
        composeTestRule.onNodeWithText("Juan Perez").assertIsDisplayed()
        composeTestRule.onNodeWithText("juan@example.com").assertIsDisplayed()

        // Verify Address Items
        composeTestRule.onNodeWithText("Av. Siempre Viva 742").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calle Falsa 123").assertIsDisplayed()
    }

    @Test
    fun displays_empty_state_when_no_addresses() {
        composeTestRule.setContent {
            AddressContent(
                currentUser = sampleUser,
                addresses = emptyList(),
                onBackClick = {},
                onAddAddress = {},
                onUpdateAddress = {},
                onDeleteAddress = {}
            )
        }

        composeTestRule.onNodeWithText("No tienes direcciones guardadas.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Av. Siempre Viva 742").assertDoesNotExist()
    }

    @Test
    fun clicking_add_button_shows_new_address_dialog() {
        composeTestRule.setContent {
            AddressContent(
                currentUser = sampleUser,
                addresses = sampleAddresses,
                onBackClick = {},
                onAddAddress = {},
                onUpdateAddress = {},
                onDeleteAddress = {}
            )
        }

        // Click on Floating Action Button (contentDescription "Agregar Dirección")
        composeTestRule.onNodeWithContentDescription("Agregar Dirección").performClick()

        // Verify Dialog Title
        composeTestRule.onNodeWithText("Nueva Dirección").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calle").assertIsDisplayed()
    }

    @Test
    fun clicking_edit_button_shows_edit_dialog_prefilled() {
        composeTestRule.setContent {
            AddressContent(
                currentUser = sampleUser,
                addresses = sampleAddresses,
                onBackClick = {},
                onAddAddress = {},
                onUpdateAddress = {},
                onDeleteAddress = {}
            )
        }

        // Find Edit button for first item (AddressManagementItem uses Icon with null contentDescription)
        // inside Row -> TextButton -> Icon. 
        // Or TextButton contains Text("Editar"). Let's stick to Text "Editar".
        // There are multiple "Editar" buttons. Let's pick the first one.
        composeTestRule.onAllNodesWithText("Editar").onFirst().performClick()

        // Verify Dialog Title
        composeTestRule.onNodeWithText("Editar Dirección").assertIsDisplayed()
        
        // Verify pre-filled data (TextFields containing value)
        // Since "Av. Siempre Viva" is the first item in logic usually (LazyColumn order matches list)
        composeTestRule.onNodeWithText("Av. Siempre Viva").assertIsDisplayed()
        composeTestRule.onNodeWithText("742").assertIsDisplayed()
    }
}
