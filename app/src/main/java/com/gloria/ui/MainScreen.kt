package com.gloria.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gloria.ui.auth.screen.LoginScreen
import com.gloria.ui.menu.MenuPrincipalScreen
import com.gloria.ui.main.screen.MainMenuScreen
import com.gloria.ui.inventario.screen.TomaManualScreen
import com.gloria.ui.components.SucursalSelectionDialog
import com.gloria.ui.auth.viewmodel.AuthEvent
import com.gloria.ui.auth.viewmodel.AuthState
import com.gloria.ui.auth.viewmodel.AuthViewModel

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.state.collectAsState()
    var currentScreen by remember { mutableStateOf("menu_principal") }
    
    LaunchedEffect(authState.errorMessage) {
        if (authState.errorMessage != null) {
            // Limpiar error después de 3 segundos
            kotlinx.coroutines.delay(3000)
            authViewModel.handleEvent(AuthEvent.ClearError)
        }
    }
    
    when {
        authState.isLoggedIn -> {
            // Usuario autenticado - mostrar navegación
            authState.currentUser?.let { username ->
                authState.selectedSucursal?.let { sucursal ->
                    when (currentScreen) {
                        "menu_principal" -> {
                            MenuPrincipalScreen(
                                navController = navController,
                                onNavigateToTomaManual = {
                                    // Ya no es necesario, se maneja internamente
                                },
                                onNavigateToRegistroEscaneados = {
                                    // Por ahora no implementado
                                },
                                onNavigateToCapturaManual = {
                                    // Por ahora no implementado
                                },
                                onNavigateToValidacionCodigos = {
                                    // Por ahora no implementado
                                },
                                username = username,
                                sucursal = sucursal.descripcion,
                                onLogoutClick = {
                                    authViewModel.handleEvent(AuthEvent.Logout)
                                }
                            )
                        }
                        "menu_completo" -> {
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
