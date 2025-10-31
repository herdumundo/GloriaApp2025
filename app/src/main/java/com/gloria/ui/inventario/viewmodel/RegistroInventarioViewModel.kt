package com.gloria.ui.inventario.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.domain.usecase.inventario.GetInventariosCardsUseCase
import com.gloria.domain.usecase.auth.GetLoggedUserSyncUseCase
import com.gloria.domain.usecase.sincronizacion.SincronizarInventariosUseCase
import com.gloria.data.entity.LoggedUser
import com.gloria.data.model.InventarioCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la UI para la pantalla de Registro de Inventario
 */
data class RegistroInventarioState(
    val isLoading: Boolean = false,
    val inventarios: List<InventarioCard> = emptyList(),
    val errorMessage: String? = null,
    val usuarioLogueado: String = "",
    val sucursalLogueada: Int = 1,
    val isSincronizando: Boolean = false,
    val syncMessage: String = ""
)

/**
 * ViewModel para la pantalla de Registro de Inventario
 */
@HiltViewModel
class RegistroInventarioViewModel @Inject constructor(
    private val getInventariosCardsUseCase: GetInventariosCardsUseCase,
    private val getLoggedUserSyncUseCase: GetLoggedUserSyncUseCase,
    private val sincronizarInventariosUseCase: SincronizarInventariosUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegistroInventarioState())
    val uiState: StateFlow<RegistroInventarioState> = _uiState.asStateFlow()
    
    init {
        // Cargar inventarios al inicializar
        cargarInventarios()
    }
    
    /**
     * Carga los inventarios para el usuario logueado
     */
    fun cargarInventarios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Usar la sucursal del usuario logueado si está disponible
                val loggedUser = getLoggedUserSyncUseCase()
                val sucursalId = loggedUser?.sucursalId ?: uiState.value.sucursalLogueada
                Log.d("PROCESO_LOGIN", "📍 Consultando inventarios para sucursal: $sucursalId")
                _uiState.value = _uiState.value.copy(sucursalLogueada = sucursalId)
                
                // Obtener inventarios DISTINCT con el query corregido
                getInventariosCardsUseCase(
                    sucursal = sucursalId
                ).collect { inventariosCards ->
                    Log.d("PROCESO_LOGIN", "📋 Inventarios cargados desde BD: ${inventariosCards.size}")
                    Log.d("PROCESO_LOGIN", "🔍 Primer inventario card: ${inventariosCards.firstOrNull()?.winvd_nro_inv}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        inventarios = inventariosCards,
                        errorMessage = null
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar inventarios: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Actualiza la sucursal logueada y recarga los inventarios
     */
    fun actualizarSucursal(sucursal: Int) {
        _uiState.value = _uiState.value.copy(sucursalLogueada = sucursal)
        cargarInventarios()
    }
    
    /**
     * Carga la información del usuario logueado y su sucursal
     */
    fun cargarUsuarioLogueado() {
        viewModelScope.launch {
            try {
                getLoggedUserSyncUseCase()?.let { loggedUser ->
                    _uiState.value = _uiState.value.copy(
                        usuarioLogueado = loggedUser.username,
                        sucursalLogueada = 1 // Por defecto, se puede configurar después
                    )
                    // Recargar inventarios con la nueva sucursal
                    cargarInventarios()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar usuario: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Refresca los inventarios sincronizando desde Oracle
     */
    fun refreshInventarios() {
        viewModelScope.launch {
            Log.d("PROCESO_LOGIN", "=== INICIANDO refreshInventarios ===")
            Log.d("PROCESO_LOGIN", "🔄 Estableciendo isSincronizando = true")
            
            _uiState.value = _uiState.value.copy(
                isSincronizando = true,
                syncMessage = "🔄 Sincronizando inventarios desde Oracle..."
            )
            
            Log.d("PROCESO_LOGIN", "✅ Estado actualizado: isSincronizando = ${_uiState.value.isSincronizando}")
            
            try {
                Log.d("PROCESO_LOGIN", "🔍 Llamando a sincronizarInventariosUseCase...")
                sincronizarInventariosUseCase { message, current, total ->
                    Log.d("PROCESO_LOGIN", "📊 Progreso: $message")
                    _uiState.value = _uiState.value.copy(
                        syncMessage = message
                    )
                }.collect { result ->
                    Log.d("PROCESO_LOGIN", "📋 Resultado recibido: ${result.isSuccess}")
                    if (result.isSuccess) {
                        val totalInventarios = result.getOrNull() ?: 0
                        Log.d("PROCESO_LOGIN", "✅ Sincronización exitosa: $totalInventarios inventarios")
                        _uiState.value = _uiState.value.copy(
                            isSincronizando = false,
                            syncMessage = "✅ Sincronización completada"
                        )
                        // Recargar inventarios después de la sincronización exitosa
                        Log.d("PROCESO_LOGIN", "🔄 Recargando inventarios después de sincronización...")
                        cargarInventarios()
                    } else {
                        val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                        Log.e("PROCESO_LOGIN", "❌ Error en sincronización: $error")
                        _uiState.value = _uiState.value.copy(
                            isSincronizando = false,
                            syncMessage = "❌ Error en sincronización",
                            errorMessage = error
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PROCESO_LOGIN", "💥 Exception en refreshInventarios: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isSincronizando = false,
                    syncMessage = "❌ Error en sincronización",
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
