package com.gloria.ui.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.entity.api.InventarioPendienteSimultaneo
import com.gloria.data.model.ConteosLogPayload
import com.gloria.domain.usecase.inventario.GetInventariosPendientesSimultaneosUseCase
import com.gloria.domain.usecase.inventario.ConfirmarConteoSimultaneoUseCase
import com.gloria.domain.usecase.inventario.GetConteosLogsRemotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de procesar conteos pendientes
 */
@HiltViewModel
class ProcesarConteosPendientesViewModel @Inject constructor(
    private val getInventariosPendientesSimultaneosUseCase: GetInventariosPendientesSimultaneosUseCase,
    private val confirmarConteoSimultaneoUseCase: ConfirmarConteoSimultaneoUseCase,
    private val getConteosLogsRemotosUseCase: GetConteosLogsRemotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProcesarConteosPendientesUiState())
    val uiState: StateFlow<ProcesarConteosPendientesUiState> = _uiState.asStateFlow()

    /**
     * Carga los inventarios pendientes simultáneos
     */
    fun loadInventariosPendientes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = getInventariosPendientesSimultaneosUseCase()
                
                result.fold(
                    onSuccess = { inventarios ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            inventariosPendientes = inventarios,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error desconocido"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar inventarios pendientes"
                )
            }
        }
    }

    /**
     * Selecciona un inventario para mostrar sus detalles
     */
    fun selectInventario(inventario: InventarioPendienteSimultaneo) {
        _uiState.value = _uiState.value.copy(
            selectedInventario = inventario,
            showDetailDialog = true,
            logsRemotos = emptyMap()
        )
        consultarLogsRemotos(inventario.cabecera.winvdNroInv)
    }

    /**
     * Consulta los logs remotos para un inventario
     */
    fun consultarLogsRemotos(winvdNroInv: Int) {
        viewModelScope.launch {
            getConteosLogsRemotosUseCase(winvdNroInv)
                .onSuccess { logs ->
                    val agrupados = logs.groupBy { "${it.winvdSecu}_${it.winvdArt}" }
                    _uiState.value = _uiState.value.copy(logsRemotos = agrupados)
                }
                .onFailure { error ->
                    android.util.Log.e(
                        "ProcesarConteosLogs",
                        "Error consultando logs del inventario $winvdNroInv",
                        error
                    )
                    _uiState.value = _uiState.value.copy(logsRemotos = emptyMap())
                }
        }
    }

    /**
     * Cierra el diálogo de detalles
     */
    fun closeDetailDialog() {
        _uiState.value = _uiState.value.copy(
            showDetailDialog = false,
            selectedInventario = null,
            logsRemotos = emptyMap()
        )
    }

    /**
     * Limpia el error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Muestra el diálogo de confirmación para confirmar un conteo
     */
    fun confirmarConteo(nroInventario: Int) {
        _uiState.value = _uiState.value.copy(
            showConfirmDialog = true,
            inventarioToConfirm = nroInventario
        )
    }

    /**
     * Procesa la confirmación del conteo
     */
    fun procesarConfirmacion() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConfirming = true)
            
            try {
                val nroInventario = _uiState.value.inventarioToConfirm
                if (nroInventario != null) {
                    android.util.Log.d("CONFIRMAR_CONTEO", "Confirmando inventario #$nroInventario")
                    
                    val result = confirmarConteoSimultaneoUseCase(nroInventario)
                    
                    result.fold(
                        onSuccess = { response ->
                            android.util.Log.d("CONFIRMAR_CONTEO", "Confirmación exitosa: ${response.message}")
                            android.util.Log.d("CONFIRMAR_CONTEO", "Artículos procesados: ${response.articulosProcesados}")
                            android.util.Log.d("CONFIRMAR_CONTEO", "Usuarios involucrados: ${response.usuariosInvolucrados}")
                            
                            _uiState.value = _uiState.value.copy(
                                isConfirming = false,
                                confirmSuccess = response.message,
                                showConfirmDialog = false,
                                inventarioToConfirm = null
                            )
                            
                            // Recargar la lista después de confirmar
                            loadInventariosPendientes()
                        },
                        onFailure = { exception ->
                            android.util.Log.e("CONFIRMAR_CONTEO", "Error al confirmar: ${exception.message}")
                            _uiState.value = _uiState.value.copy(
                                isConfirming = false,
                                error = exception.message ?: "Error al confirmar el conteo"
                            )
                        }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isConfirming = false,
                        error = "No se especificó el inventario a confirmar"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("CONFIRMAR_CONTEO", "Excepción al confirmar: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isConfirming = false,
                    error = e.message ?: "Error al confirmar el conteo"
                )
            }
        }
    }

    /**
     * Cierra el diálogo de confirmación
     */
    fun closeConfirmDialog() {
        _uiState.value = _uiState.value.copy(
            showConfirmDialog = false,
            inventarioToConfirm = null,
            confirmSuccess = null
        )
    }
}

/**
 * Estado de la UI para la pantalla de procesar conteos pendientes
 */
data class ProcesarConteosPendientesUiState(
    val isLoading: Boolean = false,
    val inventariosPendientes: List<InventarioPendienteSimultaneo> = emptyList(),
    val selectedInventario: InventarioPendienteSimultaneo? = null,
    val showDetailDialog: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val inventarioToConfirm: Int? = null,
    val isConfirming: Boolean = false,
    val confirmSuccess: String? = null,
    val error: String? = null,
    val logsRemotos: Map<String, List<ConteosLogPayload>> = emptyMap()
)
