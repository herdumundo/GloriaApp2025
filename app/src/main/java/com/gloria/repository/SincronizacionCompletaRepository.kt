package com.gloria.repository

import com.gloria.data.entity.*
import com.gloria.data.entity.api.*
import com.gloria.data.repository.*
import com.gloria.domain.usecase.permission.SyncUserPermissionsFromOracleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.gloria.domain.usecase.AuthSessionUseCase

class SincronizacionCompletaRepository(
    private val areaRepository: AreaRepository,
    private val departamentoRepository: DepartamentoRepository,
    private val seccionRepository: SeccionRepository,
    private val familiaRepository: FamiliaRepository,
    private val grupoRepository: GrupoRepository,
    private val subgrupoRepository: SubgrupoRepository,
    private val sucursalDepartamentoRepository: SucursalDepartamentoRepository,
    private val authSessionUseCase: AuthSessionUseCase,
    private val syncUserPermissionsFromOracleUseCase: SyncUserPermissionsFromOracleUseCase,
    private val datosMaestrosApiRepository: DatosMaestrosApiRepository
) {
    
    suspend fun sincronizarTodasLasTablas(
        onProgress: (message: String, current: Int, total: Int) -> Unit = { _, _, _ -> },
        userdb: String = "invap",
        passdb: String = "invext2024"
    ): Result<SincronizacionResult> = withContext(Dispatchers.IO) {
        Log.d("PROCESO_LOGIN", "=== INICIANDO sincronizarTodasLasTablas con API ===")
        Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo IO: ${Thread.currentThread().name}")
        
        try {
            Log.d("PROCESO_LOGIN", "🌐 Llamando a API de datos maestros...")
            onProgress("🌐 Conectando a API...", 0, 8)
            
            // Llamar a la API para obtener todos los datos maestros
            val apiResult = datosMaestrosApiRepository.getDatosMaestros(userdb, passdb)
            
            if (apiResult.isFailure) {
                Log.e("PROCESO_LOGIN", "❌ ERROR en API: ${apiResult.exceptionOrNull()?.message}")
                return@withContext Result.failure(
                    Exception("Error al obtener datos de la API: ${apiResult.exceptionOrNull()?.message}")
                )
            }
            
            val datosMaestros = apiResult.getOrNull()!!
            Log.d("PROCESO_LOGIN", "✅ Datos recibidos de la API - Áreas: ${datosMaestros.areas.size}, Departamentos: ${datosMaestros.departamentos.size}")
            
            val result = SincronizacionResult()
            
            try {
                // 1. Sincronizar Áreas
                Log.d("PROCESO_LOGIN", "📁 Sincronizando áreas...")
                onProgress("📁 Sincronizando áreas...", 1, 8)
                val areas = convertirAreasFromApi(datosMaestros.areas)
                areaRepository.deleteAllAreas()
                areaRepository.insertAllAreas(areas)
                result.areasCount = areas.size
                Log.d("PROCESO_LOGIN", "✅ Áreas sincronizadas: ${areas.size}")
                
                // 2. Sincronizar Departamentos
                Log.d("PROCESO_LOGIN", "📂 Sincronizando departamentos...")
                onProgress("📂 Sincronizando departamentos...", 2, 8)
                val departamentos = convertirDepartamentosFromApi(datosMaestros.departamentos)
                departamentoRepository.deleteAllDepartamentos()
                departamentoRepository.insertAllDepartamentos(departamentos)
                result.departamentosCount = departamentos.size
                Log.d("PROCESO_LOGIN", "✅ Departamentos sincronizados: ${departamentos.size}")
                
                // 3. Sincronizar Secciones
                Log.d("PROCESO_LOGIN", "📋 Sincronizando secciones...")
                onProgress("📋 Sincronizando secciones...", 3, 8)
                val secciones = convertirSeccionesFromApi(datosMaestros.secciones)
                seccionRepository.deleteAllSecciones()
                seccionRepository.insertAllSecciones(secciones)
                result.seccionesCount = secciones.size
                Log.d("PROCESO_LOGIN", "✅ Secciones sincronizadas: ${secciones.size}")
                
                // 4. Sincronizar Familias
                Log.d("PROCESO_LOGIN", "👨‍👩‍👧‍👦 Sincronizando familias...")
                onProgress("👨‍👩‍👧‍👦 Sincronizando familias...", 4, 8)
                val familias = convertirFamiliasFromApi(datosMaestros.familias)
                familiaRepository.deleteAllFamilias()
                familiaRepository.insertAllFamilias(familias)
                result.familiasCount = familias.size
                Log.d("PROCESO_LOGIN", "✅ Familias sincronizadas: ${familias.size}")
                
                // 5. Sincronizar Grupos
                Log.d("PROCESO_LOGIN", "👥 Sincronizando grupos...")
                onProgress("👥 Sincronizando grupos...", 5, 8)
                val grupos = convertirGruposFromApi(datosMaestros.grupos)
                grupoRepository.deleteAllGrupos()
                grupoRepository.insertAllGrupos(grupos)
                result.gruposCount = grupos.size
                Log.d("PROCESO_LOGIN", "✅ Grupos sincronizados: ${grupos.size}")
                
                // 6. Sincronizar Subgrupos
                Log.d("PROCESO_LOGIN", "🔗 Sincronizando subgrupos...")
                onProgress("🔗 Sincronizando subgrupos...", 6, 8)
                val subgrupos = convertirSubgruposFromApi(datosMaestros.subgrupos)
                subgrupoRepository.deleteAllSubgrupos()
                subgrupoRepository.insertAllSubgrupos(subgrupos)
                result.subgruposCount = subgrupos.size
                Log.d("PROCESO_LOGIN", "✅ Subgrupos sincronizados: ${subgrupos.size}")
                
                // 7. Sincronizar Sucursal-Departamento
                Log.d("PROCESO_LOGIN", "🏢 Sincronizando sucursales y departamentos...")
                onProgress("🏢 Sincronizando sucursales y departamentos...", 7, 8)
                val sucursalDepartamentos = convertirSucursalDepartamentosFromApi(datosMaestros.sucursalesDepartamentos)
                sucursalDepartamentoRepository.deleteAllSucursalDepartamentos()
                sucursalDepartamentoRepository.insertAllSucursalDepartamentos(sucursalDepartamentos)
                result.sucursalDepartamentosCount = sucursalDepartamentos.size
                Log.d("PROCESO_LOGIN", "✅ Sucursal-Departamentos sincronizados: ${sucursalDepartamentos.size}")
                
                // 8. Sincronizar Permisos del Usuario (Opcional)
                Log.d("PROCESO_LOGIN", "🔐 Sincronizando permisos del usuario...")
                onProgress("🔐 Sincronizando permisos del usuario...", 8, 8)
                
                try {
                    // Obtener el usuario actual del AuthSessionUseCase
                    val currentUser = authSessionUseCase.getCurrentUser()
                    if (currentUser != null) {
                        Log.d("PROCESO_LOGIN", "👤 Sincronizando permisos para usuario: ${currentUser.username}")
                        
                        val syncResult = syncUserPermissionsFromOracleUseCase(currentUser.username, currentUser.password)
                        syncResult.fold(
                            onSuccess = {
                                Log.d("PROCESO_LOGIN", "✅ Permisos sincronizados exitosamente para ${currentUser.username}")
                            },
                            onFailure = { error ->
                                Log.w("PROCESO_LOGIN", "⚠️ Error al sincronizar permisos para ${currentUser.username}: ${error.message}")
                                Log.w("PROCESO_LOGIN", "⚠️ Continuando sin permisos sincronizados - el usuario puede usar la app normalmente")
                                // No fallar toda la sincronización por error de permisos
                                // El usuario puede usar la app sin permisos sincronizados
                            }
                        )
                    } else {
                        Log.w("PROCESO_LOGIN", "⚠️ No hay usuario logueado, omitiendo sincronización de permisos")
                    }
                } catch (e: Exception) {
                    Log.w("PROCESO_LOGIN", "⚠️ Error inesperado al sincronizar permisos: ${e.message}")
                    Log.w("PROCESO_LOGIN", "⚠️ Continuando sin permisos sincronizados - el usuario puede usar la app normalmente")
                    // No fallar toda la sincronización por error de permisos
                }
                
                result.timestamp = System.currentTimeMillis()
                Log.d("PROCESO_LOGIN", "✅ SINCRONIZACIÓN API COMPLETADA EXITOSAMENTE")
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
                Log.e("PROCESO_LOGIN", "❌ ERROR durante sincronización API: ${e.message}")
                Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
                throw e
            }
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ ERROR GENERAL en sincronizarTodasLasTablas API: ${e.message}")
            Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
    
    // Métodos de conversión de modelos API a entidades de base de datos
    private fun convertirAreasFromApi(areasApi: List<AreaApi>): List<Area> {
        return areasApi.map { areaApi ->
            Area(
                areaCodigo = areaApi.areaCodigo,
                areaDesc = areaApi.areaDesc,
                syncTimestamp = System.currentTimeMillis()
            )
        }
    }
    
    private fun convertirDepartamentosFromApi(departamentosApi: List<DepartamentoApi>): List<Departamento> {
        return departamentosApi.map { departamentoApi ->
            Departamento(
                dptoCodigo = departamentoApi.dptoCodigo,
                dptoDesc = departamentoApi.dptoDesc,
                dptoArea = departamentoApi.dptoArea,
                syncTimestamp = System.currentTimeMillis()
            )
        }
    }
    
    private fun convertirSeccionesFromApi(seccionesApi: List<SeccionApi>): List<Seccion> {
        return seccionesApi.map { seccionApi ->
            Seccion(
                seccCodigo = seccionApi.seccCodigo,
                seccDesc = seccionApi.seccDesc,
                seccArea = seccionApi.seccArea,
                seccDpto = seccionApi.seccDpto,
                syncTimestamp = System.currentTimeMillis()
            )
        }
    }
    
    private fun convertirFamiliasFromApi(familiasApi: List<FamiliaApi>): List<Familia> {
        return familiasApi.map { familiaApi ->
            Familia(
                fliaCodigo = familiaApi.fliaCodigo,
                fliaDesc = familiaApi.fliaDesc,
                fliaArea = familiaApi.fliaArea,
                fliaDpto = familiaApi.fliaDpto,
                fliaSeccion = familiaApi.fliaSeccion,
                syncTimestamp = System.currentTimeMillis()
            )
        }
    }
    
    private fun convertirGruposFromApi(gruposApi: List<GrupoApi>): List<Grupo> {
        return gruposApi.map { grupoApi ->
            Grupo(
                grupCodigo = grupoApi.grupCodigo,
                grupDesc = grupoApi.grupDesc,
                grupArea = grupoApi.grupArea,
                grupDpto = grupoApi.grupDpto,
                grupSeccion = grupoApi.grupSeccion,
                grupFamilia = grupoApi.grupFamilia,
                syncTimestamp = System.currentTimeMillis()
            )
        }
    }
    
    private fun convertirSubgruposFromApi(subgruposApi: List<SubgrupoApi>): List<Subgrupo> {
        return subgruposApi.map { subgrupoApi ->
            Subgrupo(
                sugrCodigo = subgrupoApi.sugrCodigo,
                sugrDesc = subgrupoApi.sugrDesc,
                sugrArea = subgrupoApi.sugrArea,
                sugrDpto = subgrupoApi.sugrDpto,
                sugrSeccion = subgrupoApi.sugrSeccion,
                sugrFlia = subgrupoApi.sugrFlia,
                sugrGrupo = subgrupoApi.sugrGrupo,
                syncTimestamp = System.currentTimeMillis()
            )
        }
    }
    
    private fun convertirSucursalDepartamentosFromApi(sucursalDepartamentosApi: List<SucursalDepartamentoApi>): List<SucursalDepartamento> {
        return sucursalDepartamentosApi.map { sucursalDepartamentoApi ->
            SucursalDepartamento(
                sucCodigo = sucursalDepartamentoApi.sucCodigo,
                sucDesc = sucursalDepartamentoApi.sucDesc,
                depCodigo = sucursalDepartamentoApi.depCodigo,
                depDesc = sucursalDepartamentoApi.depDesc,
                syncTimestamp = System.currentTimeMillis()
            )
        }
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
