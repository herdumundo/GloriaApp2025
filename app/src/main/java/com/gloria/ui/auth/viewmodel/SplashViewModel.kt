package com.gloria.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.domain.usecase.AuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

data class SplashUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean? = null,
    val loggedUser: com.gloria.data.entity.LoggedUser? = null,
    val error: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authSessionUseCase: AuthSessionUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    
    fun checkUserSession() {
        Log.d("SplashViewModel", "Iniciando verificación de sesión")
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val loggedUser = authSessionUseCase.getCurrentUser()
                val isLoggedIn = loggedUser != null
                Log.d("SplashViewModel", "Resultado de verificación: isLoggedIn=$isLoggedIn, user=$loggedUser")
                
                       _uiState.value = _uiState.value.copy(
                           isLoading = false,
                           isLoggedIn = isLoggedIn,
                           loggedUser = loggedUser
                       )
                       
                       // Restaurar el modo oscuro si hay usuario logueado
                       if (loggedUser != null) {
                           com.gloria.ui.theme.ThemeManager.updateTheme(loggedUser.modoDark)
                       }
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Error verificando sesión", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    loggedUser = null,
                    error = e.message
                )
            }
        }
    }
}
