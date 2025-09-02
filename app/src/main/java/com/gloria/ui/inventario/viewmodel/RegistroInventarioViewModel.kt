package com.gloria.ui.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.model.InventarioCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de Registro de Inventario
 */
data class RegistroInventarioState(
    val isLoading: Boolean = false,
    val inventarios: List<InventarioCard> = emptyList(),
    val errorMessage: String? = null,
    val usuarioLogueado: String = "",
    val sucursalLogueada: Int = 1
)

/**
 * ViewModel para la pantalla de Registro de Inventario
 */
class RegistroInventarioViewModel(
    private val inventarioDetalleDao: InventarioDetalleDao,
    private val loggedUserDao: LoggedUserDao
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
                // Obtener inventarios DISTINCT con el query corregido
                inventarioDetalleDao.getInventariosCardsDistinct(
                    sucursal = uiState.value.sucursalLogueada
                ).collect { inventariosCards ->
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
                loggedUserDao.getLoggedUserSync()?.let { loggedUser ->
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
     * Refresca los inventarios
     */
    fun refreshInventarios() {
        cargarInventarios()
    }
}
