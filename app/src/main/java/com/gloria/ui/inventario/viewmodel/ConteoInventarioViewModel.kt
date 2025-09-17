package com.gloria.ui.inventario.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.domain.usecase.inventario.GetArticulosInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarCantidadInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarEstadoInventarioUseCase
import com.gloria.data.model.ArticuloInventario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la UI para la pantalla de Conteo de Inventario
 */
data class ConteoInventarioState(
    val isLoading: Boolean = false,
    val articulos: List<ArticuloInventario> = emptyList(),
    val errorMessage: String? = null,
    val nroInventario: Int = 0,
    val totalArticulos: Int = 0,
    val cantidadTotal: Int = 0,
    val searchQuery: String = "",
    val articulosContados: Set<Int> = emptySet(), // IDs de artículos que han sido contados
    val cantidadesContadas: Map<String, Int> = emptyMap(), // ID -> cantidad contada
    val showAlertNoContados: Boolean = false,
    val showConfirmRegistro: Boolean = false, // Para confirmar registro sin contar todo
    val estadosConteo: Map<String, EstadoConteo> = emptyMap(), // Estado de conteo por artículo
    val registroExitoso: Boolean = false // Indica si el registro fue exitoso
)

data class EstadoConteo(
    val totalAcumulado: Int = 0,
    val cajasInput: String = "0",
    val unidadesInput: String = "0",
    val haSidoContado: Boolean = false
)

/**
 * ViewModel para la pantalla de Conteo de Inventario
 */
