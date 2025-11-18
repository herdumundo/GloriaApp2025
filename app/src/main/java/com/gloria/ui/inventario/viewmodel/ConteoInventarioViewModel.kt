package com.gloria.ui.inventario.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.domain.usecase.inventario.GetArticulosInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarCantidadInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarCantidadInventarioSoloCantidadUseCase
import com.gloria.domain.usecase.inventario.ActualizarEstadoInventarioUseCase
import com.gloria.data.model.ArticuloInventario
import com.gloria.data.entity.ConteosLogs
import com.gloria.data.repository.InventarioDetalleRepository
import com.gloria.domain.usecase.inventario.ConteosLogsUseCase
import com.gloria.domain.usecase.inventario.EnviarConteosLogsUseCase
import com.gloria.domain.usecase.inventario.ActualizarUsuarioCerradoInventarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.util.Locale
import javax.inject.Inject

/**
 * Estado de la UI para la pantalla de Conteo de Inventario
 */
data class ConteoInventarioState(
    val isLoading: Boolean = false,
    val isRegistrando: Boolean = false, // Estado específico para el registro
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
    val registroExitoso: Boolean = false, // Indica si el registro fue exitoso
    val tipoInventario: String = "I", // Tipo de inventario: "I" = Individual, "S" = Simultáneo
    val showDetalleConteo: Boolean = false,
    val detalleConteos: List<ConteosLogs> = emptyList(),
    val detalleArticulo: ArticuloInventario? = null
)

data class EstadoConteo(
    val totalAcumulado: Int = 0,
    val cajasInput: String = "0",
    val unidadesInput: String = "0",
    val haSidoContado: Boolean = false,
    val ultimaCantidadCajas: String = "0",
    val ultimaCantidadUnidades: String = "0"
)

/**
 * ViewModel para la pantalla de Conteo de Inventario
 */
