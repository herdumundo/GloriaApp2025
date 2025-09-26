package com.gloria.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.entity.LoggedUser
import com.gloria.domain.model.Sucursal
 import com.gloria.domain.usecase.auth.LogoutUseCase
import com.gloria.domain.usecase.GetSucursalesUseCase
import com.gloria.domain.usecase.AuthSessionUseCase
import com.gloria.repository.SincronizacionCompletaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.gloria.domain.usecase.permission.LoginWithPermissionSyncUseCase
import com.gloria.repository.AuthResult
import com.gloria.repository.SucursalResult
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: String? = null,
    val errorMessage: String? = null,
    val showSucursalDialog: Boolean = false,
    val sucursales: List<Sucursal> = emptyList(),
    val selectedSucursal: Sucursal? = null,
    val tempUsername: String? = null,
    val tempPassword: String? = null,
    val modoDark: Boolean = false
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
    private val loginWithPermissionSyncUseCase: LoginWithPermissionSyncUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getSucursalesUseCase: GetSucursalesUseCase,
    private val authSessionUseCase: AuthSessionUseCase,
    private val sincronizacionCompletaRepository: SincronizacionCompletaRepository
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
    
    fun resetAuthState() {
        Log.d("AuthViewModel", "Reiniciando estado de autenticación")
        _state.value = AuthState()
    }
    
    fun restoreSession(loggedUser: com.gloria.data.entity.LoggedUser) {
        Log.d("AuthViewModel", "Restaurando sesión para usuario: ${loggedUser.username}, sucursal: ${loggedUser.sucursalNombre}, modoDark: ${loggedUser.modoDark}")
        viewModelScope.launch {
            // Crear objeto Sucursal si tenemos la información
            val sucursal = if (loggedUser.sucursalNombre != null && loggedUser.sucursalId != null) {
                com.gloria.domain.model.Sucursal(
                    id = loggedUser.sucursalId,
                    descripcion = loggedUser.sucursalNombre,
                    rol = "" // No tenemos información del rol guardada
                )
            } else null
            
            _state.value = _state.value.copy(
                isLoggedIn = true,
                currentUser = loggedUser.username, // Usar el username del usuario, no el nombre de la sucursal
                selectedSucursal = sucursal,
                modoDark = loggedUser.modoDark
            )
        }
    }
    
    fun login(username: String, password: String) {
        // Evitar múltiples llamadas simultáneas
        if (_state.value.isLoading) {
            Log.d("AuthViewModel", "Login ya en progreso, ignorando llamada duplicada")
            return
        }
        
        // Establecer loading inmediatamente, antes del launch
        _state.value = _state.value.copy(
            isLoading = true, 
            errorMessage = null,
            tempUsername = username,
            tempPassword = password
        )
        
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Por favor completa todos los campos"
                )
                return@launch
            }
            
            // Usar el caso de uso para autenticar con sincronización de permisos
            when (val result = loginWithPermissionSyncUseCase(username, password)) {
                is AuthResult.Success -> {
                    Log.d("AuthViewModel", "Login exitoso para: $username, obteniendo sucursales")
                    // NO guardar la sesión aquí - se guardará cuando se seleccione la sucursal
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
            }
        }
    }
    
    private fun sucursalSelected(sucursal: Sucursal) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Sucursal seleccionada: ${sucursal.descripcion}, guardando en sesión")
            
            // Verificar que tenemos los datos temporales necesarios
            val username = _state.value.tempUsername
            val password = _state.value.tempPassword
            
            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                Log.e("AuthViewModel", "Error: No se encontraron credenciales temporales para guardar sesión")
                return@launch
            }
            
            // Guardar la información de la sucursal en la sesión persistente usando los datos temporales
            authSessionUseCase.saveUserSession(
                username = username,
                password = password,
                sucursalId = sucursal.id,
                sucursalNombre = sucursal.descripcion
            )
            
            // Sincronizar datos maestros después de seleccionar sucursal
            Log.d("AuthViewModel", "Sincronizando datos maestros después de seleccionar sucursal...")
            try {
                val syncResult = sincronizacionCompletaRepository.sincronizarTodasLasTablas(
                    onProgress = { message, current, total ->
                        Log.d("AuthViewModel", "Sincronización: $message ($current/$total)")
                    },
                    userdb = username,
                    passdb = password
                )
                
                if (syncResult.isSuccess) {
                    Log.d("AuthViewModel", "✅ Datos maestros sincronizados exitosamente")
                } else {
                    Log.w("AuthViewModel", "⚠️ Error al sincronizar datos maestros: ${syncResult.exceptionOrNull()?.message}")
                    // No fallar el login si hay error en sincronización - el usuario puede usar la app
                }
            } catch (e: Exception) {
                Log.w("AuthViewModel", "⚠️ Error inesperado al sincronizar datos maestros: ${e.message}")
                // No fallar el login si hay error en sincronización - el usuario puede usar la app
            }
            
            _state.value = _state.value.copy(
                selectedSucursal = sucursal,
                showSucursalDialog = false,
                isLoggedIn = true,
                currentUser = username, // Usar el username del usuario, no el nombre de la sucursal
                tempUsername = null, // Limpiar datos temporales
                tempPassword = null
            )
        }
    }
    
      fun updateModoDark(modoDark: Boolean) {
        Log.d("AuthViewModel", "Actualizando modo oscuro: $modoDark")
        _state.value = _state.value.copy(modoDark = modoDark)
        viewModelScope.launch {
            authSessionUseCase.updateModoDark(modoDark)
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Usar el caso de uso para logout
            logoutUseCase()
            
            // Limpiar la sesión guardada
            authSessionUseCase.clearUserSession()
            
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
