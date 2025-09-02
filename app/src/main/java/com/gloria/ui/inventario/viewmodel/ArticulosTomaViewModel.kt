package com.gloria.ui.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.model.ArticuloToma
import com.gloria.domain.usecase.articulo.GetArticulosTomaUseCase
import com.gloria.domain.usecase.cancelacion.CancelarTomaParcialUseCase
import com.gloria.domain.usecase.cancelacion.CancelarTomaTotalUseCase
import com.gloria.util.Variables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArticulosTomaState(
    val articulos: List<ArticuloToma> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val cancelacionExitosa: Boolean = false
)

@HiltViewModel
class ArticulosTomaViewModel @Inject constructor(
    private val getArticulosTomaUseCase: GetArticulosTomaUseCase,
    private val cancelarTomaParcialUseCase: CancelarTomaParcialUseCase,
    private val cancelarTomaTotalUseCase: CancelarTomaTotalUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ArticulosTomaState())
    val state: StateFlow<ArticulosTomaState> = _state

    fun cargarArticulos(nroToma: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getArticulosTomaUseCase(nroToma).fold(
                    onSuccess = { articulos ->
                        _state.update { it.copy(
                            articulos = articulos,
                            isLoading = false
                        )}
                    },
                    onFailure = { error ->
                        _state.update { it.copy(
                            isLoading = false,
                            error = "Error al cargar los artículos: ${error.message}"
                        )}
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Error al cargar los artículos: ${e.message}"
                )}
            }
        }
    }

    fun cancelarSeleccionados(nroToma: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val articulosSeleccionados = _state.value.articulos.filter { it.isSelected }
                
                if (articulosSeleccionados.isEmpty()) {
                    _state.update { it.copy(
                        isLoading = false,
                        error = "No hay artículos seleccionados para cancelar"
                    )}
                    return@launch
                }

                val result = if (articulosSeleccionados.size == _state.value.articulos.size) {
                    // Si todos los artículos están seleccionados, cancelar toma total
                    cancelarTomaTotalUseCase(nroToma, Variables.userdb)
                } else {
                    // Si solo algunos artículos están seleccionados, cancelar toma parcial
                    val secuencias = articulosSeleccionados.map { it.winvdSecu }
                    cancelarTomaParcialUseCase(nroToma, secuencias)
                }

                result.fold(
                    onSuccess = {
                        _state.update { it.copy(
                            isLoading = false,
                            cancelacionExitosa = true
                        )}
                    },
                    onFailure = { error ->
                        _state.update { it.copy(
                            isLoading = false,
                            error = "Error al cancelar la toma: ${error.message}"
                        )}
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )}
            }
        }
    }

    fun toggleArticuloSeleccionado(articulo: ArticuloToma) {
        val articulosActuales = _state.value.articulos.toMutableList()
        val index = articulosActuales.indexOfFirst { it.winvdSecu == articulo.winvdSecu }
        if (index != -1) {
            articulosActuales[index] = articulo.copy(isSelected = !articulo.isSelected)
            _state.update { it.copy(articulos = articulosActuales) }
        }
    }

    fun seleccionarTodos() {
        val articulosActuales = _state.value.articulos.map { it.copy(isSelected = true) }
        _state.update { it.copy(articulos = articulosActuales) }
    }

    fun deseleccionarTodos() {
        val articulosActuales = _state.value.articulos.map { it.copy(isSelected = false) }
        _state.update { it.copy(articulos = articulosActuales) }
    }

    fun seleccionarPorFamilia(familia: String) {
        val articulosActuales = _state.value.articulos.toMutableList()
        val articulosDeFamilia = articulosActuales.filter { it.fliaDesc == familia }
        val todosSeleccionados = articulosDeFamilia.all { it.isSelected }
        
        articulosActuales.forEachIndexed { index, articulo ->
            if (articulo.fliaDesc == familia) {
                articulosActuales[index] = articulo.copy(isSelected = !todosSeleccionados)
            }
        }
        
        _state.update { it.copy(articulos = articulosActuales) }
    }

    fun seleccionarPorGrupo(grupo: String) {
        val articulosActuales = _state.value.articulos.toMutableList()
        val articulosDelGrupo = articulosActuales.filter { it.grupDesc == grupo }
        val todosSeleccionados = articulosDelGrupo.all { it.isSelected }
        
        articulosActuales.forEachIndexed { index, articulo ->
            if (articulo.grupDesc == grupo) {
                articulosActuales[index] = articulo.copy(isSelected = !todosSeleccionados)
            }
        }
        
        _state.update { it.copy(articulos = articulosActuales) }
    }

    fun seleccionarPorSubgrupo(subgrupo: String) {
        val articulosActuales = _state.value.articulos.toMutableList()
        val articulosDelSubgrupo = articulosActuales.filter { it.sugrDesc == subgrupo }
        val todosSeleccionados = articulosDelSubgrupo.all { it.isSelected }
        
        articulosActuales.forEachIndexed { index, articulo ->
            if (articulo.sugrDesc == subgrupo) {
                articulosActuales[index] = articulo.copy(isSelected = !todosSeleccionados)
            }
        }
        
        _state.update { it.copy(articulos = articulosActuales) }
    }

    fun limpiarError() {
        _state.update { it.copy(error = null) }
    }
}