@HiltViewModel
class ConteoInventarioViewModel @Inject constructor(
    private val getArticulosInventarioUseCase: GetArticulosInventarioUseCase,
    private val actualizarCantidadInventarioUseCase: ActualizarCantidadInventarioUseCase,
    private val actualizarEstadoInventarioUseCase: ActualizarEstadoInventarioUseCase,
    private val inventarioDetalleRepository: InventarioDetalleRepository,
    private val authSessionUseCase: com.gloria.domain.usecase.AuthSessionUseCase,
    private val conteosLogsUseCase: ConteosLogsUseCase,
    private val actualizarCantidadInventarioSoloCantidadUseCase: ActualizarCantidadInventarioSoloCantidadUseCase,
    private val actualizarUsuarioCerradoInventarioUseCase: ActualizarUsuarioCerradoInventarioUseCase
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
                // Cargar el tipo de inventario
                val tipoInventario = inventarioDetalleRepository.getTipoInventario(nroInventario) ?: "I"
                
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
                                haSidoContado = true,
                                ultimaCantidadCajas = "0",
                                ultimaCantidadUnidades = "0"
                            )
                        } else {
                            // Inicializar estado de conteo vacío
                            estadosConteo[claveCompuesta] = EstadoConteo(
                                totalAcumulado = 0,
                                cajasInput = "0",
                                unidadesInput = "0",
                                haSidoContado = false,
                                ultimaCantidadCajas = "0",
                                ultimaCantidadUnidades = "0"
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
                        tipoInventario = tipoInventario,
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
    fun marcarArticuloContado(
        articuloId: Int,
        total: Int,
        cajasRegistradas: Int,
        unidadesRegistradas: Int
    ) {
        val nroInventario = _uiState.value.nroInventario
        val claveCompuesta = "${articuloId}_${nroInventario}"
        
        Log.d("LogConteo", "=== CONTANDO ARTÍCULO ===")
        Log.d("LogConteo", "Artículo ID: $articuloId")
        Log.d("LogConteo", "Número Inventario: $nroInventario")
        Log.d("LogConteo", "Clave compuesta: $claveCompuesta")
        Log.d("LogConteo", "Cantidad total acumulada: $total")
        Log.d("LogConteo", "Cajas registradas: $cajasRegistradas")
        Log.d("LogConteo", "Unidades registradas: $unidadesRegistradas")
        
        val currentContados = _uiState.value.articulosContados.toMutableSet()
        val currentCantidades = _uiState.value.cantidadesContadas.toMutableMap()
        
        // Mostrar estado anterior
        Log.d("LogConteo", "Estado anterior - Artículos contados: $currentContados")
        Log.d("LogConteo", "Estado anterior - Cantidades: $currentCantidades")
        
        currentContados.add(articuloId)
        currentCantidades[claveCompuesta] = total
        
        // Mostrar estado nuevo
        Log.d("LogConteo", "Estado nuevo - Artículos contados: $currentContados")
        Log.d("LogConteo", "Estado nuevo - Cantidades: $currentCantidades")
        Log.d("LogConteo", "=== FIN CONTEO ===")
        
        _uiState.value = _uiState.value.copy(
            articulosContados = currentContados,
            cantidadesContadas = currentCantidades
        )

        registrarConteoLog(articuloId, cajasRegistradas, unidadesRegistradas)

        val articulo = _uiState.value.articulos.firstOrNull { it.winvdSecu == articuloId }

        // Persistir la cantidad total en la tabla local STKW002INV
        /*
        if (articulo != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    actualizarCantidadInventarioSoloCantidadUseCase(
                        numeroInventario = nroInventario,
                        secuencia = articuloId,
                        codigoArticulo = articulo.winvdArt,
                        cantidad = total
                    )
                } catch (e: Exception) {
                    Log.e("ConteoInventarioVM", "Error al actualizar cantidad total en STKW002INV", e)
                }
            }
        }
        */
    }
    
    fun mostrarDetalleConteo(articulo: ArticuloInventario) {
        viewModelScope.launch {
            val logs = withContext(Dispatchers.IO) {
                conteosLogsUseCase.getByArticulo(articulo.winvd_nro_inv, articulo.winvdSecu)
            }
            _uiState.value = _uiState.value.copy(
                showDetalleConteo = true,
                detalleConteos = logs,
                detalleArticulo = articulo
            )
        }
    }

    fun cerrarDetalleConteo() {
        _uiState.value = _uiState.value.copy(
            showDetalleConteo = false,
            detalleConteos = emptyList(),
            detalleArticulo = null
        )
    }

    fun eliminarConteosEstadoN(numeroInventario: Int? = null) {
        val targetInventario = numeroInventario ?: _uiState.value.nroInventario
        if (targetInventario <= 0) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                conteosLogsUseCase.deleteByInventarioWithEstado(targetInventario, estado = "N")
            } catch (e: Exception) {
                Log.e("ConteoInventarioVM", "Error al eliminar conteos con estado N", e)
            }
        }
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
                _uiState.value = _uiState.value.copy(isRegistrando = true)
                
                // Pequeño delay para asegurar que la UI muestre el loading
                delay(100)
                
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
                Log.d("LogConteo", "Total artículos a procesar: ${articulosFiltrados.size}")
                
                // Determinar el estado según el tipo de inventario
                val tipoInventario = _uiState.value.tipoInventario
                val estadoFinal = if (tipoInventario == "S") "S" else "P"
                
                Log.d("LogConteo", "Tipo de inventario: $tipoInventario")
                Log.d("LogConteo", "Estado a aplicar: $estadoFinal")
                
                // Actualizar cada artículo en la base de datos (en hilo de fondo)
                withContext(Dispatchers.IO) {
                    // Obtener usuario logueado
                    val usuarioLogueado = authSessionUseCase.getCurrentUser()
                    val usuarioCerrado = (usuarioLogueado?.username ?: "UNKNOWN").uppercase(Locale.getDefault())
                    
                    Log.d("LogConteo", "=== DEBUG USUARIO CERRADO ===")
                    Log.d("LogConteo", "Usuario logueado completo: $usuarioLogueado")
                    Log.d("LogConteo", "Username extraído: ${usuarioLogueado?.username}")
                    Log.d("LogConteo", "Usuario final para WINVE_LOGIN_CERRADO_WEB: '$usuarioCerrado'")
                    Log.d("LogConteo", "Longitud del usuario: ${usuarioCerrado.length}")
                    Log.d("LogConteo", "=== FIN DEBUG USUARIO ===")
                    
                    // Primero, actualizar el estado de todos los artículos
                    Log.d("LogConteo", "Actualizando estado de todos los artículos a '$estadoFinal'")
                    actualizarEstadoInventarioUseCase(
                        numeroInventario = nroInventario,
                        estado = estadoFinal
                    )

                    actualizarUsuarioCerradoInventarioUseCase(
                        numeroInventario = nroInventario,
                        usuarioCerrado = usuarioCerrado
                    )
                    
                    // Luego, actualizar solo las cantidades de los artículos contados
                    var contador = 0
                    val totalContados = cantidadesContadasSnapshot.size
                    
                    if (totalContados > 0) {
                        Log.d("LogConteo", "Actualizando cantidades de $totalContados artículos contados")
                        
                        cantidadesContadasSnapshot.forEach { (clave, cantidad) ->
                            // Extraer secuencia de la clave "secuencia_inventario"
                            val secuencia = clave.split("_").first().toIntOrNull()
                            // Aceptar cantidad 0 (para permitir "poner en cero") y normalizar negativos a 0
                            val cantidadNormalizada = if (cantidad < 0) 0 else cantidad
                            if (secuencia != null) {
                                actualizarCantidadInventarioUseCase(
                                    numeroInventario = nroInventario,
                                    secuencia = secuencia,
                                    cantidad = cantidadNormalizada,
                                    estado = estadoFinal,
                                    usuarioCerrado = usuarioCerrado
                                )
                                
                                contador++
                                if (contador % 10 == 0 || contador == totalContados) {
                                    Log.d("LogConteo", "Actualizados $contador de $totalContados artículos contados")
                                }
                            }
                        }
                    } else {
                        Log.d("LogConteo", "No hay artículos contados para actualizar")
                    }
                }
                
                // Actualizar estado de los conteos locales a 'P'
                withContext(Dispatchers.IO) {
                    try {
                        conteosLogsUseCase.actualizarEstadoPorInventario(nroInventario, "P")
                    } catch (e: Exception) {
                        Log.e("ConteoInventarioVM", "Error al actualizar estado de conteos locales", e)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isRegistrando = false,
                    errorMessage = null,
                    registroExitoso = true
                )

                Log.d("LogConteo", "=== REGISTRO COMPLETADO EXITOSAMENTE ===")

            } catch (e: Exception) {
                Log.e("ConteoInventarioVM", "Error al registrar inventario", e)
                _uiState.value = _uiState.value.copy(
                    isRegistrando = false,
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

    private fun registrarConteoLog(
        articuloId: Int,
        cajasRegistradas: Int,
        unidadesRegistradas: Int
    ) {
        val articulo = _uiState.value.articulos.firstOrNull { it.winvdSecu == articuloId } ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuario = authSessionUseCase.getCurrentUser()?.username ?: "UNKNOWN"
                Log.d("ConteoInventarioVM", "Registrando conteo log -> articuloId: $articuloId, cajas: $cajasRegistradas, unidades: $unidadesRegistradas, usuario: $usuario")

                if (cajasRegistradas == 0 && unidadesRegistradas == 0) {
                    Log.d("ConteoInventarioVM", "No se registran logs: ambas cantidades son 0")
                    return@launch
                }

                var siguienteOrden = conteosLogsUseCase.getNextOrden(
                    articulo.winvd_nro_inv,
                    articulo.winvdSecu
                )

                if (cajasRegistradas != 0) {
                    val cantidadConvertida = cajasRegistradas * articulo.caja
                    val conteoLogCajas = ConteosLogs(
                        id = System.nanoTime(),
                        orden = siguienteOrden,
                        winvd_nro_inv = articulo.winvd_nro_inv,
                        winvd_secu = articulo.winvdSecu,
                        winvdArt = articulo.winvdArt,
                        winvdLote = articulo.winvdLote,
                        usuario = usuario.uppercase(Locale.getDefault()),
                        estado = "N",
                        cantidadIngresada = cajasRegistradas,
                        cantidadConvertida = cantidadConvertida,
                        tipo = "CAJAS"
                    )
                    conteosLogsUseCase.insert(conteoLogCajas)
                    siguienteOrden += 1
                }

                if (unidadesRegistradas != 0) {
                    val conteoLogUnidades = ConteosLogs(
                        id = System.nanoTime(),
                        orden = siguienteOrden,
                        winvd_nro_inv = articulo.winvd_nro_inv,
                        winvd_secu = articulo.winvdSecu,
                        winvdArt = articulo.winvdArt,
                        winvdLote = articulo.winvdLote,
                        usuario = usuario.uppercase(Locale.getDefault()),
                        estado = "N",
                        cantidadIngresada = unidadesRegistradas,
                        cantidadConvertida = unidadesRegistradas,
                        tipo = "UNIDAD"
                    )
                    conteosLogsUseCase.insert(conteoLogUnidades)
                }
            } catch (e: Exception) {
                Log.e("ConteoInventarioVM", "Error al registrar conteo log", e)
            }
        }
    }

}
