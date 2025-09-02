package com.gloria.ui.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.repository.SincronizacionCompletaRepository
import com.gloria.data.repository.InventarioSincronizacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SincronizacionState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val syncedItemsCount: Int = 0,
    val lastSyncTimestamp: Long? = null,
    val inventariosCount: Int = 0,
    val isSincronizandoInventarios: Boolean = false,
    val inventariosProgressMessage: String = "",
    val inventariosProgressCurrent: Int = 0,
    val inventariosProgressTotal: Int = 0
)

class SincronizacionViewModel(
    private val sincronizacionRepository: SincronizacionCompletaRepository,
    private val inventarioSincronizacionRepository: InventarioSincronizacionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SincronizacionState())
    val uiState: StateFlow<SincronizacionState> = _uiState.asStateFlow()
    
    init {
        loadSyncInfo()
        loadInventariosInfo()
    }
    
    fun sincronizarDatos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSuccess = false,
                errorMessage = null
            )
            
            try {
                val result = sincronizacionRepository.sincronizarTodasLasTablas()
                
                if (result.isSuccess) {
                    val syncResult = result.getOrNull()
                    val totalCount = (syncResult?.areasCount ?: 0) + 
                                   (syncResult?.departamentosCount ?: 0) + 
                                   (syncResult?.seccionesCount ?: 0) + 
                                   (syncResult?.familiasCount ?: 0) + 
                                   (syncResult?.gruposCount ?: 0) + 
                                   (syncResult?.subgruposCount ?: 0) + 
                                   (syncResult?.sucursalDepartamentosCount ?: 0)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        syncedItemsCount = totalCount,
                        lastSyncTimestamp = syncResult?.timestamp,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    private fun loadSyncInfo() {
        viewModelScope.launch {
            try {
                val estadisticas = sincronizacionRepository.getEstadisticasSincronizacion()
                val totalCount = estadisticas.areasCount + 
                               estadisticas.departamentosCount + 
                               estadisticas.seccionesCount + 
                               estadisticas.familiasCount + 
                               estadisticas.gruposCount + 
                               estadisticas.subgruposCount + 
                               estadisticas.sucursalDepartamentosCount
                
                _uiState.value = _uiState.value.copy(
                    syncedItemsCount = totalCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar información de sincronización"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Sincroniza solo los inventarios desde Oracle
     */
    fun sincronizarInventarios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSincronizandoInventarios = true,
                inventariosProgressMessage = "🔄 Iniciando sincronización de inventarios...",
                inventariosProgressCurrent = 0,
                inventariosProgressTotal = 0
            )
            
            try {
                val result = inventarioSincronizacionRepository.sincronizarInventarios { message, current, total ->
                    _uiState.value = _uiState.value.copy(
                        inventariosProgressMessage = message,
                        inventariosProgressCurrent = current,
                        inventariosProgressTotal = total
                    )
                }.collect { result ->
                    if (result.isSuccess) {
                        val totalInventarios = result.getOrNull() ?: 0
                        _uiState.value = _uiState.value.copy(
                            isSincronizandoInventarios = false,
                            inventariosCount = totalInventarios,
                            inventariosProgressMessage = "✅ Sincronización de inventarios completada",
                            inventariosProgressCurrent = totalInventarios,
                            inventariosProgressTotal = totalInventarios
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isSincronizandoInventarios = false,
                            inventariosProgressMessage = "💥 Error en sincronización de inventarios",
                            errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSincronizandoInventarios = false,
                    inventariosProgressMessage = "💥 Error en sincronización de inventarios",
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    /**
     * Carga información de inventarios sincronizados
     */
    private fun loadInventariosInfo() {
        viewModelScope.launch {
            try {
                val totalInventarios = inventarioSincronizacionRepository.getTotalInventariosLocales()
                _uiState.value = _uiState.value.copy(
                    inventariosCount = totalInventarios
                )
            } catch (e: Exception) {
                // Silenciar error al cargar inventarios
            }
        }
    }
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
