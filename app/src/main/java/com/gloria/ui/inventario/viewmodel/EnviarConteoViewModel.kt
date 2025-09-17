package com.gloria.ui.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.model.ConteoRequest
import com.gloria.data.model.ConteoRequestResponse
import com.gloria.domain.usecase.enviarconteo.EnviarConteoVerificacionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado del ViewModel para el envío de conteos
 */
data class EnviarConteoState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val response: ConteoRequestResponse? = null,
    val registrosInsertados: Int = 0,
    val tiempoProcesamiento: Int = 0
)

/**
 * ViewModel para manejar el envío de conteos de verificación
 * Ejemplo de cómo usar el EnviarConteoVerificacionUseCase
 */
@HiltViewModel
class EnviarConteoViewModel @Inject constructor(
    private val enviarConteoVerificacionUseCase: EnviarConteoVerificacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EnviarConteoState())
    val state: StateFlow<EnviarConteoState> = _state

    /**
     * Envía una lista de conteos de verificación
     * @param conteos Lista de conteos a enviar
     */
    fun enviarConteos(conteos: List<ConteoRequest>) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            enviarConteoVerificacionUseCase(conteos)
                .onSuccess { response ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            response = response,
                            registrosInsertados = response.registrosInsertados ?: 0,
                            tiempoProcesamiento = response.tiempoMs ?: 0
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = exception.message ?: "Error desconocido"
                        )
                    }
                }
        }
    }

    /**
     * Limpia el estado de error
     */
    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    /**
     * Resetea el estado completo
     */
    fun resetState() {
        _state.update { EnviarConteoState() }
    }
}
