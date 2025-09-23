package com.gloria.ui

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.util.Log
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
    Log.d("MainScreen", "MainScreen iniciado")
    val authState by authViewModel.state.collectAsState()
    
    // Reiniciar el estado de autenticación cuando se abre la app
    LaunchedEffect(Unit) {
        Log.d("MainScreen", "Reiniciando estado de autenticación")
        authViewModel.resetAuthState()
    }
    
    LaunchedEffect(authState.errorMessage) {
        if (authState.errorMessage != null) {
            // Limpiar error después de 3 segundos
            kotlinx.coroutines.delay(3000)
            authViewModel.handleEvent(AuthEvent.ClearError)
        }
    }
    
    // NO navegar automáticamente - dejar que SplashScreen controle la navegación inicial
    // Solo navegar después de que el usuario haya hecho login exitoso
    LaunchedEffect(authState.isLoggedIn) {
        // Solo navegar si ya estamos en login y el usuario se logueó exitosamente
        val currentRoute = navController.currentDestination?.route
        if (currentRoute == "login" && authState.isLoggedIn) {
            navController.navigate("menu_principal") {
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