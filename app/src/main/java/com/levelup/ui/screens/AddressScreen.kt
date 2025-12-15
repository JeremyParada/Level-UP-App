package com.levelup.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.levelup.data.model.Direccion
import com.levelup.ui.auth.AuthViewModel
import com.levelup.ui.theme.LevelUpPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val context = LocalContext.current

    AddressContent(
        currentUser = currentUser,
        addresses = addresses,
        onBackClick = { navController.popBackStack() },
        onAddAddress = { address ->
            viewModel.addAddress(address)
            Toast.makeText(context, "Dirección agregada", Toast.LENGTH_SHORT).show()
        },
        onUpdateAddress = { address ->
            viewModel.updateAddress(address)
            Toast.makeText(context, "Dirección actualizada", Toast.LENGTH_SHORT).show()
        },
        onDeleteAddress = { addressId ->
            viewModel.deleteAddress(addressId)
            Toast.makeText(context, "Dirección eliminada", Toast.LENGTH_SHORT).show()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressContent(
    currentUser: com.levelup.data.model.User?,
    addresses: List<Direccion>,
    onBackClick: () -> Unit,
    onAddAddress: (Direccion) -> Unit,
    onUpdateAddress: (Direccion) -> Unit,
    onDeleteAddress: (Long) -> Unit
) {
    // State for Dialog
    var showAddressDialog by remember { mutableStateOf(false) }
    var addressToEdit by remember { mutableStateOf<Direccion?>(null) } // null = new, not null = edit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Direcciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LevelUpPrimary, titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addressToEdit = null
                    showAddressDialog = true
                },
                containerColor = LevelUpPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Dirección")
            }
        }
    ) { paddingValues ->
        if (currentUser == null) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
             }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Name Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Usuario: ",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = currentUser.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Correo: ",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = currentUser.email,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Direcciones Guardadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (addresses.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No tienes direcciones guardadas.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(addresses) { address ->
                            AddressManagementItem(
                                address = address,
                                onEdit = {
                                    addressToEdit = address
                                    showAddressDialog = true
                                },
                                onDelete = {
                                    onDeleteAddress(address.id!!)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddressDialog) {
        AddressDialog(
            address = addressToEdit,
            onDismiss = { showAddressDialog = false },
            onSave = { newAddress ->
                if (addressToEdit == null) {
                    onAddAddress(newAddress)
                } else {
                    onUpdateAddress(newAddress)
                }
                showAddressDialog = false
            }
        )
    }
}

@Composable
fun AddressManagementItem(address: Direccion, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${address.calle} ${address.numero}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${address.comuna}, ${address.ciudad}", style = MaterialTheme.typography.bodyMedium)
            Text(address.region, style = MaterialTheme.typography.bodySmall)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }
                TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDialog(
    address: Direccion?,
    onDismiss: () -> Unit,
    onSave: (Direccion) -> Unit
) {
    var calle by remember { mutableStateOf(address?.calle ?: "") }
    var numero by remember { mutableStateOf(address?.numero ?: "") }
    var comuna by remember { mutableStateOf(address?.comuna ?: "") }
    var ciudad by remember { mutableStateOf(address?.ciudad ?: "") }
    var region by remember { mutableStateOf(address?.region ?: "") }
    var codigoPostal by remember { mutableStateOf(address?.codigoPostal ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (address == null) "Nueva Dirección" else "Editar Dirección") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = calle, onValueChange = { calle = it }, label = { Text("Calle") }, singleLine = true)
                OutlinedTextField(value = numero, onValueChange = { numero = it }, label = { Text("Número") }, singleLine = true)
                OutlinedTextField(value = comuna, onValueChange = { comuna = it }, label = { Text("Comuna") }, singleLine = true)
                OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") }, singleLine = true)
                OutlinedTextField(value = region, onValueChange = { region = it }, label = { Text("Región") }, singleLine = true)
                OutlinedTextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Código Postal") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                val newAddress = Direccion(
                    id = address?.id,
                    calle = calle,
                    numero = numero,
                    comuna = comuna,
                    ciudad = ciudad,
                    region = region,
                    codigoPostal = codigoPostal,
                    idUsuario = address?.idUsuario
                )
                onSave(newAddress)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}