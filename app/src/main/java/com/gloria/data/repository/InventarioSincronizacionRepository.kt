package com.gloria.data.repository

import android.util.Log
import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.entity.InventarioDetalle
import com.gloria.data.entity.api.InventarioSincronizacionApi
import com.gloria.data.model.InventarioSincronizacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para la sincronización de inventarios desde Oracle a Room
 */
@Singleton
class InventarioSincronizacionRepository @Inject constructor(
    private val inventarioDetalleDao: InventarioDetalleDao,
    private val inventariosPorSucursalApiRepository: InventariosPorSucursalApiRepository,
    private val authSessionUseCase: com.gloria.domain.usecase.AuthSessionUseCase
) {
    
    /**
     * Sincroniza inventarios desde Oracle a Room
     * @param onProgressUpdate Callback para actualizar el progreso
     * @return Flow con el resultado de la sincronización
     */
    fun sincronizarInventarios(
        onProgressUpdate: (String, Int, Int) -> Unit
    ): Flow<Result<Int>> = flow {
        try {
            Log.d("PROCESO_LOGIN", "=== INICIANDO sincronizarInventarios ===")
            Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo: ${Thread.currentThread().name}")

            // 🚀 Iniciando sincronización
            onProgressUpdate("🔄 Iniciando sincronización de inventarios...", 0, 0)
            
            // 📊 Obtener inventarios desde API
            val inventariosApi = obtenerInventariosDesdeApi(onProgressUpdate)
            // 🗑️ Limpiar inventarios existentes
        //    onProgressUpdate("🗑️ Limpiando inventarios existentes...", 0, inventariosConvertidos.size)
            inventarioDetalleDao.deleteAllInventariosDetalle()

            if (inventariosApi.isEmpty()) {
                onProgressUpdate("✅ No se encontraron inventarios para sincronizar", 0, 0)
                emit(Result.success(0))
                return@flow
            }
            
            Log.d("PROCESO_LOGIN", "📊 Inventarios obtenidos desde API: ${inventariosApi.size}")
            
            // 🔄 Convertir inventarios API a modelo local
            val inventariosConvertidos = convertirInventariosApiToLocal(inventariosApi)
            
            Log.d("PROCESO_LOGIN", "🔄 Inventarios convertidos: ${inventariosConvertidos.size}")
            

            // 💾 Insertar nuevos inventarios
            onProgressUpdate("💾 Insertando inventarios sincronizados...", 0, inventariosConvertidos.size)
            val inventariosDetalle = convertirInventariosToDetalle(inventariosConvertidos)
            inventarioDetalleDao.insertInventariosDetalle(inventariosDetalle)
            
            Log.d("PROCESO_LOGIN", "✅ Inventarios sincronizados exitosamente: ${inventariosDetalle.size}")
            
            // 🎉 Finalizar
            onProgressUpdate("🎉 Sincronización completada: ${inventariosDetalle.size} inventarios", inventariosDetalle.size, inventariosDetalle.size)
            emit(Result.success(inventariosDetalle.size))
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ Error en sincronización de inventarios: ${e.message}", e)
            onProgressUpdate("❌ Error: ${e.message}", 0, 0)
            emit(Result.failure(e))
        }
    }
    
    /**
     * Obtiene inventarios desde la API
     */
    private suspend fun obtenerInventariosDesdeApi(
        onProgressUpdate: (String, Int, Int) -> Unit
    ): List<InventarioSincronizacionApi> = withContext(Dispatchers.IO) {
        Log.d("PROCESO_LOGIN", "=== INICIANDO obtenerInventariosDesdeApi ===")
        Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo IO: ${Thread.currentThread().name}")
        
        try {
            // 🔍 Obtener usuario logueado
            val currentUser = authSessionUseCase.getCurrentUser()
            if (currentUser == null) {
                throw Exception("No hay usuario logueado")
            }
            
            Log.d("PROCESO_LOGIN", "👤 Usuario logueado: ${currentUser.username}")
            
            onProgressUpdate("🌐 Consultando inventarios desde API...", 0, 0)
            
            // 🌐 Llamar a la API usando la sucursal guardada en logged_user
            val sucursalId = currentUser.sucursalId
                ?: throw Exception("No hay sucursal seleccionada en la sesión")

            val apiResult = inventariosPorSucursalApiRepository.getInventariosPorSucursal(
                userdb = currentUser.username,
                passdb = currentUser.password,
                ardeSuc = sucursalId
            )
            
            if (apiResult.isFailure) {
                val error = apiResult.exceptionOrNull() ?: Exception("Error desconocido")
                Log.e("PROCESO_LOGIN", "❌ Error en API: ${error.message}", error)
                throw error
            }
            
            val response = apiResult.getOrNull()!!
            Log.d("PROCESO_LOGIN", "✅ Respuesta API recibida - Success: ${response.success}, Length: ${response.length}")
            
            if (!response.success) {
                throw Exception("API retornó success=false")
            }
            
            val inventarios = response.data
            Log.d("PROCESO_LOGIN", "📊 Inventarios obtenidos desde API: ${inventarios.size}")
            
            onProgressUpdate("📊 Inventarios obtenidos: ${inventarios.size}", inventarios.size, 0)
            
            inventarios
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ Error al obtener inventarios desde API: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * Convierte inventarios de la API al modelo local
     */
    private fun convertirInventariosApiToLocal(inventariosApi: List<InventarioSincronizacionApi>): List<InventarioSincronizacion> {
        return inventariosApi.map { api ->
            InventarioSincronizacion(
                toma = api.toma,
                invd_cant_inv = api.invdCantInv,
                ART_DESC = api.artDesc,
                ARDE_SUC = api.ardeSuc,
                winvd_nro_inv = api.winvdNroInv,
                winvd_art = api.winvdArt.toString(),
                winvd_lote = api.winvdLote ?: "",
                winvd_fec_vto = api.winvdFecVto ?: "",
                winvd_area = api.winvdArea,
                winvd_dpto = api.winvdDpto,
                winvd_secc = api.winvdSecc,
                winvd_flia = api.winvdFlia,
                winvd_grupo = api.winvdGrupo,
                winvd_cant_act = api.winvdCantAct,
                winve_fec = api.winveFec ?: "",
                dpto_desc = api.dptoDesc,
                secc_desc = api.seccDesc,
                flia_desc = api.fliaDesc,
                grup_desc = api.grupDesc,
                area_desc = api.areaDesc,
                sugr_codigo = api.sugrCodigo,
                winvd_secu = api.winvdSecu,
                tipo_toma = api.tipoToma,
                winve_login = api.winveLogin,
                winvd_consolidado = api.winvdConsolidado ?: "",
                desc_grupo_parcial = api.descGrupoParcial,
                desc_familia = api.descFamilia,
                winve_dep = api.winveDep.toString(),
                winve_suc = api.winveSuc.toString(),
                coba_codigo_barra = api.cobaCodigoBarra ?: "",
                caja = api.caja,
                GRUESA = api.gruesa,
                UNID_IND = api.unidInd,
                SUC_DESC = api.sucDesc,
                DEP_DESC = api.depDesc,
                WINVE_STOCK_VISIBLE = api.winveStockVisible
            )
        }
    }
    
    /**
     * Convierte inventarios a detalle para Room
     */
    private fun convertirInventariosToDetalle(inventarios: List<InventarioSincronizacion>): List<InventarioDetalle> {
        return inventarios.map { oracle ->
            InventarioDetalle(
                winvd_nro_inv = oracle.winvd_nro_inv,
                winvd_secu = oracle.winvd_secu,
                winvd_cant_act = oracle.winvd_cant_act,
                winvd_cant_inv = 0,
                winvd_fec_vto = oracle.winvd_fec_vto,
                winve_fec = oracle.winve_fec,
                ARDE_SUC = oracle.ARDE_SUC,
                winvd_art = oracle.winvd_art,
                art_desc = oracle.ART_DESC,
                winvd_lote = oracle.winvd_lote,
                winvd_area = oracle.winvd_area,
                area_desc = oracle.area_desc,
                winvd_dpto = oracle.winvd_dpto,
                dpto_desc = oracle.dpto_desc,
                winvd_secc = oracle.winvd_secc,
                secc_desc = oracle.secc_desc,
                winvd_flia = oracle.winvd_flia,
                flia_desc = oracle.flia_desc,
                winvd_grupo = oracle.winvd_grupo,
                grup_desc = oracle.grup_desc,
                winvd_subgr = oracle.sugr_codigo,
                estado = "A", // Activo por defecto
                WINVE_LOGIN_CERRADO_WEB = "", // Vacío por defecto
                tipo_toma = oracle.tipo_toma,
                winve_login = oracle.winve_login,
                winvd_consolidado = oracle.winvd_consolidado,
                desc_grupo_parcial = oracle.desc_grupo_parcial,
                desc_familia = oracle.desc_familia,
                winve_dep = oracle.winve_dep,
                winve_suc = oracle.winve_suc,
                toma_registro = oracle.toma,
                cod_barra = oracle.coba_codigo_barra,
                caja = oracle.caja,
                GRUESA = oracle.GRUESA,
                UNID_IND = oracle.UNID_IND,
                sucursal = oracle.SUC_DESC,
                deposito = oracle.DEP_DESC,
                stockVisible = oracle.WINVE_STOCK_VISIBLE ?: "N"
            )
        }
    }
    
    /**
     * Obtiene inventarios desde la base de datos local
     */
    fun getInventariosLocales(): Flow<List<InventarioDetalle>> {
        return inventarioDetalleDao.getAllInventariosDetalle()
    }
    
    /**
     * Obtiene inventarios por criterios específicos
     */
    fun getInventariosPorCriterios(
        sucursal: String? = null,
        deposito: String? = null,
        area: Int? = null,
        departamento: Int? = null,
        seccion: Int? = null,
        familia: Int? = null,
        grupo: Int? = null,
        subgrupo: Int? = null,
        estado: String? = null,
        usuario: String? = null
    ): Flow<List<InventarioDetalle>> {
        return inventarioDetalleDao.getInventariosDetalleByMultiplesCriterios(
            sucursal = sucursal,
            deposito = deposito,
            area = area,
            departamento = departamento,
            seccion = seccion,
            familia = familia,
            grupo = grupo,
            subgrupo = subgrupo,
            estado = estado,
            usuario = usuario
        )
    }
    
    /**
     * Obtiene el total de inventarios locales
     */
    suspend fun getTotalInventariosLocales(): Int {
        return inventarioDetalleDao.getTotalInventariosDetalle()
    }

    /**
     * Limpia todos los inventarios locales
     */
    suspend fun limpiarInventariosLocales() {
        inventarioDetalleDao.deleteAllInventariosDetalle()
    }
}
