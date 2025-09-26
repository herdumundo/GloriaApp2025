package com.gloria.ui.inventario.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gloria.ui.components.CancelacionTomaCard
import com.gloria.ui.inventario.viewmodel.CancelacionInventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelacionInventarioScreen(
    onNavigateBack: () -> Unit,
    onNavigateToArticulos: (Int) -> Unit,
    viewModel: CancelacionInventarioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarCancelaciones()
    }

    Scaffold(
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.limpiarError()
                            viewModel.cargarCancelaciones()
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            } else if (state.cancelaciones.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay tomas para cancelar",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.cargarCancelaciones() }
                    ) {
                        Text("Actualizar")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(state.cancelaciones) { index, cancelacion ->
                        CancelacionTomaCard(
                            cancelacion = cancelacion,
                            onClick = { onNavigateToArticulos(cancelacion.winveNumero) }
                        )
                    }
                }
            }
        }
    }
}