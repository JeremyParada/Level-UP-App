package com.levelup.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.levelup.data.model.User
import com.levelup.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // --- Estados de formulario ---
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    // Snackbar y coroutine
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Crear cuenta",
                    style = MaterialTheme.typography.headlineMedium
                )

                // --- Campos del formulario ---
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(image, contentDescription = "Mostrar/Ocultar contraseña")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmarPassword,
                    onValueChange = { confirmarPassword = it },
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmarPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) {
                            Icon(image, contentDescription = "Mostrar/Ocultar contraseña")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Botón de registro ---
                Button(
                    onClick = {
                        when {
                            !validateForm(nombre, email, telefono, password, confirmarPassword) -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Completa todos los campos correctamente.")
                                }
                            }
                            password != confirmarPassword -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Las contraseñas no coinciden.")
                                }
                            }
                            else -> {
                                isLoading = true
                                authViewModel.register(
                                    user = User(
                                        id = "",
                                        nombre = nombre,
                                        email = email,
                                        telefono = telefono,
                                        direccion = null,
                                        avatarUrl = null
                                    ),
                                    password = password
                                )

                                scope.launch {
                                    authViewModel.uiState.collect { state ->
                                        isLoading = state.isLoading

                                        state.error?.let {
                                            snackbarHostState.showSnackbar(it)
                                        }

                                        state.user?.let {
                                            snackbarHostState.showSnackbar("¡Cuenta creada con éxito!")
                                            navController.navigateUp()
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Registrarse")
                    }
                }

                // --- Enlace de retorno ---
                TextButton(onClick = { navController.navigateUp() }) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}

// --- Validación de formulario ---
private fun validateForm(
    nombre: String,
    email: String,
    telefono: String,
    password: String,
    confirmarPassword: String
): Boolean {
    return nombre.isNotBlank() &&
            email.isNotBlank() &&
            telefono.isNotBlank() &&
            password.isNotBlank() &&
            confirmarPassword.isNotBlank() &&
            password.length >= 4
}
