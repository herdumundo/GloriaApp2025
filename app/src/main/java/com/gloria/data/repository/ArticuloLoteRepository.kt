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
        isFamiliaTodos: Boolean = false,
        onProgressUpdate: ((current: Int, total: Int) -> Unit)? = null
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
            
            // Preparar variables para la consulta
            val subgruposPlaceholders = if (!isFamiliaTodos) {
                subgruposSeleccionados.joinToString(",") { "'${it.first}#${it.second}'" }
            } else ""
            
            val gruposCodigos = if (!isFamiliaTodos) {
                subgruposSeleccionados.map { it.first }.distinct().joinToString(",")
            } else ""
            
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
            
            // 🔍 PRIMERO: Obtener el conteo total de registros
            Log.d("ArticuloLoteRepository", "📊 Obteniendo conteo total de registros...")
            
            // Crear consulta COUNT para obtener el total real
            val sqlCount = if (isFamiliaTodos) {
                """
                SELECT COUNT(*) as total FROM ADCS.v_web_articulos_clasificacion 
                WHERE arde_suc=? and arde_dep=? and area_codigo=? 
                and dpto_codigo=? and secc_codigo=?
                """.trimIndent()
            } else {
                """
                SELECT COUNT(*) as total FROM ADCS.v_web_articulos_clasificacion 
                WHERE arde_suc=? and arde_dep=? and area_codigo=? 
                and dpto_codigo=? and secc_codigo=? 
                and grup_codigo in ($gruposCodigos) and flia_codigo=?
                """.trimIndent()
            }
            
            val countStatement = connection.prepareStatement(sqlCount)
            countStatement.setQueryTimeout(30)
            
            if (isFamiliaTodos) {
                countStatement.setInt(1, sucursal)
                countStatement.setInt(2, deposito)
                countStatement.setInt(3, area)
                countStatement.setInt(4, departamento)
                countStatement.setInt(5, seccion)
            } else {
                countStatement.setInt(1, sucursal)
                countStatement.setInt(2, deposito)
                countStatement.setInt(3, area)
                countStatement.setInt(4, departamento)
                countStatement.setInt(5, seccion)
                countStatement.setString(6, familia)
            }
            
            val countResultSet = countStatement.executeQuery()
            countResultSet.next()
            val totalRegistros = countResultSet.getInt("total")
            countResultSet.close()
            countStatement.close()
            
            Log.d("ArticuloLoteRepository", "📊 Total de registros encontrados: $totalRegistros")
            
            // 🔄 SEGUNDO: Ejecutar consulta principal para procesar
            Log.d("ArticuloLoteRepository", "🚀 Ejecutando consulta principal para procesar registros...")
            resultSet = statement.executeQuery()
            Log.d("ArticuloLoteRepository", "✅ Consulta ejecutada exitosamente, procesando resultados...")
            
            // Notificar progreso inicial
            onProgressUpdate?.invoke(0, totalRegistros)
            
            // Contador para monitorear el progreso
            var contador = 0
            Log.d("ArticuloLoteRepository", "📊 Procesando $totalRegistros registros con progreso real...")
            
            // Mapear TODOS los resultados con progreso real
            while (resultSet.next()) {
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
                        Log.d("ArticuloLoteRepository", "📈 Progreso: $contador/$totalRegistros registros procesados...")
                        // Notificar progreso real al ViewModel
                        onProgressUpdate?.invoke(contador, totalRegistros)
                    }
                    
                } catch (e: Exception) {
                    Log.e("ArticuloLoteRepository", "❌ Error al mapear artículo: ${e.message}", e)
                    // Continuar con el siguiente artículo
                }
            }
            
            // Notificar progreso final con total real
            onProgressUpdate?.invoke(contador, totalRegistros)
            
            Log.d("ArticuloLoteRepository", "🎯 PROCESAMIENTO COMPLETADO:")
            Log.d("ArticuloLoteRepository", "   • Total de registros encontrados: $totalRegistros")
            Log.d("ArticuloLoteRepository", "   • Total de registros procesados: $contador")
            Log.d("ArticuloLoteRepository", "   • Artículos mapeados exitosamente: ${articulosLotes.size}")
            
            Log.d("ArticuloLoteRepository", "📤 Enviando ${articulosLotes.size} artículos al ViewModel...")
            
            // Enviar los resultados al ViewModel
            emit(articulosLotes)
            Log.d("ArticuloLoteRepository", "✅ Artículos enviados exitosamente al ViewModel")
            
        } catch (e: Exception) {
            // Verificar si es un aborto normal del Flow (no es un error real)
            if (e.message?.contains("Flow was aborted") == true) {
                Log.d("ArticuloLoteRepository", "ℹ️ Flow abortado normalmente por .first() - esto es esperado")
            } else {
                Log.e("ArticuloLoteRepository", "💥 ERROR CRÍTICO en consulta Oracle: ${e.message}", e)
                Log.e("ArticuloLoteRepository", "💥 Stack trace completo:", e)
            }
            // ❌ NO emitir en catch - esto viola la transparencia del Flow
            // Los errores se manejan en el ViewModel
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
    
    /**
     * Inserta la cabecera del inventario en Oracle y retorna el ID generado
     */
    suspend fun insertarCabeceraInventario(
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String?,
        subgruposSeleccionados: List<Pair<Int, Int>>,
        isFamiliaTodos: Boolean,
        userdb: String,
        inventarioVisible: Boolean
    ): Int {
        var connection: java.sql.Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            Log.d("ArticuloLoteRepository", "🚀 Iniciando inserción de cabecera del inventario...")
            
            // Conectar a Oracle
            connection = ConnectionOracle.getConnection()
            if (connection == null) {
                throw Exception("No se pudo establecer conexión a Oracle")
            }
            Log.d("ArticuloLoteRepository", "✅ Conexión Oracle establecida")
            
            // 🔍 PRIMERO: Obtener el siguiente ID de la secuencia
            Log.d("ArticuloLoteRepository", "🔍 Obteniendo siguiente ID de la secuencia SEQ_NRO_INV...")
            
            val sqlSecuencia = "SELECT SEQ_NRO_INV.NEXTVAL FROM DUAL"
            statement = connection.prepareStatement(sqlSecuencia)
            statement.setQueryTimeout(30)
            
            resultSet = statement.executeQuery()
            resultSet.next()
            val idCabecera = resultSet.getInt(1)
            resultSet.close()
            statement.close()
            
            Log.d("ArticuloLoteRepository", "✅ ID de cabecera obtenido: $idCabecera")
            
            // 🔄 SEGUNDO: Preparar los valores para la inserción
            val idGrupo = if (!isFamiliaTodos && subgruposSeleccionados.isNotEmpty()) {
                subgruposSeleccionados.first().first.toString()
            } else ""
            
            val idFamilia = if (!isFamiliaTodos && familia != null) familia else ""
            
            val gruposParcial = if (!isFamiliaTodos && subgruposSeleccionados.isNotEmpty()) {
                subgruposSeleccionados.map { it.first }.distinct().joinToString(",")
            } else ""
            
            Log.d("ArticuloLoteRepository", "📊 Valores preparados para inserción:")
            Log.d("ArticuloLoteRepository", "   • ID Cabecera: $idCabecera")
            Log.d("ArticuloLoteRepository", "   • ID Grupo: '$idGrupo'")
            Log.d("ArticuloLoteRepository", "   • ID Familia: '$idFamilia'")
            Log.d("ArticuloLoteRepository", "   • Grupos Parcial: '$gruposParcial'")
            
            // 🚀 TERCERO: Insertar la cabecera
            val sqlInsert = """
                INSERT INTO ADCS.WEB_INVENTARIO(
                    WINVE_SUC,                          
                    WINVE_DEP,                       
                    WINVE_GRUPO,                 
                    WINVE_FEC,       
                    WINVE_LOGIN,
                    WINVE_TIPO_TOMA,                    
                    WINVE_SECC,                      
                    WINVE_AREA,                  
                    WINVE_DPTO,      
                    WINVE_FLIA,
                    WINVE_IND_LOTE,
                    WINVE_ESTADO,
                    WINVE_ART_EST,
                    WINVE_ART_EXIST,
                    WINVE_CANT_TOMA,
                    WINVE_EMPR,
                    WINVE_NUMERO,
                    WINVE_ESTADO_WEB,
                    WINVE_CONSOLIDADO,
                    WINVE_GRUPO_PARCIAL,
                    WINVE_STOCK_VISIBLE,
                ) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'M', ?, ?, ?, ?, 'S', 'A', 'S', 'N', '1', '1', ?, 'A', 'N', ?, ?)
            """.trimIndent()
            
            Log.d("ArticuloLoteRepository", "📝 SQL INSERT preparado:")
            Log.d("ArticuloLoteRepository", sqlInsert)
            
            statement = connection.prepareStatement(sqlInsert)
            statement.setQueryTimeout(30)
            
            // Configurar parámetros
            statement.setInt(1, sucursal)
            statement.setInt(2, deposito)
            statement.setString(3, idGrupo)
            statement.setString(4, userdb.uppercase())
            statement.setInt(5, seccion)
            statement.setInt(6, area)
            statement.setInt(7, departamento)
            statement.setString(8, idFamilia)
            statement.setInt(9, idCabecera)
            statement.setString(10, gruposParcial)
            statement.setString(11, if (inventarioVisible) "Y" else "N")
            
            Log.d("ArticuloLoteRepository", "🔢 Parámetros configurados:")
            Log.d("ArticuloLoteRepository", "   • Sucursal: $sucursal")
            Log.d("ArticuloLoteRepository", "   • Depósito: $deposito")
            Log.d("ArticuloLoteRepository", "   • Grupo: '$idGrupo'")
            Log.d("ArticuloLoteRepository", "   • Usuario: ${userdb.uppercase()}")
            Log.d("ArticuloLoteRepository", "   • Sección: $seccion")
            Log.d("ArticuloLoteRepository", "   • Área: $area")
            Log.d("ArticuloLoteRepository", "   • Departamento: $departamento")
            Log.d("ArticuloLoteRepository", "   • Familia: '$idFamilia'")
            Log.d("ArticuloLoteRepository", "   • Número: $idCabecera")
            Log.d("ArticuloLoteRepository", "   • Grupos Parcial: '$gruposParcial'")
            Log.d("ArticuloLoteRepository", "   • Inventario Visible: ${if (inventarioVisible) "Y" else "N"}")
            
            // Ejecutar inserción
            val filasInsertadas = statement.executeUpdate()
            
            if (filasInsertadas == 1) {
                Log.d("ArticuloLoteRepository", "✅ Cabecera del inventario insertada exitosamente")
                Log.d("ArticuloLoteRepository", "🎯 ID de cabecera generado: $idCabecera")
                
                // Confirmar transacción
                connection!!.commit()
                Log.d("ArticuloLoteRepository", "✅ Transacción confirmada")
                
                return idCabecera
            } else {
                throw Exception("Error al insertar cabecera: se insertaron $filasInsertadas filas en lugar de 1")
            }
            
        } catch (e: Exception) {
            Log.e("ArticuloLoteRepository", "💥 ERROR al insertar cabecera del inventario: ${e.message}", e)
            
            // Revertir transacción en caso de error
            try {
                connection?.rollback()
                Log.d("ArticuloLoteRepository", "🔄 Transacción revertida por error")
            } catch (rollbackError: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al revertir transacción: ${rollbackError.message}")
            }
            
            throw e
        } finally {
            // Cerrar recursos
            Log.d("ArticuloLoteRepository", "🧹 Cerrando recursos de inserción...")
            try {
                resultSet?.close()
                statement?.close()
                connection?.close()
                Log.d("ArticuloLoteRepository", "✅ Recursos de inserción cerrados exitosamente")
            } catch (e: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al cerrar recursos de inserción: ${e.message}")
            }
        }
    }
    
    /**
     * Inserta el detalle del inventario en Oracle
     */
    suspend fun insertarDetalleInventario(
        idCabecera: Int,
        articulosSeleccionados: List<ArticuloLote>,
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        onProgressUpdate: ((current: Int, total: Int) -> Unit)? = null
    ): Int {
        var connection: java.sql.Connection? = null
        var statement: PreparedStatement? = null
        
        try {
            Log.d("ArticuloLoteRepository", "🚀 Iniciando inserción de detalle del inventario...")
            Log.d("ArticuloLoteRepository", "📊 Total de artículos a insertar: ${articulosSeleccionados.size}")
            
            // Conectar a Oracle
            connection = ConnectionOracle.getConnection()
            if (connection == null) {
                throw Exception("No se pudo establecer conexión a Oracle")
            }
            Log.d("ArticuloLoteRepository", "✅ Conexión Oracle establecida")
            
            // Preparar la consulta SQL para el detalle
            val sqlInsertDetalle = """
                INSERT INTO ADCS.WEB_INVENTARIO_DET (
                    WINVD_NRO_INV,
                    WINVD_ART,
                    WINVD_SECU,
                    WINVD_CANT_ACT,
                    WINVD_CANT_INV,
                    WINVD_UBIC,
                    WINVD_CODIGO_BARRA,
                    WINVD_CANT_PED_RECEP,
                    WINVD_LOTE,
                    WINVD_FEC_VTO,
                    WINVD_LOTE_CLAVE,
                    WINVD_UM,
                    WINVD_AREA,
                    WINVD_DPTO,
                    WINVD_SECC,
                    WINVD_FLIA,
                    WINVD_GRUPO,
                    WINVD_SUBGR,
                    WINVD_INDIV,
                    WINVD_CONSOLIDADO
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            
            Log.d("ArticuloLoteRepository", "📝 SQL INSERT detalle preparado:")
            Log.d("ArticuloLoteRepository", sqlInsertDetalle)
            
            statement = connection.prepareStatement(sqlInsertDetalle)
            statement.setQueryTimeout(30)
            
            var secuencia = 1
            var totalInsertados = 0
            
            // Insertar cada artículo con su secuencia
            for (articulo in articulosSeleccionados) {
                try {
                    // Configurar parámetros para cada artículo
                    statement.setInt(1, idCabecera)                    // WINVD_NRO_INV
                    statement.setString(2, articulo.artCodigo)           // WINVD_ART
                    statement.setInt(3, secuencia)                       // WINVD_SECU
                    statement.setDouble(4, articulo.cantidad)            // WINVD_CANT_ACT
                    statement.setString(5, "")                           // WINVD_CANT_INV (vacío)
                    statement.setString(6, "")                           // WINVD_UBIC (vacío)
                    statement.setString(7, "")                           // WINVD_CODIGO_BARRA (vacío)
                    statement.setString(8, "")                           // WINVD_CANT_PED_RECEP (vacío)
                    statement.setString(9, articulo.ardeLote)            // WINVD_LOTE
                    
                    // Convertir fecha de vencimiento al formato correcto
                    val fechaVto = if (articulo.vencimiento.isNotEmpty()) {
                        articulo.vencimiento // Formato: DD-MM-YYYY
                    } else {
                        "31-12-5000" // Fecha por defecto si no hay vencimiento
                    }
                    statement.setString(10, fechaVto)                    // WINVD_FEC_VTO
                    
                    statement.setString(11, "")                          // WINVD_LOTE_CLAVE (vacío)
                    statement.setString(12, "")                          // WINVD_UM (vacío)
                    statement.setInt(13, area)                           // WINVD_AREA
                    statement.setInt(14, departamento)                   // WINVD_DPTO
                    statement.setInt(15, seccion)                        // WINVD_SECC
                    statement.setString(16, articulo.fliaCodigo)         // WINVD_FLIA
                    statement.setInt(17, articulo.grupCodigo)            // WINVD_GRUPO
                    statement.setInt(18, articulo.sugrCodigo)            // WINVD_SUBGR
                    statement.setString(19, "")                          // WINVD_INDIV (vacío)
                    statement.setString(20, "N")                         // WINVD_CONSOLIDADO
                    
                    // Ejecutar inserción
                    val filasInsertadas = statement.executeUpdate()
                    
                    if (filasInsertadas == 1) {
                        totalInsertados++
                        Log.d("ArticuloLoteRepository", "✅ Artículo insertado: ${articulo.artDesc} (Secuencia: $secuencia)")
                    } else {
                        Log.w("ArticuloLoteRepository", "⚠️ Error al insertar artículo: ${articulo.artDesc}")
                    }
                    
                    // Incrementar secuencia para el siguiente artículo
                    secuencia++
                    
                    // Notificar progreso cada 10 artículos
                    if (totalInsertados % 10 == 0) {
                        onProgressUpdate?.invoke(totalInsertados, articulosSeleccionados.size)
                        Log.d("ArticuloLoteRepository", "📈 Progreso detalle: $totalInsertados/${articulosSeleccionados.size} artículos insertados...")
                    }
                    
                } catch (e: Exception) {
                    Log.e("ArticuloLoteRepository", "❌ Error al insertar artículo ${articulo.artDesc}: ${e.message}", e)
                    // Continuar con el siguiente artículo
                }
            }
            
            // Notificar progreso final
            onProgressUpdate?.invoke(totalInsertados, articulosSeleccionados.size)
            
            Log.d("ArticuloLoteRepository", "🎯 DETALLE DEL INVENTARIO COMPLETADO:")
            Log.d("ArticuloLoteRepository", "   • Total de artículos procesados: ${articulosSeleccionados.size}")
            Log.d("ArticuloLoteRepository", "   • Total de artículos insertados exitosamente: $totalInsertados")
            Log.d("ArticuloLoteRepository", "   • Secuencia final: ${secuencia - 1}")
            
            // Confirmar transacción
            connection!!.commit()
            Log.d("ArticuloLoteRepository", "✅ Transacción de detalle confirmada")
            
            return totalInsertados
            
        } catch (e: Exception) {
            Log.e("ArticuloLoteRepository", "💥 ERROR al insertar detalle del inventario: ${e.message}", e)
            
            // Revertir transacción en caso de error
            try {
                connection?.rollback()
                Log.d("ArticuloLoteRepository", "🔄 Transacción de detalle revertida por error")
            } catch (rollbackError: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al revertir transacción de detalle: ${rollbackError.message}")
            }
            
            throw e
        } finally {
            // Cerrar recursos
            Log.d("ArticuloLoteRepository", "🧹 Cerrando recursos de detalle...")
            try {
                statement?.close()
                connection?.close()
                Log.d("ArticuloLoteRepository", "✅ Recursos de detalle cerrados exitosamente")
            } catch (e: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al cerrar recursos de detalle: ${e.message}")
            }
        }
    }
    
    /**
     * Inserta la cabecera y detalle del inventario en una sola transacción
     * Garantiza integridad de datos: si falla el detalle, se revierte la cabecera
     */
    suspend fun insertarCabeceraYDetalleInventario(
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String?,
        subgruposSeleccionados: List<Pair<Int, Int>>,
        isFamiliaTodos: Boolean,
        userdb: String,
        inventarioVisible: Boolean,
        articulosSeleccionados: List<ArticuloLote>,
        onProgressUpdate: ((current: Int, total: Int) -> Unit)? = null
    ): Pair<Int, Int> {
        var connection: java.sql.Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            Log.d("ArticuloLoteRepository", "🚀 Iniciando inserción de cabecera y detalle en transacción única...")
            
            // Conectar a Oracle
            connection = ConnectionOracle.getConnection()
            if (connection == null) {
                throw Exception("No se pudo establecer conexión a Oracle")
            }
            
            // Desactivar auto-commit para manejar transacciones manualmente
            connection.autoCommit = false
            Log.d("ArticuloLoteRepository", "✅ Conexión Oracle establecida con transacción manual")
            
            // 🔍 PRIMERO: Obtener el siguiente ID de la secuencia
            Log.d("ArticuloLoteRepository", "🔍 Obteniendo siguiente ID de la secuencia SEQ_NRO_INV...")
            
            val sqlSecuencia = "SELECT SEQ_NRO_INV.NEXTVAL FROM DUAL"
            statement = connection.prepareStatement(sqlSecuencia)
            statement.setQueryTimeout(30)
            
            resultSet = statement.executeQuery()
            resultSet.next()
            val idCabecera = resultSet.getInt(1)
            resultSet.close()
            statement.close()
            
            Log.d("ArticuloLoteRepository", "✅ ID de cabecera obtenido: $idCabecera")
            
            // 🔄 SEGUNDO: Preparar los valores para la inserción de cabecera
            val idGrupo = if (!isFamiliaTodos && subgruposSeleccionados.isNotEmpty()) {
                subgruposSeleccionados.first().first.toString()
            } else ""
            
            val idFamilia = if (!isFamiliaTodos && familia != null) familia else ""
            
            val gruposParcial = if (!isFamiliaTodos && subgruposSeleccionados.isNotEmpty()) {
                subgruposSeleccionados.map { it.first }.distinct().joinToString(",")
            } else ""
            
            Log.d("ArticuloLoteRepository", "📊 Valores preparados para inserción de cabecera:")
            Log.d("ArticuloLoteRepository", "   • ID Cabecera: $idCabecera")
            Log.d("ArticuloLoteRepository", "   • ID Grupo: '$idGrupo'")
            Log.d("ArticuloLoteRepository", "   • ID Familia: '$idFamilia'")
            Log.d("ArticuloLoteRepository", "   • Grupos Parcial: '$gruposParcial'")
            
            // 🚀 TERCERO: Insertar la cabecera
            val sqlInsertCabecera = """
                INSERT INTO ADCS.WEB_INVENTARIO(
                    WINVE_SUC,                          
                    WINVE_DEP,                       
                    WINVE_GRUPO,                 
                    WINVE_FEC,       
                    WINVE_LOGIN,
                    WINVE_TIPO_TOMA,                    
                    WINVE_SECC,                      
                    WINVE_AREA,                  
                    WINVE_DPTO,      
                    WINVE_FLIA,
                    WINVE_IND_LOTE,
                    WINVE_ESTADO,
                    WINVE_ART_EST,
                    WINVE_ART_EXIST,
                    WINVE_CANT_TOMA,
                    WINVE_EMPR,
                    WINVE_NUMERO,
                    WINVE_ESTADO_WEB,
                    WINVE_CONSOLIDADO,
                    WINVE_GRUPO_PARCIAL,
                    WINVE_STOCK_VISIBLE
                ) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'M', ?, ?, ?, ?, 'S', 'A', 'S', 'N', '1', '1', ?, 'A', 'N', ?, ?)
            """.trimIndent()
            
            Log.d("ArticuloLoteRepository", "📝 SQL INSERT cabecera preparado:")
            Log.d("ArticuloLoteRepository", sqlInsertCabecera)
            
            statement = connection.prepareStatement(sqlInsertCabecera)
            statement.setQueryTimeout(30)
            
            // Configurar parámetros de cabecera
            statement.setInt(1, sucursal)
            statement.setInt(2, deposito)
            statement.setString(3, idGrupo)
            statement.setString(4, userdb.uppercase())
            statement.setInt(5, seccion)
            statement.setInt(6, area)
            statement.setInt(7, departamento)
            statement.setString(8, idFamilia)
            statement.setInt(9, idCabecera)
            statement.setString(10, gruposParcial)
            statement.setString(11, if (inventarioVisible) "Y" else "N")
            
            Log.d("ArticuloLoteRepository", "🔢 Parámetros de cabecera configurados:")
            Log.d("ArticuloLoteRepository", "   • Sucursal: $sucursal")
            Log.d("ArticuloLoteRepository", "   • Depósito: $deposito")
            Log.d("ArticuloLoteRepository", "   • Grupo: '$idGrupo'")
            Log.d("ArticuloLoteRepository", "   • Usuario: ${userdb.uppercase()}")
            Log.d("ArticuloLoteRepository", "   • Sección: $seccion")
            Log.d("ArticuloLoteRepository", "   • Área: $area")
            Log.d("ArticuloLoteRepository", "   • Departamento: $departamento")
            Log.d("ArticuloLoteRepository", "   • Familia: '$idFamilia'")
            Log.d("ArticuloLoteRepository", "   • Número: $idCabecera")
            Log.d("ArticuloLoteRepository", "   • Grupos Parcial: '$gruposParcial'")
            Log.d("ArticuloLoteRepository", "   • Inventario Visible: ${if (inventarioVisible) "Y" else "N"}")
            
            // Ejecutar inserción de cabecera
            val filasInsertadasCabecera = statement.executeUpdate()
            
            if (filasInsertadasCabecera != 1) {
                throw Exception("Error al insertar cabecera: se insertaron $filasInsertadasCabecera filas en lugar de 1")
            }
            
            Log.d("ArticuloLoteRepository", "✅ Cabecera del inventario insertada exitosamente")
            Log.d("ArticuloLoteRepository", "🎯 ID de cabecera generado: $idCabecera")
            
            // 🔄 CUARTO: Insertar el detalle
            Log.d("ArticuloLoteRepository", "🚀 Iniciando inserción de detalle del inventario...")
            Log.d("ArticuloLoteRepository", "📊 Total de artículos a insertar: ${articulosSeleccionados.size}")
            
            // Preparar la consulta SQL para el detalle
            val sqlInsertDetalle = """
                INSERT INTO ADCS.WEB_INVENTARIO_DET (
                    WINVD_NRO_INV,
                    WINVD_ART,
                    WINVD_SECU,
                    WINVD_CANT_ACT,
                    WINVD_CANT_INV,
                    WINVD_UBIC,
                    WINVD_CODIGO_BARRA,
                    WINVD_CANT_PED_RECEP,
                    WINVD_LOTE,
                    WINVD_FEC_VTO,
                    WINVD_LOTE_CLAVE,
                    WINVD_UM,
                    WINVD_AREA,
                    WINVD_DPTO,
                    WINVD_SECC,
                    WINVD_FLIA,
                    WINVD_GRUPO,
                    WINVD_SUBGR,
                    WINVD_INDIV,
                    WINVD_CONSOLIDADO
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            
            Log.d("ArticuloLoteRepository", "📝 SQL INSERT detalle preparado:")
            Log.d("ArticuloLoteRepository", sqlInsertDetalle)
            
            statement = connection.prepareStatement(sqlInsertDetalle)
            statement.setQueryTimeout(30)
            
            var secuencia = 1
            var totalInsertados = 0
            
            // Insertar cada artículo con su secuencia
            for (articulo in articulosSeleccionados) {
                try {
                    // Configurar parámetros para cada artículo
                    statement.setInt(1, idCabecera)                    // WINVD_NRO_INV
                    statement.setString(2, articulo.artCodigo)           // WINVD_ART
                    statement.setInt(3, secuencia)                       // WINVD_SECU
                    statement.setDouble(4, articulo.cantidad)            // WINVD_CANT_ACT
                    statement.setString(5, "")                           // WINVD_CANT_INV (vacío)
                    statement.setString(6, "")                           // WINVD_UBIC (vacío)
                    statement.setString(7, "")                           // WINVD_CODIGO_BARRA (vacío)
                    statement.setString(8, "")                           // WINVD_CANT_PED_RECEP (vacío)
                    statement.setString(9, articulo.ardeLote)            // WINVD_LOTE
                    
                    // Convertir fecha de vencimiento al formato correcto
                    val fechaVto = if (articulo.vencimiento.isNotEmpty()) {
                        articulo.vencimiento // Formato: DD-MM-YYYY
                    } else {
                        "31-12-5000" // Fecha por defecto si no hay vencimiento
                    }
                    statement.setString(10, fechaVto)                    // WINVD_FEC_VTO
                    
                    statement.setString(11, "")                          // WINVD_LOTE_CLAVE (vacío)
                    statement.setString(12, "")                          // WINVD_UM (vacío)
                    statement.setInt(13, area)                           // WINVD_AREA
                    statement.setInt(14, departamento)                   // WINVD_DPTO
                    statement.setInt(15, seccion)                        // WINVD_SECC
                    statement.setString(16, articulo.fliaCodigo)         // WINVD_FLIA
                    statement.setInt(17, articulo.grupCodigo)            // WINVD_GRUPO
                    statement.setInt(18, articulo.sugrCodigo)            // WINVD_SUBGR
                    statement.setString(19, "")                          // WINVD_INDIV (vacío)
                    statement.setString(20, "N")                         // WINVD_CONSOLIDADO
                    
                    // Ejecutar inserción
                    val filasInsertadas = statement.executeUpdate()
                    
                    if (filasInsertadas == 1) {
                        totalInsertados++
                        Log.d("ArticuloLoteRepository", "✅ Artículo insertado: ${articulo.artDesc} (Secuencia: $secuencia)")
                    } else {
                        Log.w("ArticuloLoteRepository", "⚠️ Error al insertar artículo: ${articulo.artDesc}")
                    }
                    
                    // Incrementar secuencia para el siguiente artículo
                    secuencia++
                    
                    // Notificar progreso cada 10 artículos
                    if (totalInsertados % 10 == 0) {
                        onProgressUpdate?.invoke(totalInsertados, articulosSeleccionados.size)
                        Log.d("ArticuloLoteRepository", "📈 Progreso detalle: $totalInsertados/${articulosSeleccionados.size} artículos insertados...")
                    }
                    
                } catch (e: Exception) {
                    Log.e("ArticuloLoteRepository", "❌ Error al insertar artículo ${articulo.artDesc}: ${e.message}", e)
                    // Continuar con el siguiente artículo
                }
            }
            
            // Notificar progreso final
            onProgressUpdate?.invoke(totalInsertados, articulosSeleccionados.size)
            
            Log.d("ArticuloLoteRepository", "🎯 DETALLE DEL INVENTARIO COMPLETADO:")
            Log.d("ArticuloLoteRepository", "📊 Total de artículos procesados: ${articulosSeleccionados.size}")
            Log.d("ArticuloLoteRepository", "📊 Total de artículos insertados exitosamente: $totalInsertados")
            Log.d("ArticuloLoteRepository", "📊 Secuencia final: ${secuencia - 1}")
            
            // ✅ CONFIRMAR TRANSACCIÓN COMPLETA
            connection.commit()
            Log.d("ArticuloLoteRepository", "✅ TRANSACCIÓN COMPLETA CONFIRMADA (Cabecera + Detalle)")
            
            return Pair(idCabecera, totalInsertados)
            
        } catch (e: Exception) {
            Log.e("ArticuloLoteRepository", "💥 ERROR en transacción completa: ${e.message}", e)
            
            // 🔄 REVERTIR TRANSACCIÓN COMPLETA en caso de error
            try {
                connection?.rollback()
                Log.d("ArticuloLoteRepository", "🔄 TRANSACCIÓN COMPLETA REVERTIDA por error")
            } catch (rollbackError: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al revertir transacción completa: ${rollbackError.message}")
            }
            
            throw e
        } finally {
            // Cerrar recursos
            Log.d("ArticuloLoteRepository", "🧹 Cerrando recursos de transacción completa...")
            try {
                resultSet?.close()
                statement?.close()
                connection?.close()
                Log.d("ArticuloLoteRepository", "✅ Recursos de transacción completa cerrados exitosamente")
            } catch (e: Exception) {
                Log.e("ArticuloLoteRepository", "❌ Error al cerrar recursos de transacción completa: ${e.message}")
            }
        }
    }
}
