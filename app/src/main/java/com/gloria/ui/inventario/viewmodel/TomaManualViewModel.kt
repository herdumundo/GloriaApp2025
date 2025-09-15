package com.gloria.ui.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.gloria.data.dao.*
import com.gloria.data.entity.*
import com.gloria.data.entity.Sucursal
import com.gloria.data.model.ArticuloLote
import com.gloria.util.InventoryLogger
import com.gloria.domain.usecase.toma.InsertarCabeceraInventarioUseCase
import com.gloria.domain.usecase.toma.InsertarDetalleInventarioUseCase
import com.gloria.domain.usecase.toma.InsertarCabeceraYDetalleInventarioUseCase
import com.gloria.domain.usecase.toma.GetArticulosLotesUseCase
import com.gloria.domain.usecase.toma.GetAreasUseCase
import com.gloria.domain.usecase.toma.GetDepartamentosUseCase
import com.gloria.domain.usecase.toma.GetSeccionesUseCase
import com.gloria.domain.usecase.toma.GetFamiliasUseCase
import com.gloria.domain.usecase.toma.GetGruposUseCase
import com.gloria.domain.usecase.toma.GetSubgruposUseCase
import com.gloria.domain.usecase.toma.GetLoggedUserUseCase
import com.gloria.domain.usecase.toma.GetSucursalesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Modelo especial para la opción "Todos"
data class FamiliaTodos(
    val codigo: Int = -1,
    val descripcion: String = "Todas las familias"
)

data class TomaManualUiState(
    val selectedSucursal: Sucursal? = null,
    val selectedDepartamento: SucursalDepartamento? = null,
    val selectedArea: Area? = null,
    val selectedDpto: Departamento? = null,
    val selectedSeccion: Seccion? = null,
    val selectedFamilia: Familia? = null,
    val selectedGrupos: List<Grupo> = emptyList(),
    val selectedSubgrupos: List<Subgrupo> = emptyList(),
    val sucursales: List<Sucursal> = emptyList(),
    val departamentos: List<SucursalDepartamento> = emptyList(),
    val areas: List<Area> = emptyList(),
    val dptos: List<Departamento> = emptyList(),
    val secciones: List<Seccion> = emptyList(),
    val familias: List<Familia> = emptyList(),
    val grupos: List<Grupo> = emptyList(),
    val subgrupos: List<Subgrupo> = emptyList(),
    val showSucursalDialog: Boolean = false,
    val showDepartamentoDialog: Boolean = false,
    val showAreaDialog: Boolean = false,
    val showDptoDialog: Boolean = false,
    val showSeccionDialog: Boolean = false,
    val showFamiliaDialog: Boolean = false,
    val showGrupoDialog: Boolean = false,
    val showSubgrupoDialog: Boolean = false,
    val isGruposTodos: Boolean = false,
    val isSubgruposTodos: Boolean = false,
    val isFamiliaTodos: Boolean = false,
    val showArticulosLotesDialog: Boolean = false,
    val articulosLotes: List<ArticuloLote> = emptyList(),
    val selectedArticulosLotes: List<ArticuloLote> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loadingMessage: String? = null,
    val progressPercentage: Float = 0f,
    val currentProgress: Int = 0,
    val totalProgress: Int = 0,
    val loadingProgress: Float = 0f,
    val successMessage: String? = null,
    val showConfirmarTomaDialog: Boolean = false,
    val inventarioVisible: Boolean = false,
    val tipoToma: String = "M" // "M" = Manual, "C" = Criterio
)

