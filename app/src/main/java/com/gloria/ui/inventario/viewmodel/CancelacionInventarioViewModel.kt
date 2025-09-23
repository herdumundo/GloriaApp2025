package com.gloria.ui.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.model.CancelacionToma
import com.gloria.domain.usecase.cancelacion.GetCancelacionesTomaUseCase
import com.gloria.domain.usecase.AuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CancelacionInventarioState(
    val cancelaciones: List<CancelacionToma> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CancelacionInventarioViewModel @Inject constructor(
    private val getCancelacionesTomaUseCase: GetCancelacionesTomaUseCase,
    private val authSessionUseCase: AuthSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CancelacionInventarioState())
    val state: StateFlow<CancelacionInventarioState> = _state

    fun cargarCancelaciones() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Obtener el usuario logueado desde la sesión
                val currentUser = authSessionUseCase.getCurrentUser()
                if (currentUser?.username.isNullOrBlank()) {
                    _state.update { it.copy(
                        isLoading = false,
                        error = "No hay usuario logueado. Por favor, inicie sesión nuevamente."
                    )}
                    return@launch
                }
                
                val cancelaciones = getCancelacionesTomaUseCase(currentUser.username)
                _state.update { it.copy(
                    cancelaciones = cancelaciones,
                    isLoading = false
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Error al cargar las cancelaciones: ${e.message}"
                )}
            }
        }
    }

    fun limpiarError() {
        _state.update { it.copy(error = null) }
    }
}

