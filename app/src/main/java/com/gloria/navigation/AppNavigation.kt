package com.gloria.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gloria.ui.exportaciones.screen.ExportacionesScreen
import com.gloria.ui.auth.viewmodel.AuthViewModel
import com.gloria.ui.inventario.viewmodel.*
import com.gloria.ui.exportaciones.viewmodel.ExportacionesViewModel

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
            val authState by authViewModel.state.collectAsState()
            LoginScreen(
                onLoginClick = { username, password ->
                    authViewModel.login(username, password)
                },
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage
            )
        }
        
        // Pantalla Principal (Home)
        composable("home") {
            val authState by authViewModel.state.collectAsState()
            HomeScreen(
                username = authState.currentUser ?: "",
                sucursal = authState.selectedSucursal?.descripcion,
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
            val authState by authViewModel.state.collectAsState()
            MainMenuScreen(
                navController = navController,
                username = authState.currentUser ?: "",
                sucursal = authState.selectedSucursal?.descripcion ?: "",
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
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
                viewModel = conteoViewModel,
                navController
            )
        }
        
        // Artículos Toma
        composable("articulos_toma/{nroToma}") { backStackEntry ->
            val nroToma = backStackEntry.arguments?.getString("nroToma")?.toIntOrNull() ?: 0
            val articulosTomaViewModel: ArticulosTomaViewModel = hiltViewModel()
            ArticulosTomaScreen(
                nroToma = nroToma,
                articulosTomaViewModel,
                navController
            )
        }
        
        // Exportaciones
        composable("exportar_inventario") {
            val exportacionesViewModel: ExportacionesViewModel = hiltViewModel()
            ExportacionesScreen(
                navController = navController,
                viewModel = exportacionesViewModel
            )
        }

    }
}
