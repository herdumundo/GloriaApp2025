package com.gloria.util

import android.util.Log
import com.gloria.data.entity.*

/**
 * Utilidad de logging para debuggear operaciones de inventario
 */
object InventoryLogger {
    
    private const val TAG = "InventoryLogger"
    
    // Logging de entidades
    fun logSucursal(sucursal: Sucursal, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Sucursal: ${sucursal.sucCodigo} - ${sucursal.sucDesc}")
    }
    
    fun logSucursales(sucursales: List<Sucursal>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Sucursales encontradas: ${sucursales.size}")
        sucursales.forEach { logSucursal(it, operation) }
    }
    
    fun logDepartamento(departamento: SucursalDepartamento, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Departamento: ${departamento.depCodigo} - ${departamento.depDesc} (Sucursal: ${departamento.sucCodigo})")
    }
    
    fun logDepartamentos(departamentos: List<SucursalDepartamento>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Departamentos encontrados: ${departamentos.size}")
        departamentos.forEach { logDepartamento(it, operation) }
    }
    
    fun logArea(area: Area, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Área: ${area.areaCodigo} - ${area.areaDesc}")
    }
    
    fun logAreas(areas: List<Area>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Áreas encontradas: ${areas.size}")
        areas.forEach { logArea(it, operation) }
    }
    
    fun logDpto(dpto: Departamento, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Dpto: ${dpto.dptoCodigo} - ${dpto.dptoDesc} (Área: ${dpto.dptoArea})")
    }
    
    fun logDptos(dptos: List<Departamento>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Dptos encontrados: ${dptos.size}")
        dptos.forEach { logDpto(it, operation) }
    }
    
    fun logSeccion(seccion: Seccion, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Sección: ${seccion.seccCodigo} - ${seccion.seccDesc} (Área: ${seccion.seccArea}, Dpto: ${seccion.seccDpto})")
    }
    
    fun logSecciones(secciones: List<Seccion>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Secciones encontradas: ${secciones.size}")
        secciones.forEach { logSeccion(it, operation) }
    }
    
    fun logFamilia(familia: Familia, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Familia: ${familia.fliaCodigo} - ${familia.fliaDesc} (Área: ${familia.fliaArea}, Dpto: ${familia.fliaDpto}, Sección: ${familia.fliaSeccion})")
    }
    
    fun logFamilias(familias: List<Familia>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Familias encontradas: ${familias.size}")
        familias.forEach { logFamilia(it, operation) }
    }
    
    fun logGrupo(grupo: Grupo, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Grupo: ${grupo.grupCodigo} - ${grupo.grupDesc} (Área: ${grupo.grupArea}, Dpto: ${grupo.grupDpto}, Sección: ${grupo.grupSeccion}, Familia: ${grupo.grupFamilia})")
    }
    
    fun logGrupos(grupos: List<Grupo>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Grupos encontrados: ${grupos.size}")
        grupos.forEach { logGrupo(it, operation) }
    }
    
    fun logSubgrupo(subgrupo: Subgrupo, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Subgrupo: ${subgrupo.sugrCodigo} - ${subgrupo.sugrDesc} (Área: ${subgrupo.sugrArea}, Dpto: ${subgrupo.sugrDpto}, Sección: ${subgrupo.sugrSeccion}, Familia: ${subgrupo.sugrFlia}, Grupo: ${subgrupo.sugrGrupo})")
    }
    
    fun logSubgrupos(subgrupos: List<Subgrupo>, operation: String = "INFO") {
        Log.d(TAG, "[$operation] Subgrupos encontrados: ${subgrupos.size}")
        subgrupos.forEach { logSubgrupo(it, operation) }
    }
    
    // Logging de consultas
    fun logQuery(query: String, params: Map<String, Any> = emptyMap()) {
        Log.d(TAG, "[QUERY] $query")
        if (params.isNotEmpty()) {
            Log.d(TAG, "[PARAMS] $params")
        }
    }
    
    fun logQueryResult(query: String, resultCount: Int) {
        Log.d(TAG, "[QUERY_RESULT] $query -> $resultCount resultados")
    }
    
    // Logging de estados
    fun logState(state: String, details: String = "") {
        Log.d(TAG, "[STATE] $state $details")
    }
    
    fun logError(operation: String, error: String, exception: Exception? = null) {
        Log.e(TAG, "[ERROR] $operation: $error", exception)
    }
    
    fun logWarning(operation: String, warning: String) {
        Log.w(TAG, "[WARNING] $operation: $warning")
    }
    
    fun logInfo(operation: String, info: String) {
        Log.i(TAG, "[INFO] $operation: $info")
    }
    
    fun logDebug(operation: String, debug: String) {
        Log.d(TAG, "[DEBUG] $operation: $debug")
    }
    
    // Logging específico para TomaManual
    fun logTomaManualState(
        selectedSucursal: Sucursal?,
        selectedDepartamento: SucursalDepartamento?,
        selectedArea: Area?,
        selectedDpto: Departamento?,
        selectedSeccion: Seccion?,
        selectedFamilia: Familia?,
        selectedGrupos: List<Grupo>,
        selectedSubgrupos: List<Subgrupo>
    ) {
        Log.d(TAG, "[TOMA_MANUAL_STATE] Estado actual:")
        Log.d(TAG, "  - Sucursal: ${selectedSucursal?.let { "${it.sucCodigo} - ${it.sucDesc}" } ?: "No seleccionada"}")
        Log.d(TAG, "  - Departamento: ${selectedDepartamento?.let { "${it.depCodigo} - ${it.depDesc}" } ?: "No seleccionado"}")
        Log.d(TAG, "  - Área: ${selectedArea?.let { "${it.areaCodigo} - ${it.areaDesc}" } ?: "No seleccionada"}")
        Log.d(TAG, "  - Dpto: ${selectedDpto?.let { "${it.dptoCodigo} - ${it.dptoDesc}" } ?: "No seleccionado"}")
        Log.d(TAG, "  - Sección: ${selectedSeccion?.let { "${it.seccCodigo} - ${it.seccDesc}" } ?: "No seleccionada"}")
        Log.d(TAG, "  - Familia: ${selectedFamilia?.let { "${it.fliaCodigo} - ${it.fliaDesc}" } ?: "No seleccionada"}")
        Log.d(TAG, "  - Grupos seleccionados: ${selectedGrupos.size}")
        selectedGrupos.forEach { grupo ->
            Log.d(TAG, "    * ${grupo.grupCodigo} - ${grupo.grupDesc}")
        }
        Log.d(TAG, "  - Subgrupos seleccionados: ${selectedSubgrupos.size}")
        selectedSubgrupos.forEach { subgrupo ->
            Log.d(TAG, "    * ${subgrupo.sugrCodigo} - ${subgrupo.sugrDesc}")
        }
    }
    
    // Logging de operaciones de base de datos
    fun logDatabaseOperation(operation: String, table: String, result: String) {
        Log.d(TAG, "[DB_OP] $operation en tabla $table: $result")
    }
    
    fun logDatabaseCount(table: String, count: Int) {
        Log.d(TAG, "[DB_COUNT] Tabla $table: $count registros")
    }
    
    // Logging de filtros
    fun logFilter(filter: String, value: Any, resultCount: Int) {
        Log.d(TAG, "[FILTER] $filter = $value -> $resultCount resultados")
    }
    
    // Logging de sincronización
    fun logSyncOperation(operation: String, details: String) {
        Log.d(TAG, "[SYNC] $operation: $details")
    }
}
