package com.gloria.repository

import com.gloria.data.entity.*
import com.gloria.data.repository.*
import com.gloria.util.ConnectionOracle
import com.gloria.util.Controles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.ResultSet
import android.util.Log

class SincronizacionCompletaRepository(
    private val areaRepository: AreaRepository,
    private val departamentoRepository: DepartamentoRepository,
    private val seccionRepository: SeccionRepository,
    private val familiaRepository: FamiliaRepository,
    private val grupoRepository: GrupoRepository,
    private val subgrupoRepository: SubgrupoRepository,
    private val sucursalDepartamentoRepository: SucursalDepartamentoRepository
) {
    
    suspend fun sincronizarTodasLasTablas(
        onProgress: (message: String, current: Int, total: Int) -> Unit = { _, _, _ -> }
    ): Result<SincronizacionResult> = withContext(Dispatchers.IO) {
        Log.d("PROCESO_LOGIN", "=== INICIANDO sincronizarTodasLasTablas ===")
        Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo IO: ${Thread.currentThread().name}")
        
        try {
            Log.d("PROCESO_LOGIN", "🔍 Obteniendo conexión Oracle...")
            val connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                Log.e("PROCESO_LOGIN", "❌ CONEXIÓN FALLIDA - connection es null")
                Log.e("PROCESO_LOGIN", "Controles.resBD: ${Controles.resBD}")
                Log.e("PROCESO_LOGIN", "Controles.mensajeLogin: ${Controles.mensajeLogin}")
                return@withContext Result.failure(
                    Exception("Error de conexión: ${Controles.mensajeLogin}")
                )
            }
            
            Log.d("PROCESO_LOGIN", "✅ CONEXIÓN EXITOSA - connection obtenida")
            val result = SincronizacionResult()
            
            // Sincronizar en orden jerárquico para mantener integridad referencial
            try {
                onProgress("🔄 Conectando a Oracle...", 0, 7)
                
                // 1. Sincronizar Áreas
                Log.d("PROCESO_LOGIN", "📁 Sincronizando áreas...")
                onProgress("📁 Sincronizando áreas...", 1, 7)
                val areas = sincronizarAreas(connection)
                areaRepository.deleteAllAreas()
                areaRepository.insertAllAreas(areas)
                result.areasCount = areas.size
                Log.d("PROCESO_LOGIN", "✅ Áreas sincronizadas: ${areas.size}")
                
                // 2. Sincronizar Departamentos
                Log.d("SincronizacionCompletaRepository", "📂 Sincronizando departamentos...")
                onProgress("📂 Sincronizando departamentos...", 2, 7)
                val departamentos = sincronizarDepartamentos(connection)
                departamentoRepository.deleteAllDepartamentos()
                departamentoRepository.insertAllDepartamentos(departamentos)
                result.departamentosCount = departamentos.size
                Log.d("SincronizacionCompletaRepository", "✅ Departamentos sincronizados: ${departamentos.size}")
                
                // 3. Sincronizar Secciones
                Log.d("SincronizacionCompletaRepository", "📋 Sincronizando secciones...")
                onProgress("📋 Sincronizando secciones...", 3, 7)
                val secciones = sincronizarSecciones(connection)
                seccionRepository.deleteAllSecciones()
                seccionRepository.insertAllSecciones(secciones)
                result.seccionesCount = secciones.size
                Log.d("SincronizacionCompletaRepository", "✅ Secciones sincronizadas: ${secciones.size}")
                
                // 4. Sincronizar Familias
                Log.d("SincronizacionCompletaRepository", "👨‍👩‍👧‍👦 Sincronizando familias...")
                onProgress("👨‍👩‍👧‍👦 Sincronizando familias...", 4, 7)
                val familias = sincronizarFamilias(connection)
                familiaRepository.deleteAllFamilias()
                familiaRepository.insertAllFamilias(familias)
                result.familiasCount = familias.size
                Log.d("SincronizacionCompletaRepository", "✅ Familias sincronizadas: ${familias.size}")
                
                // 5. Sincronizar Grupos
                Log.d("SincronizacionCompletaRepository", "👥 Sincronizando grupos...")
                onProgress("👥 Sincronizando grupos...", 5, 7)
                val grupos = sincronizarGrupos(connection)
                grupoRepository.deleteAllGrupos()
                grupoRepository.insertAllGrupos(grupos)
                result.gruposCount = grupos.size
                Log.d("SincronizacionCompletaRepository", "✅ Grupos sincronizados: ${grupos.size}")
                
                // 6. Sincronizar Subgrupos
                Log.d("SincronizacionCompletaRepository", "🔗 Sincronizando subgrupos...")
                onProgress("🔗 Sincronizando subgrupos...", 6, 7)
                val subgrupos = sincronizarSubgrupos(connection)
                subgrupoRepository.deleteAllSubgrupos()
                subgrupoRepository.insertAllSubgrupos(subgrupos)
                result.subgruposCount = subgrupos.size
                Log.d("SincronizacionCompletaRepository", "✅ Subgrupos sincronizados: ${subgrupos.size}")
                
                // 7. Sincronizar Sucursal-Departamento
                Log.d("SincronizacionCompletaRepository", "🏢 Sincronizando sucursales y departamentos...")
                onProgress("🏢 Sincronizando sucursales y departamentos...", 7, 7)
                val sucursalDepartamentos = sincronizarSucursalDepartamentos(connection)
                sucursalDepartamentoRepository.deleteAllSucursalDepartamentos()
                sucursalDepartamentoRepository.insertAllSucursalDepartamentos(sucursalDepartamentos)
                result.sucursalDepartamentosCount = sucursalDepartamentos.size
                Log.d("SincronizacionCompletaRepository", "✅ Sucursal-Departamentos sincronizados: ${sucursalDepartamentos.size}")
                
                connection.close()
                Log.d("PROCESO_LOGIN", "🔒 Conexión cerrada")
                
                result.timestamp = System.currentTimeMillis()
                Log.d("PROCESO_LOGIN", "✅ SINCRONIZACIÓN COMPLETADA EXITOSAMENTE")
                Log.d("PROCESO_LOGIN", "📊 Total elementos sincronizados:")
                Log.d("PROCESO_LOGIN", "   - Áreas: ${result.areasCount}")
                Log.d("PROCESO_LOGIN", "   - Departamentos: ${result.departamentosCount}")
                Log.d("PROCESO_LOGIN", "   - Secciones: ${result.seccionesCount}")
                Log.d("PROCESO_LOGIN", "   - Familias: ${result.familiasCount}")
                Log.d("PROCESO_LOGIN", "   - Grupos: ${result.gruposCount}")
                Log.d("PROCESO_LOGIN", "   - Subgrupos: ${result.subgruposCount}")
                Log.d("PROCESO_LOGIN", "   - Sucursal-Departamentos: ${result.sucursalDepartamentosCount}")
                
                Result.success(result)
                
            } catch (e: Exception) {
                Log.e("PROCESO_LOGIN", "❌ ERROR durante sincronización: ${e.message}")
                Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
                connection.close()
                throw e
            }
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ ERROR GENERAL en sincronizarTodasLasTablas: ${e.message}")
            Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
    
    private suspend fun sincronizarAreas(connection: java.sql.Connection): List<Area> {
        val query = "SELECT * FROM V_WEB_AREA"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        
        val areas = mutableListOf<Area>()
        while (resultSet.next()) {
            areas.add(
                Area(
                    areaCodigo = resultSet.getInt("AREA_CODIGO"),
                    areaDesc = resultSet.getString("AREA_DESC") ?: "",
                    syncTimestamp = System.currentTimeMillis()
                )
            )
        }
        
        resultSet.close()
        statement.close()
        return areas
    }
    
    private suspend fun sincronizarDepartamentos(connection: java.sql.Connection): List<Departamento> {
        val query = "SELECT * FROM V_WEB_DPTO"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        
        val departamentos = mutableListOf<Departamento>()
        while (resultSet.next()) {
            departamentos.add(
                Departamento(
                    dptoCodigo = resultSet.getInt("DPTO_CODIGO"),
                    dptoDesc = resultSet.getString("DPTO_DESC") ?: "",
                    dptoArea = resultSet.getInt("DPTO_AREA"),
                    syncTimestamp = System.currentTimeMillis()
                )
            )
        }
        
        resultSet.close()
        statement.close()
        return departamentos
    }
    
    private suspend fun sincronizarSecciones(connection: java.sql.Connection): List<Seccion> {
        val query = "SELECT * FROM V_WEB_SECC"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        
        val secciones = mutableListOf<Seccion>()
        while (resultSet.next()) {
            secciones.add(
                Seccion(
                    seccCodigo = resultSet.getInt("SECC_CODIGO"),
                    seccDesc = resultSet.getString("SECC_DESC") ?: "",
                    seccArea = resultSet.getInt("SECC_AREA"),
                    seccDpto = resultSet.getInt("SECC_DPTO"),
                    syncTimestamp = System.currentTimeMillis()
                )
            )
        }
        
        resultSet.close()
        statement.close()
        return secciones
    }
    
    private suspend fun sincronizarFamilias(connection: java.sql.Connection): List<Familia> {
        val query = "SELECT * FROM ADCS.V_WEB_FLIA"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        
        val familias = mutableListOf<Familia>()
        while (resultSet.next()) {
            familias.add(
                Familia(
                    fliaCodigo = resultSet.getInt("FLIA_CODIGO"),
                    fliaDesc = resultSet.getString("FLIA_DESC") ?: "",
                    fliaArea = resultSet.getInt("FLIA_AREA"),
                    fliaDpto = resultSet.getInt("FLIA_DPTO"),
                    fliaSeccion = resultSet.getInt("FLIA_SECCION"),
                    syncTimestamp = System.currentTimeMillis()
                )
            )
        }
        
        resultSet.close()
        statement.close()
        return familias
    }
    
    private suspend fun sincronizarGrupos(connection: java.sql.Connection): List<Grupo> {
        val query = "SELECT * FROM  V_WEB_GRUPO"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        
        val grupos = mutableListOf<Grupo>()
        while (resultSet.next()) {
            grupos.add(
                Grupo(
                    grupCodigo = resultSet.getInt("GRUP_CODIGO"),
                    grupDesc = resultSet.getString("GRUP_DESC") ?: "",
                    grupArea = resultSet.getInt("GRUP_AREA"),
                    grupDpto = resultSet.getInt("GRUP_DPTO"),
                    grupSeccion = resultSet.getInt("GRUP_SECCION"),
                    grupFamilia = resultSet.getInt("GRUP_FAMILIA"),
                    syncTimestamp = System.currentTimeMillis()
                )
            )
        }
        
        resultSet.close()
        statement.close()
        return grupos
    }
    
    private suspend fun sincronizarSubgrupos(connection: java.sql.Connection): List<Subgrupo> {
        val query = "SELECT * FROM ADCS.V_WEB_SUBGRUPO"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        
        val subgrupos = mutableListOf<Subgrupo>()
        while (resultSet.next()) {
            subgrupos.add(
                Subgrupo(
                    sugrCodigo = resultSet.getInt("SUGR_CODIGO"),
                    sugrDesc = resultSet.getString("SUGR_DESC") ?: "",
                    sugrArea = resultSet.getInt("SUGR_AREA"),
                    sugrDpto = resultSet.getInt("SUGR_DPTO"),
                    sugrSeccion = resultSet.getInt("SUGR_SECCION"),
                    sugrFlia = resultSet.getInt("SUGR_FLIA"),
                    sugrGrupo = resultSet.getInt("SUGR_GRUPO"),
                    syncTimestamp = System.currentTimeMillis()
                )
            )
        }
        
        resultSet.close()
        statement.close()
        return subgrupos
    }
    
    private suspend fun sincronizarSucursalDepartamentos(connection: java.sql.Connection): List<SucursalDepartamento> {
        val query = "SELECT * FROM ADCS.V_WEB_SUC_DEP"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        
        val sucursalDepartamentos = mutableListOf<SucursalDepartamento>()
        while (resultSet.next()) {
            sucursalDepartamentos.add(
                SucursalDepartamento(
                    sucCodigo = resultSet.getInt("SUC_CODIGO"),
                    sucDesc = resultSet.getString("SUC_DESC") ?: "",
                    depCodigo = resultSet.getInt("DEP_CODIGO"),
                    depDesc = resultSet.getString("DEP_DESC") ?: "",
                    syncTimestamp = System.currentTimeMillis()
                )
            )
        }
        
        resultSet.close()
        statement.close()
        return sucursalDepartamentos
    }
    
    // Métodos para obtener estadísticas
    suspend fun getEstadisticasSincronizacion(): EstadisticasSincronizacion {
        return EstadisticasSincronizacion(
            areasCount = areaRepository.getCount(),
            departamentosCount = departamentoRepository.getCount(),
            seccionesCount = seccionRepository.getCount(),
            familiasCount = familiaRepository.getCount(),
            gruposCount = grupoRepository.getCount(),
            subgruposCount = subgrupoRepository.getCount(),
            sucursalDepartamentosCount = sucursalDepartamentoRepository.getCount()
        )
    }
}

data class SincronizacionResult(
    var areasCount: Int = 0,
    var departamentosCount: Int = 0,
    var seccionesCount: Int = 0,
    var familiasCount: Int = 0,
    var gruposCount: Int = 0,
    var subgruposCount: Int = 0,
    var sucursalDepartamentosCount: Int = 0,
    var timestamp: Long = 0
)

data class EstadisticasSincronizacion(
    val areasCount: Int,
    val departamentosCount: Int,
    val seccionesCount: Int,
    val familiasCount: Int,
    val gruposCount: Int,
    val subgruposCount: Int,
    val sucursalDepartamentosCount: Int
)
