package com.gloria.data.dao

import android.util.Log
import com.gloria.data.model.CancelacionToma
import com.gloria.util.ConnectionOracle
import com.gloria.domain.usecase.AuthSessionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CancelacionTomaDaoImpl @Inject constructor(
    private val authSessionUseCase: AuthSessionUseCase
) : CancelacionTomaDao {
    override suspend fun getCancelacionesToma(userLogin: String): List<CancelacionToma> {
        return withContext(Dispatchers.IO) {
            Log.d("PROCESO_LOGIN", "=== INICIANDO getCancelacionesToma ===")
            Log.d("PROCESO_LOGIN", "🔄 Hilo actual: ${Thread.currentThread().name}")
            Log.d("PROCESO_LOGIN", "👤 UserLogin: $userLogin")
            
            val connection = ConnectionOracle.getConnection(authSessionUseCase) ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
                Log.d("PROCESO_LOGIN", "✅ Conexión obtenida exitosamente")
            val sql = """
                SELECT 
                    NVL(NVL(grup_desc,WINVE_GRUPO_PARCIAL),'TODOS') AS desc_grupos,
                    to_char(WEB_INVENTARIO.Winve_Fec,'DD/MM/YYYY HH:SS') AS FECHAFORM,
                    WEB_INVENTARIO.*,
                    V_WEB_GRUPO.*,
                    V_WEB_area.*,
                    V_WEB_SECC.*,
                    V_WEB_DPTO.*,
                    NVL(V_WEB_FLIA.flia_desc,'TODAS') AS flia_desc,
                    case WEB_INVENTARIO.winve_tipo_toma 
                        when 'C' then 'CRITERIO' 
                        ELSE 'MANUAL' 
                    END AS tipo_toma,
                    CASE winve_consolidado 
                        WHEN 'S' THEN 'SI' 
                        ELSE 'NO' 
                    END AS consolidado,
                    case when winve_grupo IS NULL and winve_grupo_parcial IS NULL then 'TODOS'
                         WHEN winve_grupo_parcial IS NOT NULL THEN 'PARCIALES' 
                         ELSE grup_desc 
                    END AS desc_grupo_parcial
                FROM WEB_INVENTARIO
                LEFT OUTER JOIN V_WEB_FLIA on WEB_INVENTARIO.WINVE_FLIA=V_WEB_FLIA.FLIA_CODIGO
                LEFT OUTER JOIN V_WEB_GRUPO on WEB_INVENTARIO.WINVE_GRUPO=V_WEB_GRUPO.GRUP_CODIGO 
                    AND V_WEB_FLIA.FLIA_CODIGO=V_WEB_GRUPO.GRUP_FAMILIA
                INNER JOIN V_WEB_area on V_WEB_area.AREA_CODIGO=WEB_INVENTARIO.Winve_Area
                INNER JOIN V_WEB_SECC on V_WEB_SECC.SECC_CODIGO=WEB_INVENTARIO.winve_secc
                INNER JOIN V_WEB_DPTO on V_WEB_DPTO.DPTO_CODIGO=WEB_INVENTARIO.winve_DPTO
                WHERE WINVE_ESTADO_WEB='A' 
                AND WEB_INVENTARIO.WINVE_LOGIN=UPPER(?)
                ORDER BY WINVE_NUMERO DESC
            """.trimIndent()
            
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, userLogin.uppercase())
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<CancelacionToma>()
                    while (rs.next()) {
                        result.add(
                            CancelacionToma(
                                winveNumero = try { rs.getInt("WINVE_NUMERO") } catch (e: Exception) { 0 },
                                fechaForm = rs.getString("FECHAFORM") ?: "",
                                tipoToma = rs.getString("tipo_toma") ?: "",
                                consolidado = rs.getString("consolidado") ?: "",
                                fliaDesc = rs.getString("flia_desc") ?: "",
                                grupDesc = rs.getString("desc_grupos") ?: "",
                                sugrDesc = rs.getString("desc_grupo_parcial") ?: "",
                                areaDesc = rs.getString("AREA_DESC") ?: "",
                                seccDesc = rs.getString("SECC_DESC") ?: "",
                                dptoDesc = rs.getString("DPTO_DESC") ?: "",
                                winveArea = rs.getString("WINVE_AREA") ?: "",
                                winveSecc = rs.getString("WINVE_SECC") ?: "",
                                winveDpto = rs.getString("WINVE_DPTO") ?: "",
                                winveFlia = rs.getString("WINVE_FLIA") ?: "",
                                winveGrupo = rs.getString("WINVE_GRUPO") ?: "",
                                winveGrupoParcial = rs.getString("WINVE_GRUPO_PARCIAL") ?: "",
                                winveTipoToma = rs.getString("WINVE_TIPO_TOMA") ?: "",
                                winveConsolidado = rs.getString("WINVE_CONSOLIDADO") ?: "",
                                winveEstadoWeb = rs.getString("WINVE_ESTADO_WEB") ?: "",
                                winveLogin = rs.getString("WINVE_LOGIN") ?: ""
                            )
                        )
                    }
                    Log.d("PROCESO_LOGIN", "📊 Resultados obtenidos: ${result.size} cancelaciones")
                    result
                }
            }
            } catch (e: Exception) {
                Log.e("PROCESO_LOGIN", "❌ Error en getCancelacionesToma: ${e.message}")
                throw Exception("Error al obtener las tomas para cancelar: ${e.message}")
            } finally {
                connection.close()
                Log.d("PROCESO_LOGIN", "🔚 Finalizando getCancelacionesToma")
            }
        }
    }

    override suspend fun cancelarTomaParcial(nroToma: Int, secuencias: List<String>): Int {
        return withContext(Dispatchers.IO) {
            val connection = ConnectionOracle.getConnection(authSessionUseCase) ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
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
    }

    override suspend fun cancelarTomaTotal(nroToma: Int, userLogin: String): Int {
        return withContext(Dispatchers.IO) {
            val connection = ConnectionOracle.getConnection(authSessionUseCase) ?: throw Exception("No se pudo conectar a la base de datos")
            
            try {
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
}