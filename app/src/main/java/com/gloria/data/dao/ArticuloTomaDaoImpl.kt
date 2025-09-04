package com.gloria.data.dao

import com.gloria.data.model.ArticuloToma
import com.gloria.util.ConnectionOracle
import javax.inject.Inject

class ArticuloTomaDaoImpl @Inject constructor() : ArticuloTomaDao {
    override suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma> {
        val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
        
        return try {
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
                    result
                }
            }
        } catch (e: Exception) {
            throw Exception("Error al obtener los artículos de la toma: ${e.message}")
        } finally {
            connection.close()
        }
    }
}