package com.gloria.ui.exportaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.domain.usecase.GetSucursalesUseCase
import com.gloria.domain.usecase.exportacion.ExportarConteosRealizadosUseCase
import com.gloria.domain.usecase.exportacion.ExportarConteosParaVerificacionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportacionesViewModel @Inject constructor(
    private val exportarConteosRealizadosUseCase: ExportarConteosRealizadosUseCase,
    private val exportarConteosParaVerificacionUseCase: ExportarConteosParaVerificacionUseCase,
    private val getSucursalesUseCase: GetSucursalesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExportacionesState())
    val uiState: StateFlow<ExportacionesState> = _uiState.asStateFlow()
    
    fun exportarConteosRealizados() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isExportando = true,
                    mensajeProgreso = "Exportando conteos realizados..."
                )
                
                // Obtener sucursal actual del usuario
                val sucursales = getSucursalesUseCase()
                // TODO: Obtener sucursal y usuario actual del sistema
                val idSucursal = "1" // Hardcoded por ahora
                val userLogin = "admin" // Hardcoded por ahora
                
                val resultado = exportarConteosRealizadosUseCase(
                    idSucursal = idSucursal,
                    userLogin = userLogin
                )
                
                resultado.fold(
                    onSuccess = { mensaje ->
                        _uiState.value = _uiState.value.copy(
                            isExportando = false,
                            mensajeResultado = mensaje,
                            exportacionExitosa = true
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isExportando = false,
                            mensajeResultado = error.message ?: "Error al exportar conteos realizados",
                            exportacionExitosa = false
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExportando = false,
                    mensajeResultado = e.message ?: "Error al exportar conteos realizados",
                    exportacionExitosa = false
                )
            }
        }
    }
    
    fun exportarConteosParaVerificacion() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isExportando = true,
                    mensajeProgreso = "Enviando conteos para verificación..."
                )
                
                // Obtener sucursal actual del usuario
                val sucursales = getSucursalesUseCase()
                // TODO: Obtener sucursal y usuario actual del sistema
                val idSucursal = "1" // Hardcoded por ahora
                val userLogin = "admin" // Hardcoded por ahora
                
                val resultado = exportarConteosParaVerificacionUseCase(
                    idSucursal = idSucursal,
                    userLogin = userLogin
                )
                
                resultado.fold(
                    onSuccess = { mensaje ->
                        _uiState.value = _uiState.value.copy(
                            isExportando = false,
                            mensajeResultado = mensaje,
                            exportacionExitosa = true
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isExportando = false,
                            mensajeResultado = error.message ?: "Error al enviar conteos para verificación",
                            exportacionExitosa = false
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExportando = false,
                    mensajeResultado = e.message ?: "Error al enviar conteos para verificación",
                    exportacionExitosa = false
                )
            }
        }
    }
    
    fun limpiarMensajeResultado() {
        _uiState.value = _uiState.value.copy(
            mensajeResultado = null,
            exportacionExitosa = false
        )
    }
}

data class ExportacionesState(
    val isExportando: Boolean = false,
    val mensajeProgreso: String = "",
    val mensajeResultado: String? = null,
    val exportacionExitosa: Boolean = false
)

