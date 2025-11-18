package com.gloria.ui.exportaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.domain.usecase.GetSucursalesUseCase
import com.gloria.domain.usecase.exportacion.ExportarConteosRealizadosUseCase
import com.gloria.domain.usecase.exportacion.ExportarConteosParaVerificacionUseCase
import com.gloria.domain.usecase.inventario.EnviarConteosLogsUseCase
import com.gloria.domain.usecase.AuthSessionUseCase
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
    private val enviarConteosLogsUseCase: EnviarConteosLogsUseCase,
    private val getSucursalesUseCase: GetSucursalesUseCase,
    private val authSessionUseCase: AuthSessionUseCase
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
                
                // Obtener credenciales del usuario logueado
                val loggedUser = authSessionUseCase.getCurrentUser()
                if (loggedUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isExportando = false,
                        mensajeResultado = "No hay usuario logueado",
                        exportacionExitosa = false
                    )
                    return@launch
                }
                
                val idSucursal = loggedUser.sucursalId?.toString() ?: "1"
                val userLogin = loggedUser.username
                
                val resultado = exportarConteosRealizadosUseCase(
                    idSucursal = idSucursal,
                    userLogin = userLogin
                )
                
                resultado.fold(
                    onSuccess = { mensaje ->
                        val logsResult = enviarConteosLogsUseCase()
                        val mensajeLogs = logsResult.fold(
                            onSuccess = { enviados ->
                                if (enviados > 0) "$enviados conteos locales sincronizados." else "No había conteos locales pendientes."
                            },
                            onFailure = { error -> "Error al sincronizar conteos locales: ${error.message ?: "desconocido"}" }
                        )
                        _uiState.value = _uiState.value.copy(
                            isExportando = false,
                            mensajeResultado = "$mensaje\n$mensajeLogs",
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
                
                // Obtener credenciales del usuario logueado
                val loggedUser = authSessionUseCase.getCurrentUser()
                if (loggedUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isExportando = false,
                        mensajeResultado = "No hay usuario logueado",
                        exportacionExitosa = false
                    )
                    return@launch
                }
                
                val idSucursal = loggedUser.sucursalId?.toString() ?: "1"
                val userLogin = loggedUser.username
                
                val resultado = exportarConteosParaVerificacionUseCase(
                    idSucursal = idSucursal,
                    userLogin = userLogin
                )
                
                resultado.fold(
                    onSuccess = { mensaje ->
                        val logsResult = enviarConteosLogsUseCase()
                        val mensajeLogs = logsResult.fold(
                            onSuccess = { enviados ->
                                if (enviados > 0) "$enviados conteos locales sincronizados." else "No había conteos locales pendientes."
                            },
                            onFailure = { error -> "Error al sincronizar conteos locales: ${error.message ?: "desconocido"}" }
                        )
                        _uiState.value = _uiState.value.copy(
                            isExportando = false,
                            mensajeResultado = "$mensaje\n$mensajeLogs",
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

