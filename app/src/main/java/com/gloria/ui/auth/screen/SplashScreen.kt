package com.gloria.ui.auth.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.util.Log
import com.gloria.R
import com.gloria.ui.auth.viewmodel.SplashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: (com.gloria.data.entity.LoggedUser?) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    Log.d("SplashScreen", "SplashScreen iniciado")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        Log.d("SplashScreen", "Llamando a checkUserSession")
        viewModel.checkUserSession()
    }
    
    LaunchedEffect(uiState.isLoggedIn) {
        Log.d("SplashScreen", "Estado de sesión cambiado: isLoggedIn=${uiState.isLoggedIn}")
        when {
            uiState.isLoggedIn == true -> {
                Log.d("SplashScreen", "Usuario logueado, navegando al menú principal con usuario: ${uiState.loggedUser}")
                onNavigateToMain(uiState.loggedUser)
            }
            uiState.isLoggedIn == false -> {
                Log.d("SplashScreen", "Usuario no logueado, navegando al login")
                onNavigateToLogin()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo o imagen de la aplicación
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Gloria Inventario",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Verificando sesión...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
