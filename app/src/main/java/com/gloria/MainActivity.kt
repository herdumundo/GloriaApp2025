package com.gloria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gloria.ui.MainScreen
import com.gloria.ui.theme.AppinventarioTheme
import com.gloria.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppinventarioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val application = application as InventarioApplication
                    val authViewModel: AuthViewModel = viewModel()
                    
                    // Configurar el DAO en el ViewModel
                    authViewModel.setLoggedUserDao(application.database.loggedUserDao())
                    
                    MainScreen(authViewModel = authViewModel)
                }
            }
        }
    }
}