@HiltViewModel
class ConteoInventarioViewModel @Inject constructor(
    private val getArticulosInventarioUseCase: GetArticulosInventarioUseCase,
    private val actualizarCantidadInventarioUseCase: ActualizarCantidadInventarioUseCase,
    private val actualizarEstadoInventarioUseCase: ActualizarEstadoInventarioUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ConteoInventarioState())
    val uiState: StateFlow<ConteoInventarioState> = _uiState.asStateFlow()
    
    /**
     * Carga los artículos de un inventario específico
     */
    fun cargarArticulosInventario(nroInventario: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                nroInventario = nroInventario
            )
            
            try {
                getArticulosInventarioUseCase(nroInventario).collect { articulos ->
                    // Inicializar artículos contados y cantidades basado en winvdCantInv
                    val articulosContados = mutableSetOf<Int>()
                    val cantidadesContadas = mutableMapOf<String, Int>()
                    val estadosConteo = mutableMapOf<String, EstadoConteo>()
                    
                    articulos.forEach { articulo ->
                        val claveCompuesta = "${articulo.winvdSecu}_${nroInventario}"
                        
                        if (articulo.winvdCantInv > 0) {
                            articulosContados.add(articulo.winvdSecu)
                            cantidadesContadas[claveCompuesta] = articulo.winvdCantInv
                            
                            // Inicializar estado de conteo con cantidad existente
                            estadosConteo[claveCompuesta] = EstadoConteo(
                                totalAcumulado = articulo.winvdCantInv,
                                cajasInput = "0",
                                unidadesInput = "0",
                                haSidoContado = true
                            )
                        } else {
                            // Inicializar estado de conteo vacío
                            estadosConteo[claveCompuesta] = EstadoConteo(
                                totalAcumulado = 0,
                                cajasInput = "0",
                                unidadesInput = "0",
                                haSidoContado = false
                            )
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        articulos = articulos,
                        totalArticulos = articulos.size,
                        articulosContados = articulosContados,
                        cantidadesContadas = cantidadesContadas,
                        estadosConteo = estadosConteo,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar artículos: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Actualiza la búsqueda de productos
     */
    fun actualizarBusqueda(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    
    /**
     * Obtiene los artículos filtrados por búsqueda
     */
    fun getArticulosFiltrados(): List<ArticuloInventario> {
        val query = uiState.value.searchQuery.lowercase()
        return if (query.isEmpty()) {
            uiState.value.articulos
        } else {
            uiState.value.articulos.filter { articulo ->
                // Búsqueda por descripción del artículo
                articulo.artDesc.lowercase().contains(query) ||
                // Búsqueda por código del artículo
                articulo.winvdArt.lowercase().contains(query) ||
                // Búsqueda por código de barras
                articulo.codBarra.lowercase().contains(query) ||
                // Búsqueda por familia
                articulo.fliaDesc.lowercase().contains(query) ||
                // Búsqueda por grupo
                articulo.grupDesc.lowercase().contains(query) ||
                // Búsqueda por lote
                articulo.winvdLote.lowercase().contains(query)
            }
        }
    }
    
    /**
     * Obtiene el número de artículos filtrados
     */
    fun getArticulosFiltradosCount(): Int {
        return getArticulosFiltrados().size
    }
    
    /**
     * Limpia la búsqueda
     */
    fun limpiarBusqueda() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Refresca los artículos del inventario
     */
    fun refreshArticulos() {
        if (uiState.value.nroInventario > 0) {
            cargarArticulosInventario(uiState.value.nroInventario)
        }
    }
    
    /**
     * Actualiza el estado de conteo de un artículo
     */
    fun actualizarEstadoConteo(articuloId: Int, estado: EstadoConteo) {
        val nroInventario = _uiState.value.nroInventario
        val claveCompuesta = "${articuloId}_${nroInventario}"
        
        val currentEstados = _uiState.value.estadosConteo.toMutableMap()
        currentEstados[claveCompuesta] = estado
        
        _uiState.value = _uiState.value.copy(
            estadosConteo = currentEstados
        )
    }
    
    /**
     * Obtiene el estado de conteo de un artículo
     */
     
    fun obtenerEstadoConteo(articuloId: Int): EstadoConteo {
        val nroInventario = _uiState.value.nroInventario
        val claveCompuesta = "${articuloId}_${nroInventario}"
        
        return _uiState.value.estadosConteo[claveCompuesta] ?: EstadoConteo(
            totalAcumulado = 0,
            cajasInput = "0",
            unidadesInput = "0",
            haSidoContado = false
        )
    }
    
    /**
     * Marca un artículo como contado con su cantidad
     */
    fun marcarArticuloContado(articuloId: Int, cantidad: Int) {
        val nroInventario = _uiState.value.nroInventario
        val claveCompuesta = "${articuloId}_${nroInventario}"
        
        Log.d("LogConteo", "=== CONTANDO ARTÍCULO ===")
        Log.d("LogConteo", "Artículo ID: $articuloId")
        Log.d("LogConteo", "Número Inventario: $nroInventario")
        Log.d("LogConteo", "Clave compuesta: $claveCompuesta")
        Log.d("LogConteo", "Cantidad recibida: $cantidad")
        
        val currentContados = _uiState.value.articulosContados.toMutableSet()
        val currentCantidades = _uiState.value.cantidadesContadas.toMutableMap()
        
        // Mostrar estado anterior
        Log.d("LogConteo", "Estado anterior - Artículos contados: $currentContados")
        Log.d("LogConteo", "Estado anterior - Cantidades: $currentCantidades")
        
        currentContados.add(articuloId)
        currentCantidades[claveCompuesta] = cantidad
        
        // Mostrar estado nuevo
        Log.d("LogConteo", "Estado nuevo - Artículos contados: $currentContados")
        Log.d("LogConteo", "Estado nuevo - Cantidades: $currentCantidades")
        Log.d("LogConteo", "=== FIN CONTEO ===")
        
        _uiState.value = _uiState.value.copy(
            articulosContados = currentContados,
            cantidadesContadas = currentCantidades
        )
    }
    
    /**
     * Verifica si todos los artículos han sido contados
     */
    fun verificarTodosContados(): Boolean {
        val articulosFiltrados = getArticulosFiltrados()
        return articulosFiltrados.all { articulo ->
            _uiState.value.articulosContados.contains(articulo.winvdSecu)
        }
    }
    
    /**
     * Obtiene la lista de artículos no contados
     */
    fun getArticulosNoContados(): List<ArticuloInventario> {
        val articulosFiltrados = getArticulosFiltrados()
        return articulosFiltrados.filter { articulo ->
            !_uiState.value.articulosContados.contains(articulo.winvdSecu)
        }
    }
    
    /**
     * Valida si se puede registrar el inventario
     */
    fun validarRegistro(): Boolean {
        val todosContados = verificarTodosContados()
        if (!todosContados) {
            _uiState.value = _uiState.value.copy(showAlertNoContados = true)
        } else {
            // Si todos fueron contados, proceder directamente
            procesarRegistro()
        }
        return true // Siempre permite el registro
    }
    
    /**
     * Confirma el registro a pesar de no haber contado todo
     */
    fun confirmarRegistroSinContarTodo() {
        _uiState.value = _uiState.value.copy(
            showAlertNoContados = false,
            showConfirmRegistro = true
        )
    }
    
    /**
     * Procesa el registro confirmado
     */
    fun confirmarYProcesarRegistro() {
        _uiState.value = _uiState.value.copy(showConfirmRegistro = false)
        procesarRegistro()
    }
    
    /**
     * Procesa el registro del inventario
     */
    fun procesarRegistro() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val articulosFiltrados = getArticulosFiltrados()
                val nroInventario = _uiState.value.nroInventario
                
                Log.d("LogConteo", "=== INICIANDO PROCESO DE REGISTRO ===")
                Log.d("LogConteo", "Número de inventario: $nroInventario")
                Log.d("LogConteo", "Artículos filtrados: ${articulosFiltrados.size}")
                Log.d("LogConteo", "Array de artículos contados: ${_uiState.value.articulosContados}")
                Log.d("LogConteo", "Array de cantidades contadas: ${_uiState.value.cantidadesContadas}")
                
                // Hacer una copia del mapa para evitar modificaciones durante el proceso
                val cantidadesContadasSnapshot = _uiState.value.cantidadesContadas.toMap()
                Log.d("LogConteo", "Snapshot de cantidades: $cantidadesContadasSnapshot")
                
                // Actualizar cada artículo en la base de datos
                articulosFiltrados.forEach { articulo ->
                    val claveCompuesta = "${articulo.winvdSecu}_${nroInventario}"
                    
                    Log.d("LogConteo", "Procesando artículo ${articulo.winvdSecu}")
                    Log.d("LogConteo", "Clave compuesta: $claveCompuesta")
                    Log.d("LogConteo", "Claves del snapshot: ${cantidadesContadasSnapshot.keys}")
                    Log.d("LogConteo", "¿Tiene cantidad?: ${cantidadesContadasSnapshot.containsKey(claveCompuesta)}")
                    Log.d("LogConteo", "Cantidad directa: ${cantidadesContadasSnapshot[claveCompuesta]}")
                    
                    val cantidadInventario = if (cantidadesContadasSnapshot.containsKey(claveCompuesta)) {
                        // Si tiene cantidad en el snapshot, usar esa cantidad
                        val cantidad = cantidadesContadasSnapshot[claveCompuesta] ?: 0
                        Log.d("LogConteo", "Artículo ${articulo.winvdSecu} contado con cantidad: $cantidad")
                        cantidad
                    } else {
                        // Si no tiene cantidad en el mapa, poner cero
                        Log.d("LogConteo", "Artículo ${articulo.winvdSecu} NO contado, cantidad: 0")
                        0
                    }
                    
                    Log.d("LogConteo", "Actualizando BD - Inventario: $nroInventario, Secuencia: ${articulo.winvdSecu}, Cantidad: $cantidadInventario")
                    
                    // Actualizar en la base de datos
                    actualizarCantidadInventarioUseCase(
                        numeroInventario = nroInventario,
                        secuencia = articulo.winvdSecu,
                        cantidad = cantidadInventario,
                        estado = "P"
                    )
                }
                
                // Actualizar estado del inventario principal a "P"
                actualizarEstadoInventarioUseCase(
                    numeroInventario = nroInventario,
                    estado = "P"
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    registroExitoso = true
                )
                
                Log.d("LogConteo", "=== REGISTRO COMPLETADO EXITOSAMENTE ===")
                
            } catch (e: Exception) {
                Log.e("ConteoInventarioVM", "Error al registrar inventario", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar inventario: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Cierra la alerta de productos no contados
     */
    fun cerrarAlertNoContados() {
        _uiState.value = _uiState.value.copy(showAlertNoContados = false)
    }
    
    /**
     * Resetea el estado de registro exitoso
     */
    fun resetearRegistroExitoso() {
        _uiState.value = _uiState.value.copy(registroExitoso = false)
    }
    
    /**
     * Cancela el registro
     */
    fun cancelarRegistro() {
        _uiState.value = _uiState.value.copy(
            showAlertNoContados = false,
            showConfirmRegistro = false
        )
    }
}
