package com.gloria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.repository.SincronizacionCompletaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SincronizacionState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val syncedItemsCount: Int = 0,
    val lastSyncTimestamp: Long? = null
)

class SincronizacionViewModel(
    private val sincronizacionRepository: SincronizacionCompletaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SincronizacionState())
    val uiState: StateFlow<SincronizacionState> = _uiState.asStateFlow()
    
    init {
        loadSyncInfo()
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
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
