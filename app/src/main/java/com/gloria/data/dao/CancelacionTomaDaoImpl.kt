package com.gloria.data.dao

import com.gloria.data.model.CancelacionToma
import com.gloria.util.ConnectionOracle
import javax.inject.Inject

class CancelacionTomaDaoImpl @Inject constructor() : CancelacionTomaDao {
    override suspend fun getCancelacionesToma(userLogin: String): List<CancelacionToma> {
        val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
        
        return try {
            val sql = """
                SELECT 
                    WINVE_NUMERO,
                    TO_CHAR(WINVE_FECHA, 'DD/MM/YYYY') as FECHA_FORM,
                    WINVE_TIPO_TOMA,
                    WINVE_CONSOLIDADO,
                    FLIA_DESC,
                    GRUP_DESC,
                    SUGR_DESC
                FROM WEB_INVENTARIO
                WHERE WINVE_LOGIN = ?
                AND WINVE_ESTADO_WEB = 'A'
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, userLogin)
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<CancelacionToma>()
                    while (rs.next()) {
                        result.add(
                            CancelacionToma(
                                winveNumero = rs.getInt("WINVE_NUMERO"),
                                fechaForm = rs.getString("FECHA_FORM"),
                                tipoToma = rs.getString("WINVE_TIPO_TOMA"),
                                consolidado = rs.getString("WINVE_CONSOLIDADO"),
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
            throw Exception("Error al obtener las tomas para cancelar: ${e.message}")
        } finally {
            connection.close()
        }
    }

    override suspend fun cancelarTomaParcial(nroToma: Int, secuencias: List<String>): Int {
        val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
        
        return try {
            val secuenciasDelimitadasPorComas = secuencias.joinToString(",")
            val sql = "DELETE FROM WEB_INVENTARIO_DET WHERE WINVd_NRO_inv = ? AND WINVD_SECU IN ($secuenciasDelimitadasPorComas)"
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, nroToma)
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            throw Exception("Error al cancelar la toma parcialmente: ${e.message}")
        } finally {
            connection.close()
        }
    }

    override suspend fun cancelarTomaTotal(nroToma: Int, userLogin: String): Int {
        val connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
        
        return try {
            val sql = """
                UPDATE WEB_INVENTARIO 
                SET WINVE_ESTADO_WEB = 'E',
                    WINVE_FEC_CERRADO_WEB = CURRENT_TIMESTAMP,
                    WINVE_LOGIN_CERRADO_WEB = UPPER(?)
                WHERE WINVE_NUMERO = ?
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, userLogin)
                stmt.setInt(2, nroToma)
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            throw Exception("Error al cancelar la toma totalmente: ${e.message}")
        } finally {
            connection.close()
        }
    }
}