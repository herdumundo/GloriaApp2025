package com.gloria.data.dao

import com.gloria.data.model.ArticuloToma
import com.gloria.util.ConnectionOracle
import javax.inject.Inject

class ArticuloTomaDaoImpl @Inject constructor() : ArticuloTomaDao {
    override suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma> {
        val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
        
        return try {
            val sql = """
                SELECT 
                    WINVD_SECU,
                    WINVD_ART,
                    ART_DESC,
                    WINVD_LOTE,
                    ARDE_FEC_VTO_LOTE,
                    FLIA_DESC,
                    GRUP_DESC,
                    SUGR_DESC
                FROM WEB_INVENTARIO_DET
                WHERE WINVD_NRO_INV = ?
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, nroToma)
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<ArticuloToma>()
                    while (rs.next()) {
                        result.add(
                            ArticuloToma(
                                winvdSecu = rs.getString("WINVD_SECU"),
                                winvdArt = rs.getString("WINVD_ART"),
                                artDesc = rs.getString("ART_DESC"),
                                winvdLote = rs.getString("WINVD_LOTE"),
                                ardeFecVtoLote = rs.getString("ARDE_FEC_VTO_LOTE"),
                                fliaDesc = rs.getString("FLIA_DESC"),
                                grupDesc = rs.getString("GRUP_DESC"),
                                sugrDesc = rs.getString("SUGR_DESC")
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