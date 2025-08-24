package com.gloria.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gloria.ui.screens.LoginScreen
import com.gloria.ui.screens.MainMenuScreen
import com.gloria.ui.components.SucursalSelectionDialog
import com.gloria.viewmodel.AuthEvent
import com.gloria.viewmodel.AuthState
import com.gloria.viewmodel.AuthViewModel

@Composable
fun MainScreen(
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.state.collectAsState()
    
    LaunchedEffect(authState.errorMessage) {
        if (authState.errorMessage != null) {
            // Limpiar error después de 3 segundos
            kotlinx.coroutines.delay(3000)
            authViewModel.handleEvent(AuthEvent.ClearError)
        }
    }
    
    when {
        authState.isLoggedIn -> {
            // Usuario autenticado - mostrar menú principal
            authState.currentUser?.let { username ->
                authState.selectedSucursal?.let { sucursal ->
                    MainMenuScreen(
                        username = username,
                        sucursal = sucursal.descripcion,
                        onLogoutClick = {
                            authViewModel.handleEvent(AuthEvent.Logout)
                        }
                    )
                }
            }
        }
        else -> {
            // Mostrar pantalla de login
            LoginScreen(
                onLoginClick = { username, password ->
                    authViewModel.handleEvent(AuthEvent.Login(username, password))
                },
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage
            )
        }
    }
    
    // Mostrar diálogo de selección de sucursal si es necesario
    if (authState.showSucursalDialog && authState.sucursales.isNotEmpty()) {
        SucursalSelectionDialog(
            sucursales = authState.sucursales,
            username = authState.currentUser ?: "",
            onSucursalSelected = { sucursal ->
                authViewModel.handleEvent(AuthEvent.SucursalSelected(sucursal))
            },
            onDismiss = {
                authViewModel.handleEvent(AuthEvent.HideSucursalDialog)
            }
        )
    }
}
