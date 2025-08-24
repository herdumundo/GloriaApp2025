package com.gloria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.entity.LoggedUser
import com.gloria.domain.model.Sucursal
import com.gloria.repository.AuthRepository
import com.gloria.repository.AuthResult
import com.gloria.repository.SucursalRepository
import com.gloria.repository.SucursalResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

class AuthViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    private val authRepository = AuthRepository()
    private val sucursalRepository = SucursalRepository()
    private var loggedUserDao: LoggedUserDao? = null
    
    fun setLoggedUserDao(dao: LoggedUserDao) {
        loggedUserDao = dao
    }
    
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
    
    private fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            if (username.isBlank() || password.isBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Por favor completa todos los campos"
                )
                return@launch
            }
            
            // Usar el repositorio de Oracle para autenticar
            when (val result = authRepository.authenticateUser(username, password)) {
                is AuthResult.Success -> {
                    // Guardar usuario en Room
                    loggedUserDao?.insertLoggedUser(
                        LoggedUser(
                            username = username,
                            password = password
                        )
                    )
                    
                    // Obtener sucursales del usuario
                    getSucursales(username, password)
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
            }
        }
    }
    

    
    private fun getSucursales(username: String, password: String) {
        viewModelScope.launch {
            when (val result = sucursalRepository.getSucursales(username, password)) {
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
            val username = loggedUserDao?.getLoggedUserSync()?.username
            _state.value = _state.value.copy(
                selectedSucursal = sucursal,
                showSucursalDialog = false,
                isLoggedIn = true,
                currentUser = username
            )
        }
    }
    
    private fun logout() {
        viewModelScope.launch {
            // Limpiar usuario de Room
            loggedUserDao?.clearLoggedUsers()
            
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