@HiltViewModel
class TomaManualViewModel @Inject constructor(
    private val getSucursalesTomaUseCase: GetSucursalesUseCase,
    private val getAreasUseCase: GetAreasUseCase,
    private val getDepartamentosUseCase: GetDepartamentosUseCase,
    private val getSeccionesUseCase: GetSeccionesUseCase,
    private val getFamiliasUseCase: GetFamiliasUseCase,
    private val getGruposUseCase: GetGruposUseCase,
    private val getSubgruposUseCase: GetSubgruposUseCase,
    private val getLoggedUserUseCase: GetLoggedUserUseCase,
    private val insertarCabeceraYDetalleInventarioUseCase: InsertarCabeceraYDetalleInventarioUseCase,
    private val getArticulosLotesUseCase: GetArticulosLotesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TomaManualUiState())
    val uiState: StateFlow<TomaManualUiState> = _uiState.asStateFlow()
    
    init {
        // Las sucursales y áreas se cargan desde el LaunchedEffect de la pantalla
    }
    
    fun loadSucursales() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val sucursales = getSucursalesTomaUseCase()
                _uiState.value = _uiState.value.copy(
                    sucursales = sucursales,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar sucursales: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun loadAreas() {
        viewModelScope.launch {
            try {
                getAreasUseCase().collect { areas ->
                    _uiState.value = _uiState.value.copy(areas = areas)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar áreas: ${e.message}"
                )
            }
        }
    }
    
    fun onSucursalSelected(sucursal: Sucursal) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedSucursal = sucursal,
                    selectedDepartamento = null,
                    selectedArea = null,
                    selectedDpto = null,
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    departamentos = emptyList(),
                    dptos = emptyList(),
                    secciones = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    showSucursalDialog = false,
                    showDepartamentoDialog = true,
                    isGruposTodos = false
                )
                
                // Cargar departamentos de la sucursal seleccionada
                val departamentos = getSucursalesTomaUseCase.getDepartamentosBySucursal(sucursal.sucCodigo)
                _uiState.value = _uiState.value.copy(departamentos = departamentos)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar departamentos: ${e.message}",
                    showSucursalDialog = false
                )
            }
        }
    }
    
    fun onDepartamentoSelected(departamento: SucursalDepartamento) {
        _uiState.value = _uiState.value.copy(
            selectedDepartamento = departamento,
            selectedArea = null,
            selectedDpto = null,
            selectedSeccion = null,
            selectedFamilia = null,
            selectedGrupos = emptyList(),
            selectedSubgrupos = emptyList(),
            dptos = emptyList(),
            secciones = emptyList(),
            familias = emptyList(),
            grupos = emptyList(),
            subgrupos = emptyList(),
            showDepartamentoDialog = false,
            showAreaDialog = true,
            isGruposTodos = false
        )
    }
    
    fun onAreaSelected(area: Area) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedArea = area,
                    selectedDpto = null,
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    dptos = emptyList(),
                    secciones = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    showAreaDialog = false,
                    showDptoDialog = true,
                    isGruposTodos = false
                )
                
                // Cargar departamentos del área seleccionada
                getDepartamentosUseCase.getDepartamentosByArea(area.areaCodigo).collect { dptos ->
                    _uiState.value = _uiState.value.copy(dptos = dptos)
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar departamentos: ${e.message}",
                    showAreaDialog = false
                )
            }
        }
    }
    
    fun onDptoSelected(dpto: Departamento) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedDpto = dpto,
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    secciones = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    showDptoDialog = false,
                    showSeccionDialog = true,
                    isGruposTodos = false
                )
                
                // Cargar secciones del departamento seleccionado
                getSeccionesUseCase.getSeccionesByDepartamento(dpto.dptoCodigo).collect { secciones ->
                    _uiState.value = _uiState.value.copy(secciones = secciones)
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar secciones: ${e.message}",
                    showDptoDialog = false
                )
            }
        }
    }
    
    fun onSeccionSelected(seccion: Seccion) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedSeccion = seccion,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    showSeccionDialog = false,
                    showFamiliaDialog = true,
                    isGruposTodos = false
                )
                
                // Cargar familias de la sección seleccionada
                getFamiliasUseCase.getFamiliasBySeccion(seccion.seccCodigo).collect { familias ->
                    _uiState.value = _uiState.value.copy(familias = familias)
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar familias: ${e.message}",
                    showSeccionDialog = false
                )
            }
        }
    }
    
    fun onFamiliaSelected(familia: Familia) {
        InventoryLogger.logInfo("FAMILIA_SELECTED", "Familia seleccionada: ${familia.fliaCodigo} - ${familia.fliaDesc}")
        InventoryLogger.logFamilia(familia, "SELECTED")
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedFamilia = familia,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    showFamiliaDialog = false,
                    showGrupoDialog = true,
                    isFamiliaTodos = false,
                    isGruposTodos = false
                )
                
                // Cargar grupos de la familia seleccionada
                InventoryLogger.logInfo("CARGAR_GRUPOS_FAMILIA", "Cargando grupos para familia: ${familia.fliaCodigo}")
                getGruposUseCase.getGruposByFamilia(familia.fliaCodigo).collect { grupos ->
                    InventoryLogger.logInfo("GRUPOS_CARGADOS_FAMILIA", "Grupos cargados para familia ${familia.fliaCodigo}: ${grupos.size}")
                    grupos.forEach { grupo ->
                        InventoryLogger.logGrupo(grupo, "FAMILIA_${familia.fliaCodigo}")
                    }
                    _uiState.value = _uiState.value.copy(grupos = grupos)
                }
                
            } catch (e: Exception) {
                InventoryLogger.logError("ERROR_CARGAR_GRUPOS_FAMILIA", "Error al cargar grupos para familia ${familia.fliaCodigo}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar grupos: ${e.message}",
                    showFamiliaDialog = false
                )
            }
        }
    }
    
    fun onFamiliaTodosSelected() {
        InventoryLogger.logInfo("FAMILIA_TODOS_SELECTED", "Seleccionada opción 'Todas las familias'")
        
        viewModelScope.launch {
            try {
                // Obtener todas las familias disponibles para la sección seleccionada
                val seccion = _uiState.value.selectedSeccion
                if (seccion != null) {
                    InventoryLogger.logInfo("CARGAR_FAMILIAS_TODAS", "Cargando todas las familias de la sección: ${seccion.seccCodigo}")
                    
                    val todasLasFamilias = getFamiliasUseCase.getFamiliasBySeccion(seccion.seccCodigo).first()
                    InventoryLogger.logInfo("FAMILIAS_TODAS_COUNT", "Total de familias disponibles: ${todasLasFamilias.size}")
                    
                    // Seleccionar todas las familias - NO mostrar diálogo de grupo
                    _uiState.value = _uiState.value.copy(
                        selectedFamilia = null,
                        selectedGrupos = emptyList(),
                        selectedSubgrupos = emptyList(),
                        grupos = emptyList(),
                        subgrupos = emptyList(),
                        showFamiliaDialog = false,
                        showGrupoDialog = false, // ❌ NO mostrar diálogo de grupo
                        isFamiliaTodos = true,
                        isGruposTodos = false,
                        familias = todasLasFamilias
                    )
                    
                    InventoryLogger.logInfo("FAMILIAS_TODAS_SELECT", "Seleccionando todas las familias - saltando diálogo de grupo")
                    todasLasFamilias.forEach { familia ->
                        InventoryLogger.logDebug("TODAS_FAMILIAS_SELECTED", "Familia: ${familia.fliaCodigo} - ${familia.fliaDesc} (Área: ${familia.fliaArea}, Dpto: ${familia.fliaDpto}, Sección: ${familia.fliaSeccion})")
                    }
                    
                    // ❌ NO cargar grupos cuando se selecciona "Todas las familias"
                    // Los grupos no son necesarios para la consulta
                    InventoryLogger.logInfo("FAMILIAS_TODAS_SKIP_GRUPOS", "Saltando carga de grupos - no necesarios para consulta de todas las familias")
                    
                    // ✅ Ejecutar búsqueda automáticamente
                    InventoryLogger.logInfo("AUTO_SEARCH_FAMILIA_TODOS", "Ejecutando búsqueda automática para todas las familias")
                    loadArticulosLotes()
                    
                } else {
                    InventoryLogger.logWarning("FAMILIAS_TODAS_ERROR", "No hay sección seleccionada para cargar todas las familias")
                }
                
            } catch (e: Exception) {
                InventoryLogger.logError("ERROR_FAMILIAS_TODAS", "Error al cargar todas las familias", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar todas las familias: ${e.message}",
                    showFamiliaDialog = false
                )
            }
        }
    }
    
    // Método para limpiar la selección de "Todos" cuando se selecciona una familia específica
    fun clearFamiliaTodos() {
        _uiState.value = _uiState.value.copy(isFamiliaTodos = false)
    }
    
    fun onGrupoToggled(grupo: Grupo) {
        InventoryLogger.logInfo("GRUPO_TOGGLED", "Alternando grupo: ${grupo.grupCodigo} - ${grupo.grupDesc}")
        
        val currentSelected = _uiState.value.selectedGrupos.toMutableList()
        
        if (currentSelected.contains(grupo)) {
            currentSelected.remove(grupo)
            InventoryLogger.logInfo("GRUPO_REMOVED", "Grupo removido: ${grupo.grupCodigo}")
        } else {
            currentSelected.add(grupo)
            InventoryLogger.logInfo("GRUPO_ADDED", "Grupo agregado: ${grupo.grupCodigo}")
        }
        
        val allGrupos = _uiState.value.grupos
        val isAllSelected = currentSelected.size == allGrupos.size && allGrupos.all { it in currentSelected }
        
        InventoryLogger.logInfo("GRUPOS_SELECTION", "Total grupos seleccionados: ${currentSelected.size}, Todos seleccionados: $isAllSelected")
        currentSelected.forEach { selectedGrupo ->
            InventoryLogger.logGrupo(selectedGrupo, "SELECTED")
        }
        
        _uiState.value = _uiState.value.copy(
            selectedGrupos = currentSelected,
            isGruposTodos = isAllSelected
        )
    }
    
    fun onGruposTodosToggled() {
        val allGrupos = _uiState.value.grupos
        val isCurrentlyAllSelected = _uiState.value.isGruposTodos
        
        InventoryLogger.logInfo("GRUPOS_TODOS_TOGGLED", "Alternando selección de todos los grupos. Actualmente todos seleccionados: $isCurrentlyAllSelected")
        InventoryLogger.logInfo("GRUPOS_TODOS_COUNT", "Total de grupos disponibles: ${allGrupos.size}")
        
        _uiState.value = if (isCurrentlyAllSelected) {
            // Deseleccionar todos
            InventoryLogger.logInfo("GRUPOS_TODOS_DESELECT", "Deseleccionando todos los grupos")
            _uiState.value.copy(
                selectedGrupos = emptyList(),
                isGruposTodos = false
            )
        } else {
            // Seleccionar todos
            InventoryLogger.logInfo("GRUPOS_TODOS_SELECT", "Seleccionando todos los grupos")
            allGrupos.forEach { grupo ->
                InventoryLogger.logGrupo(grupo, "TODOS_SELECTED")
            }
            _uiState.value.copy(
                selectedGrupos = allGrupos,
                isGruposTodos = true
            )
        }
    }
    
    fun confirmGrupoSelection() {
        InventoryLogger.logInfo("CONFIRM_GRUPO_SELECTION", "Confirmando selección de grupos")
        
        val isGruposTodos = _uiState.value.isGruposTodos
        
        if (isGruposTodos) {
            // Si se seleccionaron todos los grupos, saltar el diálogo de subgrupos y cargar artículos directamente
            InventoryLogger.logInfo("GRUPOS_TODOS_CONFIRM", "Se seleccionaron todos los grupos - saltando diálogo de subgrupos")
            
            _uiState.value = _uiState.value.copy(
                selectedSubgrupos = emptyList(),
                subgrupos = emptyList(),
                showGrupoDialog = false,
                showSubgrupoDialog = false, // ✅ No mostrar diálogo de subgrupos
                isSubgruposTodos = false
            )
            
            // Cargar artículos directamente
            loadArticulosLotes()
            return
        }
        
        // Lógica normal para grupos específicos
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedSubgrupos = emptyList(),
                    subgrupos = emptyList(),
                    showGrupoDialog = false,
                    showSubgrupoDialog = true,
                    isSubgruposTodos = false
                )
                
                // Cargar subgrupos de todos los grupos seleccionados
                val selectedGrupos = _uiState.value.selectedGrupos
                InventoryLogger.logInfo("CARGAR_SUBGRUPOS", "Iniciando carga de subgrupos para ${selectedGrupos.size} grupos seleccionados")
                
                if (selectedGrupos.isNotEmpty()) {
                    // Log de grupos seleccionados
                    selectedGrupos.forEach { grupo ->
                        InventoryLogger.logGrupo(grupo, "CONFIRMED_SELECTION")
                    }
                    
                    // Obtener subgrupos de todos los grupos seleccionados usando awaitAll
                    val allSubgrupos = mutableListOf<Subgrupo>()
                    
                    // Usar la consulta con JOIN completo para obtener subgrupos
                    val areaCodigo = _uiState.value.selectedArea?.areaCodigo ?: 0
                    val dptoCodigo = _uiState.value.selectedDpto?.dptoCodigo ?: 0
                    val seccCodigo = _uiState.value.selectedSeccion?.seccCodigo ?: 0
                    val fliaCodigo = _uiState.value.selectedFamilia?.fliaCodigo ?: 0
                    val grupCodigos = selectedGrupos.map { it.grupCodigo }
                    
                    // Verificar si tenemos todos los parámetros necesarios
                    if (fliaCodigo == 0) {
                        InventoryLogger.logWarning("JOIN_COMPLETO_PARAMS", "¡ADVERTENCIA! fliaCodigo es 0 - No se seleccionó familia")
                        InventoryLogger.logWarning("JOIN_COMPLETO_PARAMS", "Esto causará que la consulta no retorne resultados")
                    }
                    
                    if (areaCodigo == 0 || dptoCodigo == 0 || seccCodigo == 0) {
                        InventoryLogger.logWarning("JOIN_COMPLETO_PARAMS", "¡ADVERTENCIA! Parámetros de contexto incompletos:")
                        InventoryLogger.logWarning("JOIN_COMPLETO_PARAMS", "areaCodigo: $areaCodigo, dptoCodigo: $dptoCodigo, seccCodigo: $seccCodigo")
                    }
                    
                    InventoryLogger.logInfo("JOIN_COMPLETO", "=== INICIO CONSULTA JOIN COMPLETO ===")
                    InventoryLogger.logInfo("JOIN_COMPLETO", "Ejecutando consulta con JOIN completo")
                    InventoryLogger.logInfo("JOIN_COMPLETO_PARAMS", "areaCodigo: $areaCodigo")
                    InventoryLogger.logInfo("JOIN_COMPLETO_PARAMS", "dptoCodigo: $dptoCodigo") 
                    InventoryLogger.logInfo("JOIN_COMPLETO_PARAMS", "seccCodigo: $seccCodigo")
                    InventoryLogger.logInfo("JOIN_COMPLETO_PARAMS", "fliaCodigo: $fliaCodigo")
                    InventoryLogger.logInfo("JOIN_COMPLETO_PARAMS", "grupCodigos: $grupCodigos")
                    InventoryLogger.logInfo("JOIN_COMPLETO_PARAMS", "Grupos seleccionados: ${selectedGrupos.size}")
                    
                    // Log de cada grupo seleccionado con sus detalles
                    selectedGrupos.forEach { grupo ->
                        InventoryLogger.logInfo("JOIN_COMPLETO_GRUPO_DETALLE", "Grupo: ${grupo.grupCodigo} - ${grupo.grupDesc}")
                        InventoryLogger.logInfo("JOIN_COMPLETO_GRUPO_DETALLE", "  - Área: ${grupo.grupArea}")
                        InventoryLogger.logInfo("JOIN_COMPLETO_GRUPO_DETALLE", "  - Dpto: ${grupo.grupDpto}")
                        InventoryLogger.logInfo("JOIN_COMPLETO_GRUPO_DETALLE", "  - Sección: ${grupo.grupSeccion}")
                        InventoryLogger.logInfo("JOIN_COMPLETO_GRUPO_DETALLE", "  - Familia: ${grupo.grupFamilia}")
                    }
                    
                    // Log de la consulta SQL que se va a ejecutar
                    val sqlQuery = """
                        SELECT s.* FROM subgrupo s
                        INNER JOIN grupo g 
                        ON s.sugr_grupo = g.grupCodigo 
                        AND s.sugr_flia = g.grup_familia 
                        AND s.sugr_area = g.grup_area 
                        AND s.sugr_seccion = g.grup_seccion 
                        AND s.sugr_dpto = g.grup_dpto
                        INNER JOIN familia f 
                        ON s.sugr_flia = f.fliaCodigo 
                        AND s.sugr_area = f.flia_area 
                        AND s.sugr_dpto = f.flia_dpto 
                        AND s.sugr_seccion = f.flia_seccion
                        INNER JOIN seccion sec 
                        ON s.sugr_seccion = sec.seccCodigo 
                        AND s.sugr_area = sec.secc_area 
                        AND s.sugr_dpto = sec.secc_dpto
                        INNER JOIN departamento d 
                        ON s.sugr_dpto = d.dptoCodigo 
                        AND s.sugr_area = d.dpto_area
                        INNER JOIN area a 
                        ON s.sugr_area = a.areaCodigo
                        WHERE s.sugr_area = $areaCodigo 
                        AND s.sugr_seccion = $seccCodigo 
                        AND s.sugr_dpto = $dptoCodigo
                        AND s.sugr_flia = $fliaCodigo
                        AND s.sugr_grupo IN (${grupCodigos.joinToString(",")})
                        ORDER BY s.sugrCodigo
                    """.trimIndent()
                    
                    InventoryLogger.logInfo("JOIN_COMPLETO_SQL", "Consulta SQL generada:")
                    InventoryLogger.logInfo("JOIN_COMPLETO_SQL", sqlQuery)
                    
                    // Verificar datos antes de ejecutar la consulta
                    InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "=== VERIFICANDO DATOS EN TABLAS ===")
                    try {
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Paso 1: Obteniendo todos los subgrupos...")
                        
                        // Verificar tabla subgrupo
                        val todosSubgrupos = getSubgruposUseCase.getAllSubgrupos().first()
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Total subgrupos en BD: ${todosSubgrupos.size}")
                        
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Paso 2: Filtrando por área/dpto/sección...")
                        
                        // Filtrar por área, dpto, sección
                        val subgruposFiltrados = todosSubgrupos.filter { 
                            it.sugrArea == areaCodigo && 
                            it.sugrDpto == dptoCodigo && 
                            it.sugrSeccion == seccCodigo 
                        }
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Subgrupos que coinciden con área/dpto/sección: ${subgruposFiltrados.size}")
                        
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Paso 3: Filtrando por familia...")
                        
                        // Filtrar por familia
                        val subgruposDeFamilia = subgruposFiltrados.filter { subgrupo ->
                            subgrupo.sugrFlia == fliaCodigo
                        }
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Subgrupos que coinciden con familia: ${subgruposDeFamilia.size}")
                        
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Paso 4: Filtrando por grupos seleccionados...")
                        
                        // Filtrar por grupos seleccionados
                        val subgruposDeGrupos = subgruposDeFamilia.filter { subgrupo ->
                            grupCodigos.contains(subgrupo.sugrGrupo)
                        }
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Subgrupos de los grupos seleccionados: ${subgruposDeGrupos.size}")
                        
                        if (subgruposDeGrupos.isNotEmpty()) {
                            InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Ejemplos encontrados:")
                            subgruposDeGrupos.take(3).forEach { subgrupo ->
                                InventoryLogger.logSubgrupo(subgrupo, "VERIFICACION")
                            }
                            InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "✅ Verificación completada exitosamente")
                        } else {
                            InventoryLogger.logWarning("JOIN_COMPLETO_VERIFICACION", "¡NO SE ENCONTRARON SUBGRUPOS!")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VERIFICACION", "Posibles causas:")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VERIFICACION", "1. fliaCodigo = $fliaCodigo no existe en la BD")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VERIFICACION", "2. Los parámetros no coinciden con los datos")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VERIFICACION", "3. La consulta JOIN es demasiado restrictiva")
                        }
                    } catch (verificationError: Exception) {
                        InventoryLogger.logError("JOIN_COMPLETO_VERIFICACION", "❌ ERROR CRÍTICO en verificación de datos", verificationError)
                        InventoryLogger.logError("JOIN_COMPLETO_VERIFICACION", "Mensaje: ${verificationError.message}")
                        InventoryLogger.logError("JOIN_COMPLETO_VERIFICACION", "Causa: ${verificationError.cause?.message ?: "Desconocida"}")
                        InventoryLogger.logError("JOIN_COMPLETO_VERIFICACION", "Stack trace: ${verificationError.stackTrace.joinToString("\n")}")
                        
                        // Continuar con la ejecución aunque falle la verificación
                        InventoryLogger.logInfo("JOIN_COMPLETO_VERIFICACION", "Continuando con la ejecución principal...")
                    }
                    
                    try {
                        // PRIMERO: Probar consulta simple sin JOINs para verificar que los datos básicos funcionan
                        InventoryLogger.logInfo("TEST_QUERY_SIMPLE", "=== PROBANDO CONSULTA SIMPLE ===")
                        
                        val subgruposSimple = getSubgruposUseCase.testQuerySimple(
                            areaCodigo = areaCodigo,
                            dptoCodigo = dptoCodigo,
                            seccCodigo = seccCodigo,
                            fliaCodigo = fliaCodigo,
                            grupCodigos = selectedGrupos.map { it.grupCodigo }
                        )
                        
                        InventoryLogger.logInfo("TEST_QUERY_SIMPLE", "Consulta simple retornó: ${subgruposSimple.size} subgrupos")
                        
                        if (subgruposSimple.isNotEmpty()) {
                            InventoryLogger.logInfo("TEST_QUERY_SIMPLE", "Primeros 3 resultados de consulta simple:")
                            subgruposSimple.take(3).forEach { subgrupo ->
                                InventoryLogger.logDebug("TEST_QUERY_SIMPLE", "Subgrupo: ${subgrupo.sugrCodigo} - ${subgrupo.sugrDesc}")
                            }
                        }
                        
                        // SEGUNDO: Intentar usar la consulta con JOIN completo para múltiples grupos
                        InventoryLogger.logInfo("JOIN_COMPLETO", "=== PROBANDO CONSULTA JOIN COMPLETO ===")
                        
                        val subgruposWithGrupo = getSubgruposUseCase.getSubgruposByMultipleGruposWithCompleteJoin(
                            gruposCodigos = selectedGrupos.map { it.grupCodigo },
                            areaCodigo = areaCodigo,
                            dptoCodigo = dptoCodigo,
                            seccCodigo = seccCodigo,
                            fliaCodigo = fliaCodigo
                        )
                        
                        // Convertir SubgrupoWithGrupo a Subgrupo para mantener compatibilidad
                        val subgrupos = subgruposWithGrupo.map { subgrupoWithGrupo ->
                            Subgrupo(
                                sugrCodigo = subgrupoWithGrupo.sugrCodigo,
                                sugrDesc = subgrupoWithGrupo.sugrDesc,
                                sugrArea = subgrupoWithGrupo.sugrArea,
                                sugrDpto = subgrupoWithGrupo.sugrDpto,
                                sugrSeccion = subgrupoWithGrupo.sugrSeccion,
                                sugrFlia = subgrupoWithGrupo.sugrFlia,
                                sugrGrupo = subgrupoWithGrupo.sugrGrupo,
                                syncTimestamp = subgrupoWithGrupo.syncTimestamp
                            )
                        }
                        
                        // Log de subgrupos con información del grupo
                        InventoryLogger.logInfo("JOIN_COMPLETO_RESULTADO", "Subgrupos con información del grupo:")
                        subgruposWithGrupo.take(5).forEach { subgrupoWithGrupo ->
                            InventoryLogger.logDebug("JOIN_COMPLETO_RESULTADO", "Subgrupo: ${subgrupoWithGrupo.sugrCodigo} - ${subgrupoWithGrupo.sugrDesc} (Grupo: ${subgrupoWithGrupo.sugrGrupo} - ${subgrupoWithGrupo.grupoDescripcion})")
                        }
                        
                        InventoryLogger.logInfo("JOIN_COMPLETO_RESULTADO", "=== RESULTADO CONSULTA JOIN COMPLETO ===")
                        InventoryLogger.logInfo("JOIN_COMPLETO_RESULTADO", "Consulta con JOIN completo retornó: ${subgrupos.size} subgrupos")
                        
                        if (subgrupos.isEmpty()) {
                            InventoryLogger.logWarning("JOIN_COMPLETO_VACIO", "¡ADVERTENCIA! La consulta con JOIN completo no retornó resultados")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VACIO", "Posibles causas:")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VACIO", "1. No hay datos en las tablas relacionadas")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VACIO", "2. Los parámetros no coinciden con los datos")
                            InventoryLogger.logWarning("JOIN_COMPLETO_VACIO", "3. Los JOINs están eliminando todos los registros")
                        } else {
                            InventoryLogger.logInfo("JOIN_COMPLETO_RESULTADO", "Primeros 5 subgrupos encontrados:")
                            subgrupos.take(5).forEach { subgrupo ->
                                InventoryLogger.logSubgrupo(subgrupo, "JOIN_COMPLETO")
                            }
                        }
                        
                        // Actualizar el estado directamente
                        _uiState.value = _uiState.value.copy(subgrupos = subgrupos)
                        
                        InventoryLogger.logInfo("JOIN_COMPLETO_UI_UPDATE", "Estado del UI actualizado con ${subgrupos.size} subgrupos")
                        InventoryLogger.logInfo("JOIN_COMPLETO_UI_UPDATE", "Estado actual: ${_uiState.value.subgrupos.size} subgrupos en el estado")
                        InventoryLogger.logInfo("JOIN_COMPLETO_UI_UPDATE", "showSubgrupoDialog: ${_uiState.value.showSubgrupoDialog}")
                        InventoryLogger.logInfo("JOIN_COMPLETO_UI_UPDATE", "selectedSubgrupos: ${_uiState.value.selectedSubgrupos.size}")
                        InventoryLogger.logInfo("JOIN_COMPLETO_UI_UPDATE", "isSubgruposTodos: ${_uiState.value.isSubgruposTodos}")
                        
                        // Verificar que los subgrupos realmente se asignaron
                        val estadoActual = _uiState.value
                        if (estadoActual.subgrupos.isEmpty() && subgrupos.isNotEmpty()) {
                            InventoryLogger.logError("JOIN_COMPLETO_UI_UPDATE", "¡ERROR CRÍTICO! Los subgrupos no se asignaron al estado del UI")
                            InventoryLogger.logError("JOIN_COMPLETO_UI_UPDATE", "subgrupos obtenidos: ${subgrupos.size}, estado.subgrupos: ${estadoActual.subgrupos.size}")
                        } else if (estadoActual.subgrupos.isNotEmpty()) {
                            InventoryLogger.logInfo("JOIN_COMPLETO_UI_UPDATE", "✅ Subgrupos correctamente asignados al estado del UI")
                        }
                        
                        // Log de subgrupos con información del grupo para debugging
                        InventoryLogger.logInfo("JOIN_COMPLETO_UI_UPDATE", "Detalle de subgrupos cargados:")
                        subgrupos.take(5).forEach { subgrupo ->
                            InventoryLogger.logDebug("JOIN_COMPLETO_UI_UPDATE", "Subgrupo: ${subgrupo.sugrCodigo} - ${subgrupo.sugrDesc} (Grupo: ${subgrupo.sugrGrupo})")
                        }
                        
                    } catch (e: Exception) {
                        InventoryLogger.logError("ERROR_JOIN_COMPLETO", "=== ERROR EN CONSULTA JOIN COMPLETO ===", e)
                        InventoryLogger.logError("ERROR_JOIN_COMPLETO", "Error en consulta con JOIN completo, usando fallback")
                        InventoryLogger.logError("ERROR_JOIN_COMPLETO", "Error details: ${e.message}")
                        InventoryLogger.logError("ERROR_JOIN_COMPLETO", "Causa probable: ${e.cause?.message ?: "Desconocida"}")
                        
                        // Fallback: usar consultas individuales
                        val allSubgrupos = mutableListOf<Subgrupo>()
                        
                        selectedGrupos.forEach { grupo ->
                            try {
                                val subgrupos = getSubgruposUseCase.getSubgruposByGrupoWithJoin(
                                    grupoCodigo = grupo.grupCodigo,
                                    areaCodigo = areaCodigo,
                                    dptoCodigo = dptoCodigo,
                                    seccCodigo = seccCodigo,
                                    fliaCodigo = fliaCodigo
                                ).first()
                                
                                InventoryLogger.logInfo("FALLBACK_INDIVIDUAL", "Grupo ${grupo.grupCodigo}: ${subgrupos.size} subgrupos")
                                allSubgrupos.addAll(subgrupos)
                                
                            } catch (e2: Exception) {
                                InventoryLogger.logError("ERROR_FALLBACK", "Error en fallback para grupo ${grupo.grupCodigo}", e2)
                            }
                        }
                        
                        val subgruposFinales = allSubgrupos.distinctBy { it.sugrCodigo }
                        _uiState.value = _uiState.value.copy(subgrupos = subgruposFinales)
                    }
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar subgrupos: ${e.message}",
                    showGrupoDialog = false
                )
            }
        }
    }
    
    fun onSubgrupoToggled(subgrupo: Subgrupo) {
        val currentSelected = _uiState.value.selectedSubgrupos.toMutableList()
        
        if (currentSelected.any { it.sugrCodigo == subgrupo.sugrCodigo }) {
            // Remover si ya está seleccionado
            currentSelected.removeAll { it.sugrCodigo == subgrupo.sugrCodigo }
            InventoryLogger.logInfo("SUBGRUPO_TOGGLED", "Subgrupo removido: ${subgrupo.sugrCodigo} - ${subgrupo.sugrDesc}")
        } else {
            // Agregar si no está seleccionado
            currentSelected.add(subgrupo)
            InventoryLogger.logInfo("SUBGRUPO_TOGGLED", "Subgrupo agregado: ${subgrupo.sugrCodigo} - ${subgrupo.sugrDesc}")
        }
        
        _uiState.value = _uiState.value.copy(
            selectedSubgrupos = currentSelected,
            isSubgruposTodos = false
        )
        
        InventoryLogger.logInfo("SUBGRUPO_TOGGLED", "Total subgrupos seleccionados: ${currentSelected.size}")
    }
    
    fun onSubgruposTodosToggled() {
        val currentState = _uiState.value.isSubgruposTodos
        val newState = !currentState
        
        if (newState) {
            // Seleccionar todos los subgrupos disponibles
            _uiState.value = _uiState.value.copy(
                selectedSubgrupos = _uiState.value.subgrupos,
                isSubgruposTodos = true
            )
            InventoryLogger.logInfo("SUBGRUPOS_TODOS_TOGGLED", "Todos los subgrupos seleccionados: ${_uiState.value.subgrupos.size}")
        } else {
            // Deseleccionar todos
            _uiState.value = _uiState.value.copy(
                selectedSubgrupos = emptyList(),
                isSubgruposTodos = false
            )
            InventoryLogger.logInfo("SUBGRUPOS_TODOS_TOGGLED", "Todos los subgrupos deseleccionados")
        }
    }
    
    // Función helper para obtener la descripción del grupo de un subgrupo
    fun getGrupoDescripcionForSubgrupo(subgrupo: Subgrupo): String {
        return try {
            val grupo = _uiState.value.grupos.find { it.grupCodigo == subgrupo.sugrGrupo }
            grupo?.grupDesc ?: "Grupo ${subgrupo.sugrGrupo}"
        } catch (e: Exception) {
            "Grupo ${subgrupo.sugrGrupo}"
        }
    }
    
    fun confirmSubgrupoSelection() {
        InventoryLogger.logInfo("CONFIRM_SUBGRUPO_SELECTION", "Confirmando selección de subgrupos")
        InventoryLogger.logInfo("CONFIRM_SUBGRUPO_SELECTION", "Subgrupos seleccionados: ${_uiState.value.selectedSubgrupos.size}")
        
        _uiState.value = _uiState.value.copy(showSubgrupoDialog = false)
        
        // Log de todos los subgrupos seleccionados
        _uiState.value.selectedSubgrupos.forEach { subgrupo ->
            InventoryLogger.logSubgrupo(subgrupo, "CONFIRMED_SELECTION")
        }
        
        // ✅ Ejecutar búsqueda automáticamente
        InventoryLogger.logInfo("AUTO_SEARCH_SUBGRUPOS", "Ejecutando búsqueda automática para subgrupos seleccionados")
        loadArticulosLotes()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Función de debug para ver el estado completo
    fun debugCurrentState() {
        val currentState = _uiState.value
        InventoryLogger.logInfo("DEBUG_STATE", "=== ESTADO ACTUAL DEL VIEWMODEL ===")
        InventoryLogger.logInfo("DEBUG_STATE", "showSubgrupoDialog: ${currentState.showSubgrupoDialog}")
        InventoryLogger.logInfo("DEBUG_STATE", "subgrupos en estado: ${currentState.subgrupos.size}")
        InventoryLogger.logInfo("DEBUG_STATE", "selectedSubgrupos: ${currentState.selectedSubgrupos.size}")
        if (currentState.selectedSubgrupos.isNotEmpty()) {
            currentState.selectedSubgrupos.forEach { subgrupo ->
                InventoryLogger.logSubgrupo(subgrupo, "DEBUG_STATE")
            }
        }
        InventoryLogger.logInfo("DEBUG_STATE", "selectedGrupos: ${currentState.selectedGrupos.size}")
        currentState.selectedGrupos.forEach { grupo ->
            InventoryLogger.logGrupo(grupo, "DEBUG_STATE")
        }
        InventoryLogger.logInfo("DEBUG_STATE", "=== FIN DEL ESTADO ===")
    }
    
    // Métodos para manejar diálogos - Navegación libre
    fun showSucursalDialog() {
        _uiState.value = _uiState.value.copy(showSucursalDialog = true)
    }
    
    fun hideSucursalDialog() {
        _uiState.value = _uiState.value.copy(showSucursalDialog = false)
    }
    
    fun showDepartamentoDialog() {
        _uiState.value = _uiState.value.copy(showDepartamentoDialog = true)
    }
    
    fun hideDepartamentoDialog() {
        _uiState.value = _uiState.value.copy(showDepartamentoDialog = false)
    }
    
    fun showAreaDialog() {
        _uiState.value = _uiState.value.copy(showAreaDialog = true)
    }
    
    fun hideAreaDialog() {
        _uiState.value = _uiState.value.copy(showAreaDialog = false)
    }
    
    fun showDptoDialog() {
        _uiState.value = _uiState.value.copy(showDptoDialog = true)
    }
    
    fun hideDptoDialog() {
        _uiState.value = _uiState.value.copy(showDptoDialog = false)
    }
    
    fun showSeccionDialog() {
        _uiState.value = _uiState.value.copy(showSeccionDialog = true)
    }
    
    fun hideSeccionDialog() {
        _uiState.value = _uiState.value.copy(showSeccionDialog = false)
    }
    
    fun showFamiliaDialog() {
        _uiState.value = _uiState.value.copy(showFamiliaDialog = true)
    }
    
    fun hideFamiliaDialog() {
        _uiState.value = _uiState.value.copy(showFamiliaDialog = false)
    }
    
    fun showGrupoDialog() {
        _uiState.value = _uiState.value.copy(showGrupoDialog = true)
    }
    
    fun hideGrupoDialog() {
        _uiState.value = _uiState.value.copy(showGrupoDialog = false)
    }
    
    fun showSubgrupoDialog() {
        InventoryLogger.logInfo("SHOW_SUBGRUPO_DIALOG", "Abriendo diálogo de subgrupos")
        
        // Log del estado actual antes de cargar subgrupos
        val currentState = _uiState.value
        InventoryLogger.logTomaManualState(
            currentState.selectedSucursal,
            currentState.selectedDepartamento,
            currentState.selectedArea,
            currentState.selectedDpto,
            currentState.selectedSeccion,
            currentState.selectedFamilia,
            currentState.selectedGrupos,
            currentState.selectedSubgrupos
        )
        
        // Verificar estado de las tablas en la base de datos
        checkDatabaseState()
        
        // Cargar subgrupos si hay grupos seleccionados
        if (currentState.selectedGrupos.isNotEmpty()) {
            loadSubgruposForSelectedGrupos()
        } else {
            InventoryLogger.logWarning("SHOW_SUBGRUPO_DIALOG", "No hay grupos seleccionados")
        }
        
        _uiState.value = _uiState.value.copy(showSubgrupoDialog = true)
        
        // Log del estado final del diálogo
        InventoryLogger.logInfo("DIALOG_ESTADO_FINAL", "showSubgrupoDialog = ${_uiState.value.showSubgrupoDialog}")
        InventoryLogger.logInfo("DIALOG_ESTADO_FINAL", "subgrupos en estado: ${_uiState.value.subgrupos.size}")
        InventoryLogger.logInfo("DIALOG_ESTADO_FINAL", "showSubgrupoDialog en estado: ${_uiState.value.showSubgrupoDialog}")
    }
    
    private fun checkDatabaseState() {
        viewModelScope.launch {
            try {
                InventoryLogger.logInfo("DB_CHECK", "Verificando estado de las tablas...")
                
                // Verificar tabla subgrupo
                val totalSubgrupos = getSubgruposUseCase.getSubgruposCount()
                InventoryLogger.logDatabaseCount("subgrupo", totalSubgrupos)
                
                // Verificar tabla grupo
                getGruposUseCase().collect { grupos ->
                    InventoryLogger.logDatabaseCount("grupo", grupos.size)
                    if (grupos.size > 0) {
                        InventoryLogger.logInfo("DB_CHECK_GRUPO", "Primeros 3 grupos:")
                        grupos.take(3).forEach { grupo ->
                            InventoryLogger.logGrupo(grupo, "DB_CHECK")
                        }
                    }
                }
                
                // Verificar si hay subgrupos para los grupos específicos
                val selectedGrupos = _uiState.value.selectedGrupos
                selectedGrupos.forEach { grupo ->
                    getSubgruposUseCase().collect { todosSubgrupos ->
                        val subgruposDelGrupo = todosSubgrupos.filter { it.sugrGrupo == grupo.grupCodigo }
                        InventoryLogger.logInfo("DB_CHECK_SUBGRUPOS", "Subgrupos para grupo ${grupo.grupCodigo}: ${subgruposDelGrupo.size}")
                        
                        val subgruposConContexto = todosSubgrupos.filter { 
                            it.sugrGrupo == grupo.grupCodigo &&
                            it.sugrArea == grupo.grupArea &&
                            it.sugrDpto == grupo.grupDpto &&
                            it.sugrSeccion == grupo.grupSeccion &&
                            it.sugrFlia == grupo.grupFamilia
                        }
                        InventoryLogger.logInfo("DB_CHECK_SUBGRUPOS_CONTEXTO", "Subgrupos con contexto completo para grupo ${grupo.grupCodigo}: ${subgruposConContexto.size}")
                        
                        if (subgruposConContexto.isNotEmpty()) {
                            InventoryLogger.logInfo("DB_CHECK_SUBGRUPOS_EJEMPLOS", "Ejemplos de subgrupos encontrados:")
                            subgruposConContexto.take(3).forEach { subgrupo ->
                                InventoryLogger.logSubgrupo(subgrupo, "DB_CHECK")
                            }
                        }
                    }
                }
                
            } catch (e: Exception) {
                InventoryLogger.logError("ERROR_DB_CHECK", "Error al verificar estado de la base de datos", e)
            }
        }
    }
    
    private fun loadSubgruposIndividualFallback(selectedGrupos: List<Grupo>) {
        InventoryLogger.logInfo("FALLBACK_INDIVIDUAL", "Ejecutando fallback individual para ${selectedGrupos.size} grupos")
        
        viewModelScope.launch {
            try {
                val allSubgrupos = mutableListOf<Subgrupo>()
                
                selectedGrupos.forEach { grupo ->
                    InventoryLogger.logInfo("PROCESANDO_GRUPO_FALLBACK", "Procesando grupo: ${grupo.grupCodigo} - ${grupo.grupDesc}")
                    
                    try {
                        getSubgruposUseCase.getSubgruposByGrupoWithJoin(
                            grupoCodigo = grupo.grupCodigo,
                            areaCodigo = grupo.grupArea,
                            dptoCodigo = grupo.grupDpto,
                            seccCodigo = grupo.grupSeccion,
                            fliaCodigo = grupo.grupFamilia
                        ).collect { subgrupos ->
                            InventoryLogger.logInfo("SUBGRUPOS_ENCONTRADOS_FALLBACK", "Para grupo ${grupo.grupCodigo}: ${subgrupos.size} subgrupos")
                            subgrupos.forEach { subgrupo ->
                                InventoryLogger.logSubgrupo(subgrupo, "FALLBACK_${grupo.grupCodigo}")
                            }
                            allSubgrupos.addAll(subgrupos)
                        }
                    } catch (e: Exception) {
                        InventoryLogger.logError("ERROR_SUBGRUPOS_FALLBACK", "Error al cargar subgrupos para grupo ${grupo.grupCodigo}", e)
                    }
                }
                
                val subgruposFinales = allSubgrupos.distinctBy { it.sugrCodigo }
                InventoryLogger.logInfo("RESULTADO_FINAL_FALLBACK", "Total de subgrupos únicos: ${subgruposFinales.size}")
                _uiState.value = _uiState.value.copy(subgrupos = subgruposFinales)
                
            } catch (e: Exception) {
                InventoryLogger.logError("ERROR_FALLBACK", "Error en fallback individual", e)
            }
        }
    }
    
    private fun loadSubgruposForSelectedGrupos() {
        val selectedGrupos = _uiState.value.selectedGrupos
        InventoryLogger.logInfo("LOAD_SUBGRUPOS", "Cargando subgrupos para ${selectedGrupos.size} grupos")
        
        viewModelScope.launch {
            try {
                val allSubgrupos = mutableListOf<Subgrupo>()
                
                // Obtener el contexto común de los grupos seleccionados
                val firstGrupo = selectedGrupos.first()
                val areaCodigo = firstGrupo.grupArea
                val dptoCodigo = firstGrupo.grupDpto
                val seccCodigo = firstGrupo.grupSeccion
                
                InventoryLogger.logInfo("CONTEXTO_COMUN", "Área: $areaCodigo, Dpto: $dptoCodigo, Sección: $seccCodigo")
                
                // Verificar que todos los grupos tengan el mismo contexto
                val sameContext = selectedGrupos.all { grupo ->
                    grupo.grupArea == areaCodigo && 
                    grupo.grupDpto == dptoCodigo && 
                    grupo.grupSeccion == seccCodigo
                }
                
                if (sameContext) {
                    InventoryLogger.logInfo("CONTEXTO_VALIDO", "Todos los grupos tienen el mismo contexto")
                    
                    // Usar la consulta optimizada con JOIN para múltiples grupos
                    val grupCodigos = selectedGrupos.map { it.grupCodigo }
                    InventoryLogger.logInfo("GRUPOS_CODIGOS", "Códigos de grupos: $grupCodigos")
                    
                                            try {
                            // Log de la consulta SQL que se va a ejecutar
                            val sqlQuery = """
                                SELECT s.* FROM subgrupo s
                                INNER JOIN grupo g 
                                ON s.sugr_grupo = g.grupCodigo 
                                AND s.sugr_flia = g.grup_familia 
                                AND s.sugr_area = g.grup_area 
                                AND s.sugr_seccion = g.grup_seccion 
                                AND s.sugr_dpto = g.grup_dpto
                                WHERE s.sugr_area = $areaCodigo 
                                AND s.sugr_seccion = $seccCodigo 
                                AND s.sugr_dpto = $dptoCodigo
                                AND s.sugr_grupo IN (${grupCodigos.joinToString(",")})
                                ORDER BY s.sugrCodigo
                            """.trimIndent()
                            
                            InventoryLogger.logInfo("SQL_QUERY_MULTIPLE", "Ejecutando consulta SQL:")
                            InventoryLogger.logInfo("SQL_QUERY_MULTIPLE", sqlQuery)
                            InventoryLogger.logInfo("SQL_PARAMS_MULTIPLE", "Parámetros: grupCodigos=$grupCodigos, areaCodigo=$areaCodigo, dptoCodigo=$dptoCodigo, seccCodigo=$seccCodigo")
                            
                            getSubgruposUseCase.getSubgruposByMultipleGruposWithJoin(
                                gruposCodigos = grupCodigos,
                                areaCodigo = areaCodigo,
                                dptoCodigo = dptoCodigo,
                                seccCodigo = seccCodigo,
                                fliaCodigo = firstGrupo.grupFamilia
                            ).collect { subgrupos ->
                                InventoryLogger.logInfo("SUBGRUPOS_ENCONTRADOS_JOIN", "Total subgrupos encontrados: ${subgrupos.size}")
                                if (subgrupos.isEmpty()) {
                                    InventoryLogger.logWarning("SQL_RESULT_EMPTY", "La consulta SQL retornó 0 resultados")
                                    InventoryLogger.logWarning("SQL_RESULT_EMPTY", "Consulta ejecutada: $sqlQuery")
                                } else {
                                    subgrupos.forEach { subgrupo ->
                                        InventoryLogger.logSubgrupo(subgrupo, "JOIN_QUERY")
                                    }
                                }
                                allSubgrupos.addAll(subgrupos)
                            }
                    } catch (e: Exception) {
                        InventoryLogger.logError("ERROR_SUBGRUPOS_JOIN", "Error en consulta JOIN", e)
                        
                        // Fallback: usar consulta individual por grupo
                        InventoryLogger.logInfo("FALLBACK_INDIVIDUAL", "Usando consulta individual por grupo")
                        selectedGrupos.forEach { grupo ->
                            InventoryLogger.logInfo("PROCESANDO_GRUPO_INDIVIDUAL", "Procesando grupo: ${grupo.grupCodigo} - ${grupo.grupDesc}")
                            
                            try {
                                // Log de la consulta SQL individual
                                val sqlQueryIndividual = """
                                    SELECT s.* FROM subgrupo s
                                    INNER JOIN grupo g 
                                    ON s.sugr_grupo = g.grupCodigo 
                                    AND s.sugr_flia = g.grup_familia 
                                    AND s.sugr_area = g.grup_area 
                                    AND s.sugr_seccion = g.grup_seccion 
                                    AND s.sugr_dpto = g.grup_dpto
                                    WHERE s.sugr_area = ${grupo.grupArea} 
                                    AND s.sugr_seccion = ${grupo.grupSeccion} 
                                    AND s.sugr_dpto = ${grupo.grupDpto}
                                    AND s.sugr_grupo = ${grupo.grupCodigo}
                                    ORDER BY s.sugrCodigo
                                """.trimIndent()
                                
                                InventoryLogger.logInfo("SQL_QUERY_INDIVIDUAL", "Ejecutando consulta SQL individual para grupo ${grupo.grupCodigo}:")
                                InventoryLogger.logInfo("SQL_QUERY_INDIVIDUAL", sqlQueryIndividual)
                                InventoryLogger.logInfo("SQL_PARAMS_INDIVIDUAL", "Parámetros: grupCodigo=${grupo.grupCodigo}, areaCodigo=${grupo.grupArea}, dptoCodigo=${grupo.grupDpto}, seccCodigo=${grupo.grupSeccion}")
                                
                                getSubgruposUseCase.getSubgruposByGrupoWithJoin(
                                    grupoCodigo = grupo.grupCodigo,
                                    areaCodigo = grupo.grupArea,
                                    dptoCodigo = grupo.grupDpto,
                                    seccCodigo = grupo.grupSeccion,
                                    fliaCodigo = grupo.grupFamilia
                                ).collect { subgrupos ->
                                    InventoryLogger.logInfo("SUBGRUPOS_ENCONTRADOS_INDIVIDUAL", "Para grupo ${grupo.grupCodigo}: ${subgrupos.size} subgrupos")
                                    if (subgrupos.isEmpty()) {
                                        InventoryLogger.logWarning("SQL_RESULT_EMPTY_INDIVIDUAL", "La consulta SQL individual retornó 0 resultados para grupo ${grupo.grupCodigo}")
                                        InventoryLogger.logWarning("SQL_RESULT_EMPTY_INDIVIDUAL", "Consulta ejecutada: $sqlQueryIndividual")
                                    } else {
                                        subgrupos.forEach { subgrupo ->
                                            InventoryLogger.logSubgrupo(subgrupo, "JOIN_INDIVIDUAL_${grupo.grupCodigo}")
                                        }
                                    }
                                    allSubgrupos.addAll(subgrupos)
                                }
                            } catch (e2: Exception) {
                                InventoryLogger.logError("ERROR_SUBGRUPOS_INDIVIDUAL", "Error al cargar subgrupos para grupo ${grupo.grupCodigo}", e2)
                            }
                        }
                    }
                } else {
                    InventoryLogger.logWarning("CONTEXTO_DIFERENTE", "Los grupos tienen contextos diferentes, usando consulta individual")
                    
                    // Los grupos tienen contextos diferentes, usar consulta individual
                    selectedGrupos.forEach { grupo ->
                        InventoryLogger.logInfo("PROCESANDO_GRUPO_INDIVIDUAL", "Procesando grupo: ${grupo.grupCodigo} - ${grupo.grupDesc}")
                        
                        try {
                            getSubgruposUseCase.getSubgruposByGrupoWithJoin(
                                grupoCodigo = grupo.grupCodigo,
                                areaCodigo = grupo.grupArea,
                                dptoCodigo = grupo.grupDpto,
                                seccCodigo = grupo.grupSeccion,
                                fliaCodigo = grupo.grupFamilia
                            ).collect { subgrupos ->
                                InventoryLogger.logInfo("SUBGRUPOS_ENCONTRADOS_INDIVIDUAL", "Para grupo ${grupo.grupCodigo}: ${subgrupos.size} subgrupos")
                                subgrupos.forEach { subgrupo ->
                                    InventoryLogger.logSubgrupo(subgrupo, "JOIN_INDIVIDUAL_${grupo.grupCodigo}")
                                }
                                allSubgrupos.addAll(subgrupos)
                            }
                        } catch (e: Exception) {
                            InventoryLogger.logError("ERROR_SUBGRUPOS_INDIVIDUAL", "Error al cargar subgrupos para grupo ${grupo.grupCodigo}", e)
                        }
                    }
                }
                
                // Log del resultado final
                val subgruposFinales = allSubgrupos.distinctBy { it.sugrCodigo }
                InventoryLogger.logInfo("RESULTADO_FINAL", "Total de subgrupos únicos: ${subgruposFinales.size}")
                subgruposFinales.forEach { subgrupo ->
                    InventoryLogger.logSubgrupo(subgrupo, "FINAL")
                }
                
                _uiState.value = _uiState.value.copy(subgrupos = subgruposFinales)
                
            } catch (e: Exception) {
                InventoryLogger.logError("ERROR_LOAD_SUBGRUPOS", "Error al cargar subgrupos", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar subgrupos: ${e.message}"
                )
            }
        }
    }
    
    fun hideSubgrupoDialog() {
        InventoryLogger.logInfo("HIDE_SUBGRUPO_DIALOG", "Cerrando diálogo de subgrupos")
        InventoryLogger.logInfo("HIDE_SUBGRUPO_DIALOG", "Estado antes de cerrar: showSubgrupoDialog = ${_uiState.value.showSubgrupoDialog}")
        
        _uiState.value = _uiState.value.copy(showSubgrupoDialog = false)
        
        InventoryLogger.logInfo("HIDE_SUBGRUPO_DIALOG", "Estado después de cerrar: showSubgrupoDialog = ${_uiState.value.showSubgrupoDialog}")
    }
    
    // Método para limpiar selecciones desde un punto específico
    fun clearFromPoint(point: String) {
        when (point) {
            "sucursal" -> {
                _uiState.value = _uiState.value.copy(
                    selectedSucursal = null,
                    selectedDepartamento = null,
                    selectedArea = null,
                    selectedDpto = null,
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    departamentos = emptyList(),
                    dptos = emptyList(),
                    secciones = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    isFamiliaTodos = false,
                    isGruposTodos = false,
                    isSubgruposTodos = false
                )
            }
            "departamento" -> {
                _uiState.value = _uiState.value.copy(
                    selectedDepartamento = null,
                    selectedArea = null,
                    selectedDpto = null,
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    dptos = emptyList(),
                    secciones = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    isFamiliaTodos = false,
                    isGruposTodos = false,
                    isSubgruposTodos = false
                )
            }
            "area" -> {
                _uiState.value = _uiState.value.copy(
                    selectedArea = null,
                    selectedDpto = null,
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    dptos = emptyList(),
                    secciones = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    isFamiliaTodos = false,
                    isGruposTodos = false,
                    isSubgruposTodos = false
                )
            }
            "dpto" -> {
                _uiState.value = _uiState.value.copy(
                    selectedDpto = null,
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    secciones = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    isFamiliaTodos = false,
                    isGruposTodos = false,
                    isSubgruposTodos = false
                )
            }
            "seccion" -> {
                _uiState.value = _uiState.value.copy(
                    selectedSeccion = null,
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    familias = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    isFamiliaTodos = false,
                    isGruposTodos = false,
                    isSubgruposTodos = false
                )
            }
            "familia" -> {
                _uiState.value = _uiState.value.copy(
                    selectedFamilia = null,
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    grupos = emptyList(),
                    subgrupos = emptyList(),
                    isFamiliaTodos = false,
                    isGruposTodos = false,
                    isSubgruposTodos = false
                )
            }
            "grupo" -> {
                _uiState.value = _uiState.value.copy(
                    selectedGrupos = emptyList(),
                    selectedSubgrupos = emptyList(),
                    subgrupos = emptyList(),
                    isGruposTodos = false,
                    isSubgruposTodos = false
                )
            }
        }
    }
    
    // Función para mostrar el diálogo de artículos con lotes
    fun showArticulosLotesDialog() {
        _uiState.value = _uiState.value.copy(showArticulosLotesDialog = true)
    }
    
    // Función para ocultar el diálogo de artículos con lotes
    fun hideArticulosLotesDialog() {
        _uiState.value = _uiState.value.copy(showArticulosLotesDialog = false)
    }
    
    // Función para mostrar el diálogo de confirmación de toma
    fun showConfirmarTomaDialog() {
        // Marcar todos los artículos seleccionados como inventario no visible por defecto
        val articulosActualizados = _uiState.value.selectedArticulosLotes.map { articulo ->
            articulo.copy(inventarioVisible = "N")
        }
        
        _uiState.value = _uiState.value.copy(
            selectedArticulosLotes = articulosActualizados,
            showConfirmarTomaDialog = true
        )
    }
    
    // Función para ocultar el diálogo de confirmación de toma
    fun hideConfirmarTomaDialog() {
        _uiState.value = _uiState.value.copy(showConfirmarTomaDialog = false)
    }
    
    // Función para actualizar la marca de inventario visible en los artículos seleccionados
    fun updateInventarioVisibleMark(inventarioVisible: Boolean) {
        android.util.Log.d("TomaManualViewModel", "🔄 Actualizando inventario visible: $inventarioVisible")
        
        val articulosActualizados = _uiState.value.selectedArticulosLotes.map { articulo ->
            articulo.copy(
                inventarioVisible = if (inventarioVisible) "Y" else "N"
            )
        }
        
        _uiState.value = _uiState.value.copy(
            selectedArticulosLotes = articulosActualizados,
            inventarioVisible = inventarioVisible
        )
        
        android.util.Log.d("TomaManualViewModel", "✅ Inventario visible actualizado: ${_uiState.value.inventarioVisible}")
        android.util.Log.d("TomaManualViewModel", "📊 Artículos seleccionados: ${_uiState.value.selectedArticulosLotes.size}")
    }
    
    // Función para establecer el tipo de toma (Manual o Criterio)
    fun setTipoToma(tipoToma: String) {
        android.util.Log.d("TomaManualViewModel", "🔄 Estableciendo tipo de toma: $tipoToma")
        
        _uiState.value = _uiState.value.copy(
            tipoToma = tipoToma
        )
        
        android.util.Log.d("TomaManualViewModel", "✅ Tipo de toma establecido: ${_uiState.value.tipoToma}")
    }
    
    // Función para limpiar el mensaje de éxito
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
    
    // Función para limpiar todos los parámetros después del registro exitoso
    fun clearAllParameters() {
        android.util.Log.d("TomaManualViewModel", "🧹 Limpiando todos los parámetros del ViewModel...")
        
        _uiState.value = TomaManualUiState()
        
        android.util.Log.d("TomaManualViewModel", "✅ Todos los parámetros limpiados exitosamente")
    }
    
    // Función para cargar artículos con lotes desde Oracle
    fun loadArticulosLotes() {
        android.util.Log.d("TomaManualViewModel", "🚀 FUNCIÓN loadArticulosLotes() EJECUTADA")
        android.util.Log.d("TomaManualViewModel", "📱 Usuario hizo clic en el botón 'Buscar productos'")
        
        // Limpiar selección anterior y cerrar diálogo si está abierto
        _uiState.value = _uiState.value.copy(
            selectedArticulosLotes = emptyList(),
            showArticulosLotesDialog = false
        )
        
        // Resetear progreso anterior
        resetProgress()
        
        // Activar loading inmediatamente para feedback visual instantáneo
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            loadingMessage = "Iniciando consulta Oracle...",
            errorMessage = null
        )
        android.util.Log.d("TomaManualViewModel", "✅ Estado de loading actualizado: ${_uiState.value.isLoading}")
        android.util.Log.d("TomaManualViewModel", "📝 Mensaje de loading: ${_uiState.value.loadingMessage}")
        
        viewModelScope.launch {
            android.util.Log.d("TomaManualViewModel", "🔄 Iniciando corrutina en viewModelScope...")
            try {
                android.util.Log.d("TomaManualViewModel", "🔍 Verificando campos requeridos...")
                
                // Verificar que todos los campos requeridos estén seleccionados
                val sucursal = _uiState.value.selectedSucursal?.sucCodigo
                val deposito = _uiState.value.selectedDepartamento?.depCodigo
                val area = _uiState.value.selectedArea?.areaCodigo
                val departamento = _uiState.value.selectedDpto?.dptoCodigo
                val seccion = _uiState.value.selectedSeccion?.seccCodigo
                val familia = _uiState.value.selectedFamilia?.fliaCodigo?.toString()
                
                android.util.Log.d("TomaManualViewModel", "📊 Valores de campos:")
                android.util.Log.d("TomaManualViewModel", "   • Sucursal: $sucursal")
                android.util.Log.d("TomaManualViewModel", "   • Depósito: $deposito")
                android.util.Log.d("TomaManualViewModel", "   • Área: $area")
                android.util.Log.d("TomaManualViewModel", "   • Departamento: $departamento")
                android.util.Log.d("TomaManualViewModel", "   • Sección: $seccion")
                android.util.Log.d("TomaManualViewModel", "   • Familia: $familia")
                
                if (sucursal == null || deposito == null || area == null || departamento == null || seccion == null) {
                    android.util.Log.e("TomaManualViewModel", "❌ Campos requeridos faltantes, abortando...")
                    return@launch
                }
                
                if (!_uiState.value.isFamiliaTodos && familia == null) {
                    android.util.Log.e("TomaManualViewModel", "❌ Familia requerida para consulta específica, abortando...")
                    return@launch
                }
                
                android.util.Log.d("TomaManualViewModel", "✅ Todos los campos requeridos están presentes")
                

                
                // Lógica especial para cuando se selecciona "Todos" en familia
                val isFamiliaTodos = _uiState.value.isFamiliaTodos
                android.util.Log.d("TomaManualViewModel", "🎯 Verificando tipo de consulta: isFamiliaTodos = $isFamiliaTodos")
                
                if (isFamiliaTodos) {
                    android.util.Log.d("TomaManualViewModel", "🌟 Ejecutando lógica para 'TODAS las familias'")
                    // Si se seleccionó "Todos" en familia, no requerir subgrupos
                    // Usar lista vacía para indicar que no se filtre por grupo/subgrupo
                    val subgruposSeleccionados = emptyList<Pair<Int, Int>>()
                    
                    android.util.Log.d("TomaManualViewModel", "Consultando TODAS las familias - esto puede tomar tiempo...")
                    _uiState.value = _uiState.value.copy(loadingMessage = "Consultando TODAS las familias...")
                    
                    // Consultar artículos con lotes desde Oracle en hilo de background
                    val articulosLotes = withContext(Dispatchers.IO) {
                        try {
                            _uiState.value = _uiState.value.copy(loadingMessage = "Ejecutando consulta Oracle...")
                            android.util.Log.d("TomaManualViewModel", "Ejecutando consulta Oracle en hilo de background...")
                            
                            android.util.Log.d("TomaManualViewModel", "🔄 Llamando al repositorio...")
                            val resultado = getArticulosLotesUseCase(
                                subgruposSeleccionados = subgruposSeleccionados,
                                sucursal = sucursal,
                                deposito = deposito,
                                area = area,
                                departamento = departamento,
                                seccion = seccion,
                                familia = familia ?: "", // Convertir null a string vacío
                                isFamiliaTodos = true,
                                isGruposTodos = false, // Para "todas las familias" no aplica grupos específicos
                                onProgressUpdate = { current, total ->
                                    updateProgress(current, total)
                                }
                            ).first()
                            
                            android.util.Log.d("TomaManualViewModel", "✅ Consulta Oracle completada exitosamente")
                            android.util.Log.d("TomaManualViewModel", "📊 Resultados recibidos: ${resultado.size} artículos")
                            resultado
                        } catch (e: Exception) {
                            android.util.Log.e("TomaManualViewModel", "❌ Error en consulta de todas las familias: ${e.message}", e)
                            android.util.Log.e("TomaManualViewModel", "❌ Stack trace completo:", e)
                            emptyList<ArticuloLote>()
                        }
                    }
                    
                    android.util.Log.d("TomaManualViewModel", "🎯 RESULTADO FINAL:")
                    android.util.Log.d("TomaManualViewModel", "   • Artículos recibidos del repositorio: ${articulosLotes.size}")
                    android.util.Log.d("TomaManualViewModel", "   • Tipo de datos: ${articulosLotes::class.simpleName}")
                    
                    _uiState.value = _uiState.value.copy(loadingMessage = "Procesando ${articulosLotes.size} artículos...")
                    
                    android.util.Log.d("TomaManualViewModel", "🔄 Actualizando UI State...")
                    _uiState.value = _uiState.value.copy(
                        articulosLotes = articulosLotes,
                        isLoading = false,
                        loadingMessage = null
                    )
                    android.util.Log.d("TomaManualViewModel", "✅ UI State actualizado:")
                    android.util.Log.d("TomaManualViewModel", "   • isLoading: ${_uiState.value.isLoading}")
                    android.util.Log.d("TomaManualViewModel", "   • articulosLotes.size: ${_uiState.value.articulosLotes.size}")
                    android.util.Log.d("TomaManualViewModel", "   • loadingMessage: ${_uiState.value.loadingMessage}")
                    
                    // Mostrar el diálogo automáticamente
                    android.util.Log.d("TomaManualViewModel", "🚀 Mostrando diálogo de artículos...")
                    showArticulosLotesDialog()
                    return@launch
                }
                
                // Lógica normal para familia específica
                _uiState.value = _uiState.value.copy(loadingMessage = "Verificando subgrupos seleccionados...")
                
                val isGruposTodos = _uiState.value.isGruposTodos
                
                // Crear lista de pares (grupCodigo, sugrCodigo) para la consulta
                val subgruposSeleccionados = if (isGruposTodos) {
                    // Si se seleccionaron todos los grupos, usar todos los subgrupos disponibles
                    _uiState.value.subgrupos.map { subgrupo ->
                        subgrupo.sugrGrupo to subgrupo.sugrCodigo
                    }
                } else {
                    // Si se seleccionaron grupos específicos, usar solo los subgrupos seleccionados
                    _uiState.value.selectedSubgrupos.map { subgrupo ->
                        subgrupo.sugrGrupo to subgrupo.sugrCodigo
                    }
                }
                
                android.util.Log.d("TomaManualViewModel", "🔍 Verificando subgrupos:")
                android.util.Log.d("TomaManualViewModel", "   • Es todos los grupos: $isGruposTodos")
                android.util.Log.d("TomaManualViewModel", "   • Subgrupos disponibles: ${_uiState.value.subgrupos.size}")
                android.util.Log.d("TomaManualViewModel", "   • Subgrupos seleccionados: ${_uiState.value.selectedSubgrupos.size}")
                android.util.Log.d("TomaManualViewModel", "   • Subgrupos para consulta: ${subgruposSeleccionados.size}")
                
                if (!isFamiliaTodos && !isGruposTodos && subgruposSeleccionados.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Debe seleccionar al menos un subgrupo",
                        isLoading = false,
                        loadingMessage = null
                    )
                    return@launch
                }
                

                
                _uiState.value = _uiState.value.copy(loadingMessage = "Consultando artículos específicos...")
                
                // Consultar artículos con lotes desde Oracle en hilo de background
                val articulosLotes = withContext(Dispatchers.IO) {
                    try {
                        _uiState.value = _uiState.value.copy(loadingMessage = "Ejecutando consulta Oracle...")
                        android.util.Log.d("TomaManualViewModel", "Ejecutando consulta Oracle para familia específica...")
                        
                        val resultado = getArticulosLotesUseCase(
                            subgruposSeleccionados = subgruposSeleccionados,
                            sucursal = sucursal,
                            deposito = deposito,
                            area = area,
                            departamento = departamento,
                            seccion = seccion,
                            familia = familia ?: "", // Convertir null a string vacío
                            isFamiliaTodos = false,
                            isGruposTodos = isGruposTodos, // ✅ Pasar el estado de grupos todos
                            onProgressUpdate = { current, total ->
                                updateProgress(current, total)
                            }
                        ).first()
                        
                        android.util.Log.d("TomaManualViewModel", "Consulta Oracle para familia específica completada")
                        resultado
                    } catch (e: Exception) {
                        android.util.Log.e("TomaManualViewModel", "Error en consulta de familia específica: ${e.message}", e)
                        emptyList<ArticuloLote>()
                    }
                }
                
                _uiState.value = _uiState.value.copy(loadingMessage = "Procesando ${articulosLotes.size} artículos...")
                
                _uiState.value = _uiState.value.copy(
                    articulosLotes = articulosLotes,
                    isLoading = false,
                    loadingMessage = null
                )
                android.util.Log.d("TomaManualViewModel", "Loading completado: ${_uiState.value.isLoading}")
                
                // Mostrar el diálogo automáticamente
                showArticulosLotesDialog()
                
            } catch (e: Exception) {
                android.util.Log.e("TomaManualViewModel", "Error general al cargar artículos: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar artículos: ${e.message}",
                    isLoading = false,
                    loadingMessage = null
                )
            }
        }
    }
    
    // Función para seleccionar/deseleccionar artículos con lotes
    fun toggleArticuloLote(articuloLote: ArticuloLote) {
        val currentSelected = _uiState.value.selectedArticulosLotes.toMutableList()
        
        if (currentSelected.contains(articuloLote)) {
            currentSelected.remove(articuloLote)
        } else {
            currentSelected.add(articuloLote)
        }
        
        _uiState.value = _uiState.value.copy(selectedArticulosLotes = currentSelected)
    }
    
    // Función para limpiar selección de artículos con lotes
    fun clearArticulosLotesSelection() {
        _uiState.value = _uiState.value.copy(selectedArticulosLotes = emptyList())
    }
    
    // Función para crear la toma del inventario (INSERT de cabecera)
    fun crearTomaInventario() {
        android.util.Log.d("TomaManualViewModel", "🚀 FUNCIÓN crearTomaInventario() EJECUTADA")
        android.util.Log.d("TomaManualViewModel", "📱 Usuario confirmó la creación de la toma")
        
        // Activar loading para la inserción
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            loadingMessage = "Creando toma del inventario...",
            loadingProgress = 0f,
            successMessage = null,
            errorMessage = null
        )
        
        viewModelScope.launch {
            try {
                android.util.Log.d("TomaManualViewModel", "🔍 Verificando campos requeridos para inserción...")
                
                // Verificar que todos los campos requeridos estén seleccionados
                val sucursal = _uiState.value.selectedSucursal?.sucCodigo
                val deposito = _uiState.value.selectedDepartamento?.depCodigo
                val area = _uiState.value.selectedArea?.areaCodigo
                val departamento = _uiState.value.selectedDpto?.dptoCodigo
                val seccion = _uiState.value.selectedSeccion?.seccCodigo
                val familia = _uiState.value.selectedFamilia?.fliaCodigo?.toString()
                val isFamiliaTodos = _uiState.value.isFamiliaTodos
                val isGruposTodos = _uiState.value.isGruposTodos
                
                // Construir lista de subgrupos según el tipo de selección
                val subgruposSeleccionados = if (isGruposTodos) {
                    // Si se seleccionaron todos los grupos, usar todos los subgrupos disponibles
                    _uiState.value.subgrupos.map { subgrupo ->
                        subgrupo.sugrGrupo to subgrupo.sugrCodigo
                    }
                } else {
                    // Si se seleccionaron grupos específicos, usar solo los subgrupos seleccionados
                    _uiState.value.selectedSubgrupos.map { subgrupo ->
                        subgrupo.sugrGrupo to subgrupo.sugrCodigo
                    }
                }
                
                android.util.Log.d("TomaManualViewModel", "📊 Valores para inserción:")
                android.util.Log.d("TomaManualViewModel", "   • Sucursal: $sucursal")
                android.util.Log.d("TomaManualViewModel", "   • Depósito: $deposito")
                android.util.Log.d("TomaManualViewModel", "   • Área: $area")
                android.util.Log.d("TomaManualViewModel", "   • Departamento: $departamento")
                android.util.Log.d("TomaManualViewModel", "   • Sección: $seccion")
                android.util.Log.d("TomaManualViewModel", "   • Familia: $familia")
                android.util.Log.d("TomaManualViewModel", "   • Es todas las familias: $isFamiliaTodos")
                android.util.Log.d("TomaManualViewModel", "   • Es todos los grupos: $isGruposTodos")
                android.util.Log.d("TomaManualViewModel", "   • Subgrupos seleccionados: $subgruposSeleccionados")
                android.util.Log.d("TomaManualViewModel", "   • Total subgrupos: ${subgruposSeleccionados.size}")
                
                if (sucursal == null || deposito == null || area == null || departamento == null || seccion == null) {
                    android.util.Log.e("TomaManualViewModel", "❌ Campos requeridos faltantes para inserción, abortando...")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Todos los campos deben estar seleccionados para crear la toma",
                        isLoading = false,
                        loadingMessage = null
                    )
                    return@launch
                }
                
                if (!isFamiliaTodos && familia == null) {
                    android.util.Log.e("TomaManualViewModel", "❌ Familia requerida para inserción, abortando...")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Debe seleccionar una familia para crear la toma",
                        isLoading = false,
                        loadingMessage = null
                    )
                    return@launch
                }
                
                if (!isFamiliaTodos && !isGruposTodos && subgruposSeleccionados.isEmpty()) {
                    android.util.Log.e("TomaManualViewModel", "❌ Subgrupos requeridos para inserción, abortando...")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Debe seleccionar al menos un subgrupo para crear la toma",
                        isLoading = false,
                        loadingMessage = null
                    )
                    return@launch
                }
                
                android.util.Log.d("TomaManualViewModel", "✅ Todos los campos requeridos están presentes para inserción")
                
                // 🚀 Insertar la cabecera del inventario
                android.util.Log.d("TomaManualViewModel", "🚀 Iniciando inserción de cabecera del inventario...")
                _uiState.value = _uiState.value.copy(
                    loadingMessage = "Insertando cabecera del inventario...",
                    loadingProgress = 10f
                )
                
                // 🔍 Obtener el usuario logueado
                android.util.Log.d("TomaManualViewModel", "🔍 Obteniendo usuario logueado...")
                val usuarioLogueado = withContext(Dispatchers.IO) {
                    try {
                        getLoggedUserUseCase()
                    } catch (e: Exception) {
                        android.util.Log.e("TomaManualViewModel", "❌ Error al obtener usuario logueado: ${e.message}", e)
                        null
                    }
                }
                
                if (usuarioLogueado == null) {
                    android.util.Log.e("TomaManualViewModel", "❌ No hay usuario logueado, abortando inserción...")
                    throw Exception("No hay usuario logueado en la aplicación")
                }
                
                android.util.Log.d("TomaManualViewModel", "✅ Usuario logueado obtenido: ${usuarioLogueado.username}")
                
                // 🚀 TRANSACCIÓN ÚNICA: Insertar cabecera y detalle en una sola operación
                android.util.Log.d("TomaManualViewModel", "🚀 Iniciando transacción única de cabecera y detalle...")
                _uiState.value = _uiState.value.copy(
                    loadingMessage = "Creando inventario completo...",
                    loadingProgress = 10f
                )
                
                val (idCabecera, totalArticulosInsertados) = withContext(Dispatchers.IO) {
                    try {
                        insertarCabeceraYDetalleInventarioUseCase(
                            sucursal = sucursal,
                            deposito = deposito,
                            area = area,
                            departamento = departamento,
                            seccion = seccion,
                            familia = familia,
                            subgruposSeleccionados = subgruposSeleccionados,
                            isFamiliaTodos = isFamiliaTodos,
                            userdb = usuarioLogueado.username, // ✅ Usuario real del login
                            inventarioVisible = _uiState.value.inventarioVisible,
                            articulosSeleccionados = _uiState.value.selectedArticulosLotes,
                            tipoToma = _uiState.value.tipoToma, // ✅ Tipo de toma (M=Manual, C=Criterio)
                            onProgressUpdate = { current, total ->
                                // Ajustar progreso del 10% al 90% para el detalle
                                val adjustedProgress = 10f + (current.toFloat() / total.toFloat() * 80f)
                                updateProgress(current, total, adjustedProgress)
                            }
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("TomaManualViewModel", "❌ Error en transacción única: ${e.message}", e)
                        throw e
                    }
                }
                
                android.util.Log.d("TomaManualViewModel", "✅ TRANSACCIÓN ÚNICA COMPLETADA EXITOSAMENTE")
                android.util.Log.d("TomaManualViewModel", "🎯 ID de cabecera generado: $idCabecera")
                android.util.Log.d("TomaManualViewModel", "📊 Total de artículos insertados: $totalArticulosInsertados")
                
                // ✅ Actualizar UI con éxito
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loadingMessage = null,
                    loadingProgress = 100f,
                    successMessage = "Toma del inventario creada exitosamente. ID: $idCabecera, Artículos: $totalArticulosInsertados",
                    errorMessage = null
                )
                
                // 🎉 Mostrar mensaje de éxito completo
                android.util.Log.d("TomaManualViewModel", "🎉 INVENTARIO COMPLETADO EXITOSAMENTE:")
                android.util.Log.d("TomaManualViewModel", "   • ID de cabecera: $idCabecera")
                android.util.Log.d("TomaManualViewModel", "   • Total de artículos: $totalArticulosInsertados")
                android.util.Log.d("TomaManualViewModel", "   • Usuario: ${usuarioLogueado.username}")
                android.util.Log.d("TomaManualViewModel", "   • Transacción única: ✅ EXITOSA")
                
                // TODO: Aquí se podría navegar a otra pantalla o mostrar un mensaje de confirmación
                
            } catch (e: Exception) {
                android.util.Log.e("TomaManualViewModel", "💥 Error general al crear toma del inventario: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al crear la toma del inventario: ${e.message}",
                    isLoading = false,
                    loadingMessage = null,
                    loadingProgress = 0f,
                    successMessage = null
                )
            }
        }
    }
    
    // Función para seleccionar solo los artículos VISIBLES (filtrados)
    fun selectAllArticulosLotes(articulosVisibles: List<ArticuloLote> = emptyList()) {
        val articulosASeleccionar = if (articulosVisibles.isNotEmpty()) {
            articulosVisibles
        } else {
            _uiState.value.articulosLotes
        }
        _uiState.value = _uiState.value.copy(selectedArticulosLotes = articulosASeleccionar)
    }
    
    // Función para seleccionar artículos específicos
    fun selectSpecificArticulosLotes(articulos: List<ArticuloLote>) {
        _uiState.value = _uiState.value.copy(selectedArticulosLotes = articulos)
    }
    
    // Función para limpiar selección cuando cambie el filtro
    fun clearSelectionOnFilterChange() {
        _uiState.value = _uiState.value.copy(selectedArticulosLotes = emptyList())
    }
    
    // Función para actualizar el progreso de la consulta
    fun updateProgress(current: Int, total: Int, adjustedProgress: Float? = null) {
        val percentage = if (total > 0) {
            (current.toFloat() / total.toFloat()) * 100f
        } else {
            0f
        }
        
        // Para el loading progress, usar el progreso ajustado si se proporciona, sino calcular
        val finalLoadingProgress = adjustedProgress ?: if (total > 0) {
            30f + (percentage * 0.6f) // 30% base + 60% del progreso del detalle
        } else {
            30f
        }
        
        _uiState.value = _uiState.value.copy(
            currentProgress = current,
            totalProgress = total,
            progressPercentage = percentage,
            loadingProgress = finalLoadingProgress
        )
        
        android.util.Log.d("TomaManualViewModel", "📊 Progreso actualizado: $current/$total (${String.format("%.1f", percentage)}%) - Loading: ${String.format("%.1f", finalLoadingProgress)}%")
    }
    
    // Función para resetear el progreso
    fun resetProgress() {
        _uiState.value = _uiState.value.copy(
            progressPercentage = 0f,
            currentProgress = 0,
            totalProgress = 0
        )
    }
}

