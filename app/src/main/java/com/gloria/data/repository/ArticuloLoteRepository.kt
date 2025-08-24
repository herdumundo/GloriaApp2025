package com.gloria.data.repository

import com.gloria.data.model.ArticuloLote
import com.gloria.util.ConnectionOracle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import android.util.Log

class ArticuloLoteRepository {
    
    suspend fun getArticulosLotes(
        subgruposSeleccionados: List<Pair<Int, Int>>, // (grupCodigo, sugrCodigo)
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String,
        isFamiliaTodos: Boolean = false
    ): Flow<List<ArticuloLote>> = flow {
        Log.d("ArticuloLoteRepository", "🎯 MÉTODO getArticulosLotes() LLAMADO")
        Log.d("ArticuloLoteRepository", "📞 Llamada desde ViewModel recibida")
        val articulosLotes = mutableListOf<ArticuloLote>()
        var connection: java.sql.Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            Log.d("ArticuloLoteRepository", "🔄 INICIANDO consulta Oracle...")
            Log.d("ArticuloLoteRepository", "📊 Parámetros: suc=$sucursal, dep=$deposito, area=$area, dpto=$departamento, secc=$seccion, flia=$familia, isFamiliaTodos=$isFamiliaTodos")
            
            // Obtener conexión a Oracle
            Log.d("ArticuloLoteRepository", "🔌 Obteniendo conexión a Oracle...")
            connection = ConnectionOracle.getConnection()
            if (connection == null) {
                Log.e("ArticuloLoteRepository", "❌ No se pudo establecer conexión a Oracle")
                emit(articulosLotes)
                return@flow
            }
            Log.d("ArticuloLoteRepository", "✅ Conexión a Oracle establecida exitosamente")
            
            // Configurar timeout de conexión para evitar que se cuelgue
            // Nota: setNetworkTimeout no está disponible en esta versión de JDBC
            // Usaremos solo el timeout del statement
            
            // Construir la consulta SQL según si es familia todos o no
            val sql = if (isFamiliaTodos) {
                """
                SELECT  
                    CONCAT(CONCAT(grup_codigo, '#'),SUGR_CODIGO) as concatID,
                    TO_NUMBER (arde_cant_act) as cantidad, 
                    TO_CHAR(arde_fec_vto_lote,'DD-MM-YYYY') as vencimiento, 
                    flia_codigo, GRUP_CODIGO, GRUP_desc, flia_desc, art_desc, 
                    arde_lote, art_codigo, ARDE_FEC_VTO_LOTE, sugr_codigo, sugr_desc
                FROM ADCS.v_web_articulos_clasificacion 
                WHERE arde_suc=? and arde_dep=? and area_codigo=? 
                    and dpto_codigo=? and secc_codigo=?
                ORDER BY flia_desc, GRUP_desc, sugr_desc, art_desc ASC
                """.trimIndent()
            } else {
                // Construir la consulta SQL con placeholders para subgrupos específicos
                val subgruposPlaceholders = subgruposSeleccionados.joinToString(",") { "'${it.first}#${it.second}'" }
                val gruposCodigos = subgruposSeleccionados.map { it.first }.distinct().joinToString(",")
                
                """
                SELECT  
                    CONCAT(CONCAT(grup_codigo, '#'),SUGR_CODIGO) as concatID,
                    TO_NUMBER (arde_cant_act) as cantidad, 
                    TO_CHAR(arde_fec_vto_lote,'DD-MM-YYYY') as vencimiento, 
                    flia_codigo, GRUP_CODIGO, GRUP_desc, flia_desc, art_desc, 
                    arde_lote, art_codigo, ARDE_FEC_VTO_LOTE, sugr_codigo, sugr_desc
                FROM ADCS.v_web_articulos_clasificacion 
                WHERE CONCAT(CONCAT(grup_codigo, '#'),SUGR_CODIGO) in ($subgruposPlaceholders)
                    and arde_suc=? and arde_dep=? and area_codigo=? 
                    and dpto_codigo=? and secc_codigo=? 
                    and grup_codigo in ($gruposCodigos) and flia_codigo=?
                ORDER BY flia_desc, GRUP_desc, sugr_desc, art_desc ASC
                """.trimIndent()
            }
            
            Log.d("ArticuloLoteRepository", "📝 SQL Query construida:")
            Log.d("ArticuloLoteRepository", sql)
            
            // Preparar y ejecutar la consulta según el tipo de consulta
            Log.d("ArticuloLoteRepository", "🔧 Preparando statement...")
            statement = connection.prepareStatement(sql)
            
            // Configurar timeout de statement para evitar consultas infinitas
            statement.setQueryTimeout(30) // 30 segundos
            Log.d("ArticuloLoteRepository", "⏱️ Timeout configurado: 30 segundos")
            
            if (isFamiliaTodos) {
                // Consulta sin filtro de grupo/subgrupo ni familia (5 parámetros)
                Log.d("ArticuloLoteRepository", "🔢 Configurando 5 parámetros para consulta 'Todas las familias'")
                statement.setInt(1, sucursal)
                statement.setInt(2, deposito)
                statement.setInt(3, area)
                statement.setInt(4, departamento)
                statement.setInt(5, seccion)
            } else {
                // Consulta con filtro de grupo/subgrupo y familia (6 parámetros)
                Log.d("ArticuloLoteRepository", "🔢 Configurando 6 parámetros para consulta específica")
                statement.setInt(1, sucursal)
                statement.setInt(2, deposito)
                statement.setInt(3, area)
                statement.setInt(4, departamento)
                statement.setInt(5, seccion)
                statement.setString(6, familia)
            }
            
            Log.d("ArticuloLoteRepository", "🚀 Ejecutando consulta Oracle...")
            resultSet = statement.executeQuery()
            Log.d("ArticuloLoteRepository", "✅ Consulta ejecutada exitosamente, procesando resultados...")
            
            // Contador para monitorear el progreso
            var contador = 0
            val maxResultados = if (isFamiliaTodos) 1000 else 5000 // Límite para consultas masivas
            
            Log.d("ArticuloLoteRepository", "📊 Límite de resultados configurado: $maxResultados")
            
            // Mapear resultados con límite y progreso
            while (resultSet.next() && contador < maxResultados) {
                try {
                    val articulo = ArticuloLote(
                        concatID = resultSet.getString("concatID") ?: "",
                        cantidad = resultSet.getDouble("cantidad"),
                        vencimiento = resultSet.getString("vencimiento") ?: "",
                        fliaCodigo = resultSet.getString("flia_codigo") ?: "",
                        grupCodigo = resultSet.getInt("grup_codigo"),
                        grupDesc = resultSet.getString("grup_desc") ?: "",
                        fliaDesc = resultSet.getString("flia_desc") ?: "",
                        artDesc = resultSet.getString("art_desc") ?: "",
                        ardeLote = resultSet.getString("arde_lote") ?: "",
                        artCodigo = resultSet.getString("art_codigo") ?: "",
                        ardeFecVtoLote = resultSet.getString("arde_fec_vto_lote") ?: "",
                        sugrCodigo = resultSet.getInt("sugr_codigo"),
                        sugrDesc = resultSet.getString("sugr_desc") ?: ""
                    )
                    articulosLotes.add(articulo)
                    contador++
                    
                    // Log de progreso cada 100 registros
                    if (contador % 100 == 0) {
                        Log.d("ArticuloLoteRepository", "📈 Progreso: $contador registros procesados...")
                    }
                    
                } catch (e: Exception) {
                    Log.e("ArticuloLoteRepository", "❌ Error al mapear artículo: ${e.message}", e)
                    // Continuar con el siguiente artículo
                }
            }
            
            // Verificar si se alcanzó el límite
            if (contador >= maxResultados) {
                Log.w("ArticuloLoteRepository", "⚠️ Se alcanzó el límite de $maxResultados resultados. Total procesados: $contador")
            }
            
            Log.d("ArticuloLoteRepository", "🎯 PROCESAMIENTO COMPLETADO:")
            Log.d("ArticuloLoteRepository", "   • Total de registros procesados: $contador")
            Log.d("ArticuloLoteRepository", "   • Artículos mapeados exitosamente: ${articulosLotes.size}")
            Log.d("ArticuloLoteRepository", "   • Límite configurado: $maxResultados")
            
            Log.d("ArticuloLoteRepository", "📤 Enviando ${articulosLotes.size} artículos al ViewModel...")
            
            // Enviar los resultados al ViewModel
            try {
                emit(articulosLotes)
                Log.d("ArticuloLoteRepository", "✅ Artículos enviados exitosamente al ViewModel")
            } catch (e: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al enviar artículos: ${e.message}", e)
                // Re-emitir en caso de error para mantener la transparencia del Flow
                emit(articulosLotes)
            }
            
        } catch (e: Exception) {
            Log.e("ArticuloLoteRepository", "💥 ERROR CRÍTICO en consulta Oracle: ${e.message}", e)
            Log.e("ArticuloLoteRepository", "💥 Stack trace completo:", e)
            // Emitir lista vacía en caso de error para evitar que se cuelgue
            emit(emptyList<ArticuloLote>())
        } finally {
            // Cerrar recursos
            Log.d("ArticuloLoteRepository", "🧹 Cerrando recursos de base de datos...")
            try {
                resultSet?.close()
                statement?.close()
                connection?.close()
                Log.d("ArticuloLoteRepository", "✅ Recursos cerrados exitosamente")
            } catch (e: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al cerrar recursos: ${e.message}")
            }
        }
    }
}
