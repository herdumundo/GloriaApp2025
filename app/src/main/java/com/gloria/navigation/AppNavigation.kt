package com.gloria.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.gloria.ui.auth.screen.LoginScreen
import com.gloria.ui.main.screen.HomeScreen
import com.gloria.ui.main.screen.MainMenuScreen
import com.gloria.ui.inventario.screen.*
import com.gloria.ui.auth.viewmodel.AuthViewModel
import com.gloria.ui.inventario.viewmodel.*

/**
 * Configuración principal de navegación de la aplicación
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Pantalla de Login
        composable("login") {
            LoginScreen(
                onLoginClick = { username, password ->
                    authViewModel.login(username, password)
                },
                isLoading = authViewModel.state.value.isLoading,
                errorMessage = authViewModel.state.value.errorMessage
            )
        }
        
        // Pantalla Principal (Home)
        composable("home") {
            HomeScreen(
                username = authViewModel.state.value.currentUser ?: "",
                sucursal = authViewModel.state.value.selectedSucursal?.descripcion,
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        // Menú Principal
        composable("menu_principal") {
            MainMenuScreen(
                navController = navController,
                username = authViewModel.state.value.currentUser ?: "",
                sucursal = authViewModel.state.value.selectedSucursal?.descripcion ?: "",
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        // Registro de Toma
        composable("registro_toma") {
            val registroTomaViewModel: TomaManualViewModel = hiltViewModel()
            TomaManualScreen(
                viewModel = registroTomaViewModel,
                navController = navController
            )
        }
        
        // Registro de Inventario
        composable("registro_inventario") {
            val registroViewModel: RegistroInventarioViewModel = hiltViewModel()
            RegistroInventarioScreen(
                viewModel = registroViewModel,
                onNavigateToConteo = { nroInventario ->
                    navController.navigate("conteo_inventario/$nroInventario")
                },
                navController = navController
            )
        }
        
        // Conteo de Inventario
        composable(
            "conteo_inventario/{nroInventario}",
            arguments = listOf(
                navArgument("nroInventario") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val nroInventario = backStackEntry.arguments?.getInt("nroInventario") ?: 0
            val conteoViewModel: ConteoInventarioViewModel = hiltViewModel()
            ConteoInventarioScreen(
                nroInventario = nroInventario,
                onBackPressed = {
                    navController.popBackStack()
                },
                onNavigateToMainMenu = {
                    navController.navigate("menu_principal") {
                        popUpTo("menu_principal") { inclusive = true }
                    }
                },
                viewModel = conteoViewModel
            )
        }
        
        // Artículos Toma
        composable("articulos_toma/{nroToma}") { backStackEntry ->
            val nroToma = backStackEntry.arguments?.getString("nroToma")?.toIntOrNull() ?: 0
            val articulosTomaViewModel: ArticulosTomaViewModel = hiltViewModel()
            ArticulosTomaScreen(
                nroToma = nroToma,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Cancelación de Inventario
        composable("cancelacion_inventario") {
            // TODO: Implementar pantalla de cancelación
            CancelacionInventarioScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToArticulos = { nroToma ->
                    navController.navigate("articulos_toma/$nroToma")
                }
            )
        }
        
        // Exportar Inventario
        composable("exportar_inventario") {
            // TODO: Implementar pantalla de exportación
            ExportarInventarioScreen()
        }
        
        // Exportar Inventario Parcial
        composable("exportar_parcial") {
            // TODO: Implementar pantalla de exportación parcial
            ExportarParcialScreen()
        }
        
        // Sincronizar Datos
        composable("sincronizar_datos") {
            val sincronizacionViewModel: SincronizacionViewModel = hiltViewModel()
            SincronizarDatosScreen(
                sincronizacionViewModel = sincronizacionViewModel
            )
        }
    }
}
