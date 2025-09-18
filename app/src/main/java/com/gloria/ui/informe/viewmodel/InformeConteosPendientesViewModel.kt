package com.gloria.ui.informe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.model.ConteoPendienteResponse
import com.gloria.data.model.InventarioConteo
import com.gloria.domain.usecase.conteopendiente.GetConteosPendientesByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Estado del ViewModel para el informe de conteos pendientes
 */
data class InformeConteosPendientesState(
    val isLoading: Boolean = false,
    val fechaSeleccionada: String = "",
    val conteosPendientes: ConteoPendienteResponse? = null,
    val errorMessage: String? = null,
    val mostrarDetalle: Boolean = false,
    val detalleSeleccionado: InventarioConteo? = null
)

/**
 * ViewModel para manejar el informe de conteos pendientes
 */
@HiltViewModel
class InformeConteosPendientesViewModel @Inject constructor(
    private val getConteosPendientesByDateUseCase: GetConteosPendientesByDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InformeConteosPendientesState())
    val uiState: StateFlow<InformeConteosPendientesState> = _uiState.asStateFlow()

    /**
     * Busca conteos pendientes para la fecha seleccionada
     */
    fun buscarConteosPendientes(fecha: Date) {
        val fechaFormateada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha)
        buscarConteosPendientes(fechaFormateada)
    }

    /**
     * Busca conteos pendientes para una fecha en formato string
     */
    fun buscarConteosPendientes(fechaString: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                fechaSeleccionada = fechaString,
                errorMessage = null,
                conteosPendientes = null
            )

            getConteosPendientesByDateUseCase(fechaString)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conteosPendientes = response,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conteosPendientes = null,
                        errorMessage = exception.message ?: "Error al consultar conteos pendientes"
                    )
                }
        }
    }

    /**
     * Muestra el detalle de un inventario específico
     */
    fun mostrarDetalleInventario(inventario: InventarioConteo) {
        _uiState.value = _uiState.value.copy(
            mostrarDetalle = true,
            detalleSeleccionado = inventario
        )
    }

    /**
     * Oculta el detalle del inventario
     */
    fun ocultarDetalleInventario() {
        _uiState.value = _uiState.value.copy(
            mostrarDetalle = false,
            detalleSeleccionado = null
        )
    }

    /**
     * Limpia el mensaje de error
     */
    fun limpiarError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Resetea el estado completo
     */
    fun resetearEstado() {
        _uiState.value = InformeConteosPendientesState()
    }
}
