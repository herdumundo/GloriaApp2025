package com.gloria.data.dao

import android.util.Log
import com.gloria.data.model.ArticuloToma
import com.gloria.data.repository.DetalleInventarioExportar
import com.gloria.domain.usecase.exportacion.InventarioPendienteExportar
import com.gloria.util.ConnectionOracle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticuloTomaDaoImpl @Inject constructor() : ArticuloTomaDao {
    override suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma> {
        return withContext(Dispatchers.IO) {
            Log.d("PROCESO_LOGIN", "=== INICIANDO getArticulosToma ===")
            Log.d("PROCESO_LOGIN", "🔄 Hilo actual: ${Thread.currentThread().name}")
            Log.d("PROCESO_LOGIN", "🔢 NroToma: $nroToma")
            
            val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
                Log.d("PROCESO_LOGIN", "✅ Conexión obtenida exitosamente")
            val sql = """
                SELECT DISTINCT
                    b.WINVD_SECU,
                    to_char(a.ARDE_FEC_VTO_LOTE) as ARDE_FEC_VTO_LOTE,
                    b.winvd_fec_vto,
                    a.ARDE_SUC,
                    b.winvd_nro_inv,
                    b.winvd_art,
                    a.ART_DESC,
                    b.winvd_lote,
                    b.winvd_fec_vto,
                    b.winvd_area,
                    b.winvd_dpto,
                    b.winvd_secc,
                    b.winvd_flia,
                    b.winvd_grupo,
                    b.winvd_cant_act,
                    c.winve_fec,
                    dpto_desc,
                    secc_desc,
                    flia_desc,
                    grup_desc,
                    area_desc,
                    sugr_desc
                FROM ADCS.V_WEB_ARTICULOS_CLASIFICACION a
                INNER JOIN ADCS.WEB_INVENTARIO_det b 
                    ON a.arde_lote = b.winvd_lote 
                    AND a.ART_CODIGO = b.winvd_art
                    AND a.SECC_CODIGO = b.winvd_secc 
                    AND a.ARDE_FEC_VTO_LOTE = b.winvd_fec_vto
                INNER JOIN ADCS.WEB_INVENTARIO c 
                    ON b.winvd_nro_inv = c.winve_numero
                    AND c.winve_dep = a.ARDE_DEP 
                    AND c.winve_area = a.AREA_CODIGO
                    AND c.winve_suc = a.ARDE_SUC 
                    AND c.winve_secc = a.SECC_CODIGO
                WHERE c.WINVE_NUMERO = ?
                ORDER BY WINVD_SECU ASC
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, nroToma)
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<ArticuloToma>()
                    while (rs.next()) {
                                            result.add(
                        ArticuloToma(
                            winvdSecu = rs.getString("WINVD_SECU") ?: "",
                            winvdArt = rs.getString("WINVD_ART") ?: "",
                            artDesc = rs.getString("ART_DESC") ?: "",
                            winvdLote = rs.getString("WINVD_LOTE") ?: "",
                            ardeFecVtoLote = rs.getString("ARDE_FEC_VTO_LOTE") ?: "",
                            fliaDesc = rs.getString("flia_desc") ?: "",
                            grupDesc = rs.getString("grup_desc") ?: "",
                            sugrDesc = rs.getString("sugr_desc") ?: "",
                            winvdFecVto = rs.getString("winvd_fec_vto") ?: "",
                            ardeSuc = rs.getString("ARDE_SUC") ?: "",
                            winvdNroInv = rs.getString("winvd_nro_inv") ?: "",
                            winvdArea = rs.getString("winvd_area") ?: "",
                            winvdDpto = rs.getString("winvd_dpto") ?: "",
                            winvdSecc = rs.getString("winvd_secc") ?: "",
                            winvdFlia = rs.getString("winvd_flia") ?: "",
                            winvdGrupo = rs.getString("winvd_grupo") ?: "",
                            winvdCantAct = rs.getString("winvd_cant_act") ?: "",
                            winveFec = rs.getString("winve_fec") ?: "",
                            dptoDesc = rs.getString("dpto_desc") ?: "",
                            seccDesc = rs.getString("secc_desc") ?: "",
                            areaDesc = rs.getString("area_desc") ?: ""
                        )
                    )
                    }
                    Log.d("PROCESO_LOGIN", "📊 Resultados obtenidos: ${result.size} artículos")
                    result
                }
            }
            } catch (e: Exception) {
                Log.e("PROCESO_LOGIN", "❌ Error en getArticulosToma: ${e.message}")
                throw Exception("Error al obtener los artículos de la toma: ${e.message}")
            } finally {
                connection.close()
                Log.d("PROCESO_LOGIN", "🔚 Finalizando getArticulosToma")
            }
        }
    }

    override suspend fun getInventariosPendientesExportar(
        idSucursal: String,
        userLogin: String
    ): List<InventarioPendienteExportar> {
        return withContext(Dispatchers.IO) {
            val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
            val sql = """
                SELECT DISTINCT 
                    winvd_nro_inv,
                    WINVE_LOGIN_CERRADO_WEB,
                    winve_dep,
                    arde_suc,
                    tipo_toma,
                    toma_registro,
                    winve_fec,
                    winve_sucursal
                FROM ADCS.STKW002INV 
                WHERE arde_suc = ? 
                AND estado IN ('P','F') 
                AND UPPER(WINVE_LOGIN_CERRADO_WEB) = UPPER(?)
                ORDER BY winvd_nro_inv ASC
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, idSucursal)
                stmt.setString(2, userLogin)
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<InventarioPendienteExportar>()
                    while (rs.next()) {
                        result.add(
                            InventarioPendienteExportar(
                                winvdNroInv = rs.getInt("winvd_nro_inv"),
                                winveLoginCerradoWeb = rs.getString("WINVE_LOGIN_CERRADO_WEB") ?: "",
                                winveDep = rs.getInt("winve_dep"),
                                ardeSuc = rs.getInt("arde_suc"),
                                tipoToma = rs.getString("tipo_toma") ?: "",
                                tomaRegistro = rs.getString("toma_registro") ?: "",
                                winveFecha = rs.getString("winve_fec") ?: "",
                                winveSucursal = rs.getString("winve_sucursal") ?: ""
                            )
                        )
                    }
                    result
                }
            }
            } catch (e: Exception) {
                throw Exception("Error al obtener inventarios pendientes de exportar: ${e.message}")
            } finally {
                connection.close()
            }
        }
    }

    override suspend fun getDetallesInventario(nroInventario: Int): List<DetalleInventarioExportar> {
        return withContext(Dispatchers.IO) {
            val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
            val sql = """
                SELECT 
                    winvd_nro_inv,
                    winvd_lote,
                    winvd_art,
                    TO_CHAR(winvd_fec_vto, 'DD/MM/YYYY') as winvd_fec_vto,
                    winvd_area,
                    winvd_dpto,
                    winvd_secc,
                    winvd_flia,
                    winvd_grupo,
                    winvd_cant_act,
                    winvd_cant_inv,
                    winvd_secu,
                    winve_dep,
                    winve_suc,
                    caja,
                    gruesa,
                    unid_ind,
                    art_desc,
                    area_desc,
                    dpto_desc,
                    secc_desc,
                    flia_desc,
                    grup_desc,
                    winvd_subgr,
                    estado,
                    WINVE_LOGIN_CERRADO_WEB,
                    tipo_toma,
                    winve_login,
                    winvd_consolidado,
                    desc_grupo_parcial,
                    desc_familia,
                    winve_fec,
                    toma_registro,
                    cod_barra,
                    sucursal,
                    deposito
                FROM ADCS.STKW002INV
                WHERE winvd_nro_inv = ?
                ORDER BY winvd_secu ASC
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, nroInventario)
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<DetalleInventarioExportar>()
                    while (rs.next()) {
                        result.add(
                            DetalleInventarioExportar(
                                winvdNroInv = rs.getInt("winvd_nro_inv"),
                                winvdLote = rs.getString("winvd_lote") ?: "",
                                winvdArt = rs.getString("winvd_art") ?: "",
                                winvdFecVto = rs.getString("winvd_fec_vto") ?: "",
                                winvdArea = rs.getString("winvd_area") ?: "",
                                winvdDpto = rs.getString("winvd_dpto") ?: "",
                                winvdSecc = rs.getString("winvd_secc") ?: "",
                                winvdFlia = rs.getString("winvd_flia") ?: "",
                                winvdGrupo = rs.getString("winvd_grupo") ?: "",
                                winvdCantAct = rs.getString("winvd_cant_act") ?: "",
                                winvdCantInv = rs.getString("winvd_cant_inv") ?: "",
                                winvdSecu = rs.getInt("winvd_secu"),
                                winveDep = rs.getInt("winve_dep"),
                                winveSuc = rs.getInt("winve_suc"),
                                caja = rs.getInt("caja"),
                                gruesa = rs.getInt("gruesa"),
                                unidInd = rs.getInt("unid_ind"),
                                artDesc = rs.getString("art_desc") ?: "",
                                areaDesc = rs.getString("area_desc") ?: "",
                                dptoDesc = rs.getString("dpto_desc") ?: "",
                                seccDesc = rs.getString("secc_desc") ?: "",
                                fliaDesc = rs.getString("flia_desc") ?: "",
                                grupDesc = rs.getString("grup_desc") ?: "",
                                winvdSubgr = rs.getInt("winvd_subgr"),
                                estado = rs.getString("estado") ?: "",
                                winveLoginCerradoWeb = rs.getString("WINVE_LOGIN_CERRADO_WEB") ?: "",
                                tipoToma = rs.getString("tipo_toma") ?: "",
                                winveLogin = rs.getString("winve_login") ?: "",
                                winvdConsolidado = rs.getString("winvd_consolidado") ?: "",
                                descGrupoParcial = rs.getString("desc_grupo_parcial") ?: "",
                                descFamilia = rs.getString("desc_familia") ?: "",
                                winveFec = rs.getString("winve_fec") ?: "",
                                tomaRegistro = rs.getString("toma_registro") ?: "",
                                codBarra = rs.getString("cod_barra") ?: "",
                                sucursal = rs.getString("sucursal") ?: "",
                                deposito = rs.getString("deposito") ?: ""
                            )
                        )
                    }
                    result
                }
            }
            } catch (e: Exception) {
                throw Exception("Error al obtener detalles del inventario: ${e.message}")
            } finally {
                connection.close()
            }
        }
    }

    override suspend fun marcarInventarioComoAnulado(nroInventario: Int) {
        withContext(Dispatchers.IO) {
            val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
            val sql = """
                UPDATE ADCS.STKW002INV 
                SET estado = 'E'
                WHERE winvd_nro_inv = ?
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, nroInventario)
                val rowsAffected = stmt.executeUpdate()
                if (rowsAffected == 0) {
                    throw Exception("No se encontró el inventario con número: $nroInventario")
                }
            }
            } catch (e: Exception) {
                throw Exception("Error al marcar inventario como anulado: ${e.message}")
            } finally {
                connection.close()
            }
        }
    }

    override suspend fun marcarInventarioComoCerrado(nroInventario: Int) {
        withContext(Dispatchers.IO) {
            val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
            val sql = """
                UPDATE ADCS.STKW002INV 
                SET estado = 'C'
                WHERE winvd_nro_inv = ?
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, nroInventario)
                val rowsAffected = stmt.executeUpdate()
                if (rowsAffected == 0) {
                    throw Exception("No se encontró el inventario con número: $nroInventario")
                }
            }
            } catch (e: Exception) {
                throw Exception("Error al marcar inventario como cerrado: ${e.message}")
            } finally {
                connection.close()
            }
        }
    }
}