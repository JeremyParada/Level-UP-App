package com.levelup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.levelup.data.model.Address
import com.levelup.viewmodel.AddressUiState
import com.levelup.viewmodel.AddressViewModel

@Composable
fun AddressScreen(viewModel: AddressViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Direcciones") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is AddressUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is AddressUiState.Success -> {
                    val addresses = (uiState as AddressUiState.Success).addresses
                    LazyColumn {
                        items(addresses) { address ->
                            AddressCard(address)
                        }
                    }
                }
                is AddressUiState.Error -> {
                    Text(
                        text = (uiState as AddressUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun AddressCard(address: Address) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${address.street} ${address.number}, ${address.city}")
            Text(text = "${address.region}, ${address.postalCode}")
            if (address.isPrimary) {
                Text(text = "Principal", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}