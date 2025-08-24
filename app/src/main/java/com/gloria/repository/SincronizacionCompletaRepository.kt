package com.gloria.repository

import com.gloria.data.dao.*
import com.gloria.data.entity.*
import com.gloria.util.ConnectionOracle
import com.gloria.util.Controles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.ResultSet

class SincronizacionCompletaRepository(
    private val areaDao: AreaDao,
    private val departamentoDao: DepartamentoDao,
    private val seccionDao: SeccionDao,
    private val familiaDao: FamiliaDao,
    private val grupoDao: GrupoDao,
    private val subgrupoDao: SubgrupoDao,
    private val sucursalDepartamentoDao: SucursalDepartamentoDao
) {
    
    suspend fun sincronizarTodasLasTablas(): Result<SincronizacionResult> = withContext(Dispatchers.IO) {
        try {
            val connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                return@withContext Result.failure(
                    Exception("Error de conexión: ${Controles.mensajeLogin}")
                )
            }
            
            val result = SincronizacionResult()
            
            // Sincronizar en orden jerárquico para mantener integridad referencial
            try {
                // 1. Sincronizar Áreas
                val areas = sincronizarAreas(connection)
                areaDao.deleteAllAreas()
                areaDao.insertAllAreas(areas)
                result.areasCount = areas.size
                
                // 2. Sincronizar Departamentos
                val departamentos = sincronizarDepartamentos(connection)
                departamentoDao.deleteAllDepartamentos()
                departamentoDao.insertAllDepartamentos(departamentos)
                result.departamentosCount = departamentos.size
                
                // 3. Sincronizar Secciones
                val secciones = sincronizarSecciones(connection)
                seccionDao.deleteAllSecciones()
                seccionDao.insertAllSecciones(secciones)
                result.seccionesCount = secciones.size
                
                // 4. Sincronizar Familias
                val familias = sincronizarFamilias(connection)
                familiaDao.deleteAllFamilias()
                familiaDao.insertAllFamilias(familias)
                result.familiasCount = familias.size
                
                // 5. Sincronizar Grupos
                val grupos = sincronizarGrupos(connection)
                grupoDao.deleteAllGrupos()
                grupoDao.insertAllGrupos(grupos)
                result.gruposCount = grupos.size
                
                // 6. Sincronizar Subgrupos
                val subgrupos = sincronizarSubgrupos(connection)
                subgrupoDao.deleteAllSubgrupos()
                subgrupoDao.insertAllSubgrupos(subgrupos)
                result.subgruposCount = subgrupos.size
                
                // 7. Sincronizar Sucursal-Departamento
                val sucursalDepartamentos = sincronizarSucursalDepartamentos(connection)
                sucursalDepartamentoDao.deleteAllSucursalDepartamentos()
                sucursalDepartamentoDao.insertAllSucursalDepartamentos(sucursalDepartamentos)
                result.sucursalDepartamentosCount = sucursalDepartamentos.size
                
                connection.close()
                
                result.timestamp = System.currentTimeMillis()
                Result.success(result)
                
            } catch (e: Exception) {
                connection.close()
                throw e
            }
            
        } catch (e: Exception) {
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
            areasCount = areaDao.getCount(),
            departamentosCount = departamentoDao.getCount(),
            seccionesCount = seccionDao.getCount(),
            familiasCount = familiaDao.getCount(),
            gruposCount = grupoDao.getCount(),
            subgruposCount = subgrupoDao.getCount(),
            sucursalDepartamentosCount = sucursalDepartamentoDao.getCount()
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
