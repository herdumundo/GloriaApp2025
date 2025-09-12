package com.gloria.ui

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gloria.navigation.AppNavigation
import com.gloria.ui.auth.viewmodel.AuthEvent
import com.gloria.ui.auth.viewmodel.AuthState
import com.gloria.ui.auth.viewmodel.AuthViewModel
import com.gloria.ui.components.SucursalSelectionDialog

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.state.collectAsState()
    
    LaunchedEffect(authState.errorMessage) {
        if (authState.errorMessage != null) {
            // Limpiar error después de 3 segundos
            kotlinx.coroutines.delay(3000)
            authViewModel.handleEvent(AuthEvent.ClearError)
        }
    }
    
    // Navegar automáticamente según el estado de autenticación
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            navController.navigate("menu_principal") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    
    // Usar la nueva navegación
    AppNavigation(
        navController = navController,
        authViewModel = authViewModel
    )
    
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