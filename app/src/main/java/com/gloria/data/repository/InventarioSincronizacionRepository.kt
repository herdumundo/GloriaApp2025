package com.gloria.data.repository

import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.entity.InventarioDetalle
import com.gloria.data.model.InventarioSincronizacion
import com.gloria.util.ConnectionOracle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * Repositorio para la sincronización de inventarios desde Oracle a Room
 */
class InventarioSincronizacionRepository @Inject constructor(
    private val inventarioDetalleDao: InventarioDetalleDao
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
            emit(Result.success(0))
            
            // 🚀 Iniciando sincronización
            onProgressUpdate("🔄 Iniciando sincronización de inventarios...", 0, 0)
            
            // 📊 Obtener inventarios desde Oracle
            val inventariosOracle = obtenerInventariosDesdeOracle(onProgressUpdate)
            
            if (inventariosOracle.isEmpty()) {
                onProgressUpdate("✅ No se encontraron inventarios para sincronizar", 0, 0)
                emit(Result.success(0))
                return@flow
            }
            
            // 🗄️ Limpiar tabla local antes de insertar
            onProgressUpdate("🧹 Limpiando tabla local...", 0, inventariosOracle.size)
            inventarioDetalleDao.deleteAllInventariosDetalle()
            
            // 💾 Insertar inventarios en Room
            val totalInsertados = insertarInventariosEnRoom(inventariosOracle, onProgressUpdate)
            
            // ✅ Sincronización completada
            onProgressUpdate("✅ Sincronización completada exitosamente", totalInsertados, totalInsertados)
            emit(Result.success(totalInsertados))
            
        } catch (e: Exception) {
            // 💥 Error en sincronización
            onProgressUpdate("💥 Error en sincronización: ${e.message}", 0, 0)
            emit(Result.failure(e))
        }
    }
    
    /**
     * Obtiene inventarios desde Oracle usando el query de sincronización
     */
    private suspend fun obtenerInventariosDesdeOracle(
        onProgressUpdate: (String, Int, Int) -> Unit
    ): List<InventarioSincronizacion> = withContext(Dispatchers.IO) {
        
        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        
        try {
            // 🔌 Conectar a Oracle
            connection = ConnectionOracle.getConnection()
            statement = connection?.createStatement()
            
            // ⏱️ Configurar timeout
            statement?.queryTimeout = 30
            
            // 📝 Query de sincronización
            val sqlQuery = """
                SELECT  
                    'A' AS toma,
                    b.WINVD_CANT_ACT as invd_cant_inv,
                    ART_DESC,
                    ARDE_SUC,
                    winvd_nro_inv,
                    winvd_art,
                    b.WINVD_LOTE AS winvd_lote,
                    b.winvd_fec_vto AS winvd_fec_vto,
                    winvd_area,
                    winvd_dpto,
                    winvd_secc,
                    winvd_flia,
                    winvd_grupo,
                    b.WINVD_CANT_ACT AS winvd_cant_act,
                    winve_fec,
                    dpto_desc,
                    secc_desc,
                    flia_desc,
                    grup_desc,
                    area_desc,
                    sugr_codigo,
                    winvd_secu AS winvd_secu,
                    CASE 
                        WHEN c.winve_tipo_toma = 'C' THEN 'CRITERIO'
                        ELSE 'MANUAL' 
                    END AS tipo_toma,
                    winve_login,
                    '' AS winvd_consolidado,
                    CASE 
                        WHEN c.winve_grupo IS NULL AND c.winve_grupo_parcial IS NULL THEN 'TODOS'
                        WHEN c.winve_grupo_parcial IS NOT NULL THEN 'PARCIALES' 
                        ELSE grup_desc 
                    END AS desc_grupo_parcial,
                    CASE 
                        WHEN c.winve_flia IS NULL THEN 'TODAS'
                        ELSE a.flia_desc 
                    END AS desc_familia,
                    winve_dep,
                    winve_suc,
                    a.coba_codigo_barra,
                    a.caja,
                    a.GRUESA,
                    a.UNID_IND,
                    suc.SUC_DESC,
                    suc.DEP_DESC,
                    c.WINVE_STOCK_VISIBLE
                FROM  
                    ADCS.V_WEB_ARTICULOS_CLASIFICACION a
                    INNER JOIN  ADCS.WEB_INVENTARIO_det b  ON a.ART_CODIGO = b.winvd_art  AND a.SECC_CODIGO = b.winvd_secc
                    INNER JOIN  ADCS.WEB_INVENTARIO c  ON b.winvd_nro_inv = c.winve_numero 
                        AND c.winve_dep = a.ARDE_DEP 
                        AND c.winve_area = a.AREA_CODIGO 
                        AND c.winve_suc = a.ARDE_SUC 
                        AND c.winve_secc = a.SECC_CODIGO
                    INNER JOIN  ADCS.V_WEB_SUC_DEP suc  ON  suc.SUC_CODIGO = a.ARDE_SUC
                        AND suc.DEP_CODIGO = a.ARDE_DEP
                WHERE 
                    c.winve_empr = 1 
                    AND a.ARDE_SUC = 1 
                    AND c.WINVE_ESTADO_WEB = 'A' 
                GROUP BY 
                    ARDE_SUC, winvd_nro_inv,winvd_art,winvd_area,winvd_dpto,winvd_secc,winve_suc, 
                    winvd_flia, winvd_grupo,winve_fec,dpto_desc,secc_desc,flia_desc,grup_desc, 
                    area_desc, sugr_codigo,winve_grupo,winve_tipo_toma,winve_login,winve_grupo_parcial, 
                    winve_flia, winve_dep,ART_DESC,a.coba_codigo_barra,a.caja,a.GRUESA,
                    a.UNID_IND,suc.SUC_DESC,suc.DEP_DESC,b.WINVD_LOTE,b.winvd_fec_vto,winvd_secu,
                    c.WINVE_STOCK_VISIBLE, b.WINVD_CANT_ACT
            """.trimIndent()
            
            // 🚀 Ejecutar query
            onProgressUpdate("🔍 Consultando inventarios en Oracle...", 0, 0)
            resultSet = statement?.executeQuery(sqlQuery)
            
            // 📊 Procesar resultados
            val inventarios = mutableListOf<InventarioSincronizacion>()
            var contador = 0
            
            while (resultSet!!.next()) {
                val inventario = InventarioSincronizacion(
                    toma = resultSet!!.getString("toma") ?: "A",
                    invd_cant_inv = resultSet!!.getInt("invd_cant_inv"),
                    ART_DESC = resultSet!!.getString("ART_DESC") ?: "",
                    ARDE_SUC = resultSet!!.getInt("ARDE_SUC"),
                    winvd_nro_inv = resultSet!!.getInt("winvd_nro_inv"),
                    winvd_art = resultSet!!.getString("winvd_art") ?: "",
                    winvd_lote = resultSet!!.getString("winvd_lote") ?: "",
                    winvd_fec_vto = resultSet!!.getString("winvd_fec_vto") ?: "",
                    winvd_area = resultSet!!.getInt("winvd_area"),
                    winvd_dpto = resultSet!!.getInt("winvd_dpto"),
                    winvd_secc = resultSet!!.getInt("winvd_secc"),
                    winvd_flia = resultSet!!.getInt("winvd_flia"),
                    winvd_grupo = resultSet!!.getInt("winvd_grupo"),
                    winvd_cant_act = resultSet!!.getInt("winvd_cant_act"),
                    winve_fec = resultSet!!.getString("winve_fec") ?: "",
                    dpto_desc = resultSet!!.getString("dpto_desc") ?: "",
                    secc_desc = resultSet!!.getString("secc_desc") ?: "",
                    flia_desc = resultSet!!.getString("flia_desc") ?: "",
                    grup_desc = resultSet!!.getString("grup_desc") ?: "",
                    area_desc = resultSet!!.getString("area_desc") ?: "",
                    sugr_codigo = resultSet!!.getInt("sugr_codigo"),
                    winvd_secu = resultSet!!.getInt("winvd_secu"),
                    tipo_toma = resultSet!!.getString("tipo_toma") ?: "MANUAL",
                    winve_login = resultSet!!.getString("winve_login") ?: "",
                    winvd_consolidado = resultSet!!.getString("winvd_consolidado") ?: "",
                    desc_grupo_parcial = resultSet!!.getString("desc_grupo_parcial") ?: "",
                    desc_familia = resultSet!!.getString("desc_familia") ?: "",
                    winve_dep = resultSet!!.getString("winve_dep") ?: "",
                    winve_suc = resultSet!!.getString("winve_suc") ?: "",
                    coba_codigo_barra = resultSet!!.getString("coba_codigo_barra") ?: "",
                    caja = resultSet!!.getInt("caja"),
                    GRUESA = resultSet!!.getInt("GRUESA"),
                    UNID_IND = resultSet!!.getInt("UNID_IND"),
                    SUC_DESC = resultSet!!.getString("SUC_DESC") ?: "",
                    DEP_DESC = resultSet!!.getString("DEP_DESC") ?: "",
                    WINVE_STOCK_VISIBLE = resultSet!!.getString("WINVE_STOCK_VISIBLE") ?: "N"
                )
                
                inventarios.add(inventario)
                contador++
                
                // 📊 Actualizar progreso cada 100 registros
                if (contador % 100 == 0) {
                    onProgressUpdate("📊 Procesando inventarios... ($contador registros)", contador, 0)
                }
            }
            
            onProgressUpdate("✅ Inventarios obtenidos desde Oracle: $contador registros", contador, 0)
            inventarios
            
        } catch (e: Exception) {
            throw Exception("Error al obtener inventarios desde Oracle: ${e.message}")
        } finally {
            // 🧹 Cerrar recursos
            resultSet?.close()
            statement?.close()
            connection?.close()
        }
    }
    
    /**
     * Inserta inventarios en la base de datos local Room
     */
    private suspend fun insertarInventariosEnRoom(
        inventarios: List<InventarioSincronizacion>,
        onProgressUpdate: (String, Int, Int) -> Unit
    ): Int {
        
        val inventariosRoom = inventarios.map { oracle ->
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
                stockVisible = oracle.WINVE_STOCK_VISIBLE ?: "N" // Valor por defecto "N" si es null
            )
        }
        
        // 💾 Insertar en lotes para mejor performance
        val loteSize = 100
        var totalInsertados = 0
        
        for (i in inventariosRoom.indices step loteSize) {
            val lote = inventariosRoom.subList(i, minOf(i + loteSize, inventariosRoom.size))
            
            try {
                val ids = inventarioDetalleDao.insertInventariosDetalle(lote)
                totalInsertados += ids.size
                
                onProgressUpdate("💾 Insertando en Room... ($totalInsertados/${inventariosRoom.size})", totalInsertados, inventariosRoom.size)
                
            } catch (e: Exception) {
                throw Exception("Error al insertar lote en Room: ${e.message}")
            }
        }
        
        return totalInsertados
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
