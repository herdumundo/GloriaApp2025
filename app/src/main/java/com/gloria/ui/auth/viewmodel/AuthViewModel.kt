package com.gloria.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.entity.LoggedUser
import com.gloria.domain.model.Sucursal
import com.gloria.domain.usecase.auth.LoginUseCase
import com.gloria.domain.usecase.auth.LogoutUseCase
import com.gloria.domain.usecase.GetSucursalesUseCase
import com.gloria.repository.AuthResult
import com.gloria.repository.SucursalResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: String? = null,
    val errorMessage: String? = null,
    val showSucursalDialog: Boolean = false,
    val sucursales: List<Sucursal> = emptyList(),
    val selectedSucursal: Sucursal? = null
)

sealed class AuthEvent {
    data class Login(val username: String, val password: String) : AuthEvent()
    data class SucursalSelected(val sucursal: Sucursal) : AuthEvent()
    object Logout : AuthEvent()
    object HideSucursalDialog : AuthEvent()
    object ClearError : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getSucursalesUseCase: GetSucursalesUseCase
) : ViewModel() {
    
      val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> {
                login(event.username, event.password)
            }
            is AuthEvent.SucursalSelected -> {
                sucursalSelected(event.sucursal)
            }
            is AuthEvent.Logout -> {
                logout()
            }
            is AuthEvent.HideSucursalDialog -> {
                _state.value = _state.value.copy(showSucursalDialog = false)
            }
            is AuthEvent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }
    
    fun login(username: String, password: String) {
        // Establecer loading inmediatamente, antes del launch
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Por favor completa todos los campos"
                )
                return@launch
            }
            
            // Usar el caso de uso para autenticar (es suspend)
            when (val result = loginUseCase(username, password)) {
                is AuthResult.Success -> {
                    // Obtener sucursales del usuario
                    getSucursales()
                }
                is AuthResult.InvalidCredentials -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.NetworkError -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

              /*  is com.gloria.domain.model.AuthResult.Error -> TODO()
                com.gloria.domain.model.AuthResult.Success -> TODO()*/
            }
        }
    }
    

    
    private fun getSucursales() {
        viewModelScope.launch {
            when (val result = getSucursalesUseCase()) {
                is SucursalResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        sucursales = result.sucursales,
                        showSucursalDialog = true,
                        errorMessage = null
                    )
                }
                is SucursalResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is SucursalResult.NetworkError -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is SucursalResult.InvalidCredentials -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    private fun sucursalSelected(sucursal: Sucursal) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                selectedSucursal = sucursal,
                showSucursalDialog = false,
                isLoggedIn = true,
                currentUser = sucursal.descripcion
            )
        }
    }
    
      fun logout() {
        viewModelScope.launch {
            // Usar el caso de uso para logout
            logoutUseCase()
            
            _state.value = _state.value.copy(
                isLoading = false,
                isLoggedIn = false,
                currentUser = null,
                selectedSucursal = null,
                sucursales = emptyList(),
                showSucursalDialog = false,
                errorMessage = null
            )
        }
    }
}
