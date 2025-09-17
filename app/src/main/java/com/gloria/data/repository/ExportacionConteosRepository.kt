package com.gloria.data.repository

import android.util.Log
import com.gloria.data.dao.ArticuloTomaDao
import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.repository.InventarioSincronizacionRepository
import com.gloria.domain.usecase.exportacion.InventarioPendienteExportar
import com.gloria.domain.usecase.enviarconteo.EnviarConteoVerificacionUseCase
import com.gloria.data.mapper.ConteoRequestMapper
import com.gloria.util.ConnectionOracle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.sql.Connection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar la exportación de conteos
 * Implementa la lógica de exportación de inventarios desde SQLite a Oracle
 */
@Singleton
class ExportacionConteosRepository @Inject constructor(
     private val inventarioDetalleDao: InventarioDetalleDao,
     private val enviarConteoVerificacionUseCase: EnviarConteoVerificacionUseCase
 ) {
    
    /**
     * Exporta conteos realizados (estado 'C')
     * Procesa inventarios con estado 'P' o 'F' y los marca como 'C' después de exportar
     */
    suspend fun exportarConteosRealizados(
        idSucursal: String,
        userLogin: String
    ): Result<String> {
        return try {
            Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Iniciando exportación de conteos realizados")
            Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Parámetros: idSucursal=$idSucursal, userLogin=$userLogin")
            
            // 1. Obtener inventarios pendientes de exportar desde SQLite
            Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Consultando inventarios pendientes desde SQLite...")
            val inventariosPendientes = obtenerInventariosPendientesDesdeSQLite(idSucursal, userLogin)
            
            Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Inventarios encontrados: ${inventariosPendientes.size}")
            
            if (inventariosPendientes.isEmpty()) {
                Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] No se encontraron registros por exportar")
                return Result.success("No se encontraron registros por exportar.")
            }
            
            var contadorExitosos = 0
            var mensajeError = ""
            
            // 2. Procesar cada inventario
            Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Procesando ${inventariosPendientes.size} inventarios...")
            for (inventario in inventariosPendientes) {
                try {
                    Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Procesando inventario #${inventario.winvdNroInv}")
                    
                    // Verificar si el inventario está anulado en el servidor
                    Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Verificando si inventario #${inventario.winvdNroInv} está anulado...")
                    val estaAnulado = verificarInventarioAnulado(inventario.winvdNroInv)
                    
                    if (estaAnulado) {
                        Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Inventario #${inventario.winvdNroInv} está anulado, marcando en SQLite...")
                        // Marcar como anulado en SQLite
                        inventarioDetalleDao.marcarInventarioComoAnulado(inventario.winvdNroInv)
                    } else {
                        Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Exportando inventario #${inventario.winvdNroInv}...")
                        // Exportar el inventario
                        exportarInventarioCompleto(inventario)
                        contadorExitosos++
                        Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Inventario #${inventario.winvdNroInv} exportado exitosamente")
                    }
                } catch (e: Exception) {
                    Log.d("EXPORTACIONES_LOG", "❌ [EXPORTACION] Error en inventario ${inventario.winvdNroInv}: ${e.message}")
                    mensajeError += "Error en inventario ${inventario.winvdNroInv}: ${e.message}\n"
                }
            }
            
            Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTACION] Procesamiento completado. Exitosos: $contadorExitosos")
            
            if (contadorExitosos > 0) {
                val mensaje = "Datos exportados con éxito. Procesados: $contadorExitosos inventarios."
                Log.d("EXPORTACIONES_LOG", "✅ [EXPORTACION] $mensaje")
                Result.success(mensaje)
            } else {
                val error = "No se pudo exportar ningún inventario. Errores: $mensajeError"
                Log.d("EXPORTACIONES_LOG", "❌ [EXPORTACION] $error")
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            val error = "Error al exportar conteos realizados: ${e.message}"
            Log.d("EXPORTACIONES_LOG", "❌ [EXPORTACION] $error")
            Result.failure(Exception(error))
        }
    }
    
    /**
     * Exporta conteos para verificación (mantiene estado 'P' o 'F')
     * Envía los datos sin cambiar el estado en SQLite
     */
    suspend fun exportarConteosParaVerificacion(
        idSucursal: String,
        userLogin: String
    ): Result<String> {
        return try {
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Iniciando envío de conteos para verificación")
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Parámetros: idSucursal=$idSucursal, userLogin=$userLogin")
            
            // 1. Obtener inventarios pendientes de exportar desde SQLite
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Consultando inventarios pendientes desde SQLite...")
            val inventariosPendientes = obtenerInventariosPendientesDesdeSQLite(idSucursal, userLogin)
            
            if (inventariosPendientes.isEmpty()) {
                return Result.success("No se encontraron registros por exportar.")
            }
            
            var contadorExitosos = 0
            var mensajeError = ""
            
            // 2. Procesar cada inventario (sin cambiar estados)
            for (inventario in inventariosPendientes) {
                try {
                    // Verificar si el inventario está anulado en el servidor
                    val estaAnulado = verificarInventarioAnulado(inventario.winvdNroInv)
                    
                    if (!estaAnulado) {
                        // Exportar solo para verificación (sin cambiar estados)
                        exportarInventarioParaVerificacion(inventario)
                        contadorExitosos++
                    }
                } catch (e: Exception) {
                    mensajeError += "Error en inventario ${inventario.winvdNroInv}: ${e.message}\n"
                }
            }
            
            if (contadorExitosos > 0) {
                Result.success("Conteos enviados para verificación. Procesados: $contadorExitosos inventarios.")
            } else {
                Result.failure(Exception("No se pudo enviar ningún inventario para verificación. Errores: $mensajeError"))
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("Error al exportar conteos para verificación: ${e.message}"))
        }
    }
    
    /**
     * Genera un nuevo ID para la cabecera STK
     */
    private suspend fun generarIdCabeceraStk(): Int {
        var connection: java.sql.Connection? = null
        
        try {
            connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            
            val sql = "SELECT SEQ_STK_CARGA_INV.NEXTVAL    FROM   DUAL"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(sql)
            
            val id = if (resultSet.next()) resultSet.getInt(1) else throw Exception("No se pudo generar ID")
            
            resultSet.close()
            statement.close()
            
            return id
            
        } catch (e: Exception) {
            Log.e("EXPORTACIONES_LOG", "Error al generar ID cabecera STK: ${e.message}")
            throw e
        } finally {
            connection?.close()
        }
    }

    /**
     * Inserta un inventario completo en una sola transacción Oracle
     * Incluye: cabecera STK, detalles STK y actualización de estado web
     */
    private suspend fun insertarInventarioCompletoEnTransaccion(
        idCabecera: Int,
        inventario: InventarioPendienteExportar,
        detalles: List<DetalleInventarioExportar>
    ) {
        var connection: java.sql.Connection? = null
        
        try {
            connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            connection.autoCommit = false
            
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Iniciando transacción única para inventario #${inventario.winvdNroInv}")
            
            // 1. Insertar cabecera STK
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Insertando cabecera STK...")
            val tipoToma = if (inventario.tipoToma == "MANUAL") "M" else "C"
            
            val sqlCabecera = """
                INSERT INTO ADCS.WEB_STK_CARGA_INV  
                (INVE_NUMERO, INVE_EMPR, INVE_SUC, INVE_DEP, INVE_FEC, INVE_CANT_TOMA, 
                 INVE_LOGIN, INVE_TIPO_TOMA, INVE_REF) 
                VALUES (?, '1', ?, ?, CURRENT_TIMESTAMP, '1', UPPER(?), ?, ?)
            """.trimIndent()
            
            val psCabecera = connection.prepareStatement(sqlCabecera)
            psCabecera.setInt(1, idCabecera)
            psCabecera.setInt(2, inventario.ardeSuc)
            psCabecera.setInt(3, inventario.winveDep)
            psCabecera.setString(4, inventario.winveLoginCerradoWeb)
            psCabecera.setString(5, tipoToma)
            psCabecera.setInt(6, inventario.winvdNroInv)
            
            val filasCabecera = psCabecera.executeUpdate()
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Cabecera insertada. Filas afectadas: $filasCabecera")
            psCabecera.close()
            
            // 2. Insertar detalles STK
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Insertando ${detalles.size} detalles STK...")

            for (detalle in detalles) {
                val sqlDetalle = """
                    INSERT INTO ADCS.WEB_STK_CARGA_INV_DET   
                    (INVD_NRO_INV, INVD_ART, INVD_SECU, INVD_CANT_INV, 
                     INVD_FEC_VTO, INVD_LOTE, INVD_UM) 
                    VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), ?, '')
                """.trimIndent()
                
                val psDetalle = connection.prepareStatement(sqlDetalle)
                psDetalle.setInt(1, idCabecera)
                psDetalle.setString(2, detalle.winvdArt)
                psDetalle.setInt(3, detalle.winvdSecu)
                psDetalle.setString(4, detalle.winvdCantInv)
                psDetalle.setString(5, detalle.winvdFecVto)
                psDetalle.setString(6, detalle.winvdLote)
                
                val filasDetalle = psDetalle.executeUpdate()
                Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Detalle ${detalle.winvdSecu} insertado. Filas afectadas: $filasDetalle")
                psDetalle.close()
                
             }
            
            // 3. Actualizar estado en web_inventario
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Actualizando estado en web_inventario...")
            val sqlWebInventario = """
                UPDATE ADCS.web_inventario 
                SET WINVE_ESTADO_WEB = 'C', WINVE_FEC_CERRADO_WEB = CURRENT_TIMESTAMP
                WHERE WINVE_NUMERO = ?
            """.trimIndent()
            
            val psWebInventario = connection.prepareStatement(sqlWebInventario)
            psWebInventario.setInt(1, inventario.winvdNroInv)
            
            val filasWebInventario = psWebInventario.executeUpdate()
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Estado web_inventario actualizado. Filas afectadas: $filasWebInventario")
            psWebInventario.close()
            
            // 4. Commit de toda la transacción
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Commit de toda la transacción...")
            connection.commit()
            Log.d("EXPORTACIONES_LOG", "✅ [TRANSACCION] Transacción completada exitosamente")
            
        } catch (e: Exception) {
            Log.d("EXPORTACIONES_LOG", "❌ [TRANSACCION] Error en transacción: ${e.message}")
            Log.d("EXPORTACIONES_LOG", "🔍 [TRANSACCION] Realizando rollback...")
            connection?.rollback()
            throw Exception("Error al insertar inventario completo en transacción: ${e.message}")
        } finally {
            connection?.close()
        }
    }

    /**
     * Exporta un inventario completo (cambia estado a 'C')
     */
    private suspend fun exportarInventarioCompleto(inventario: InventarioPendienteExportar) {
        Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTAR] Obteniendo detalles del inventario #${inventario.winvdNroInv} desde SQLite...")
        val detalles = obtenerDetallesInventarioDesdeSQLite(inventario.winvdNroInv)
        
        try {
            if (inventario.tomaRegistro == "R") {
                // Reinventario: actualizar cantidades existentes
                Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTAR] Procesando como reinventario...")
                actualizarInventarioReinventario(
                    numeroInventario = inventario.winvdNroInv,
                    detalles = detalles
                )
            } else {
                // Inventario nuevo: insertar cabecera y detalles en una sola transacción
                Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTAR] Procesando como inventario nuevo...")
                val idCabeceraStk = generarIdCabeceraStk()
                
                // Ejecutar todas las operaciones Oracle en una sola transacción
                insertarInventarioCompletoEnTransaccion(
                    idCabecera = idCabeceraStk,
                    inventario = inventario,
                    detalles = detalles
                )
            }
            
            // Solo si Oracle fue exitoso, marcar como cerrado en SQLite
            Log.d("EXPORTACIONES_LOG", "🔍 [EXPORTAR] Marcando inventario como cerrado en SQLite...")
            inventarioDetalleDao.marcarInventarioComoCerrado(inventario.winvdNroInv)
            Log.d("EXPORTACIONES_LOG", "✅ [EXPORTAR] Inventario #${inventario.winvdNroInv} exportado exitosamente")
            
        } catch (e: Exception) {
            Log.d("EXPORTACIONES_LOG", "❌ [EXPORTAR] Error al exportar inventario #${inventario.winvdNroInv}: ${e.message}")
            throw e // Re-lanzar para que se maneje en el nivel superior
        }
    }
    
    /**
     * Exporta un inventario solo para verificación usando la API STKW002INV
     * Envía los datos sin cambiar estados en SQLite
     */
    private suspend fun exportarInventarioParaVerificacion(inventario: InventarioPendienteExportar) {
        Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICAR] Obteniendo detalles del inventario #${inventario.winvdNroInv} desde SQLite...")
        val detalles = obtenerDetallesInventarioDesdeSQLite(inventario.winvdNroInv)
        
        // Convertir detalles a ConteoRequest usando el mapper
        val conteoRequests = ConteoRequestMapper.toConteoRequestListFromDetalleExportar(detalles)
        
        Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICAR] Enviando ${conteoRequests.size} registros a la API STKW002INV...")
        
        // Enviar a la API usando el use case
        val resultado = enviarConteoVerificacionUseCase(conteoRequests)
        
        resultado.fold(
            onSuccess = { response ->
                Log.d("EXPORTACIONES_LOG", "✅ [VERIFICAR] API respondió exitosamente:")
                Log.d("EXPORTACIONES_LOG", "📈 Success: ${response.success}")
                Log.d("EXPORTACIONES_LOG", "💬 Message: ${response.message}")
                Log.d("EXPORTACIONES_LOG", "🔢 Code: ${response.code}")
                Log.d("EXPORTACIONES_LOG", "📊 Registros Insertados: ${response.registrosInsertados}")
                Log.d("EXPORTACIONES_LOG", "⏱️ Tiempo (ms): ${response.tiempoMs}")
                Log.d("EXPORTACIONES_LOG", "🔧 Method: ${response.method}")
                if (response.winvdNroInvList?.isNotEmpty() == true) {
                    Log.d("EXPORTACIONES_LOG", "📋 Inventarios procesados: ${response.winvdNroInvList}")
                }
            },
            onFailure = { error ->
                Log.e("EXPORTACIONES_LOG", "❌ [VERIFICAR] Error en API: ${error.message}")
                throw error
            }
        )
        
        // NO cambiar estado en SQLite - mantener para verificación
    }
    
    /**
     * Inserta un inventario para verificación (sin cerrar el inventario)
     * Incluye: cabecera STK y detalles STK (NO actualiza web_inventario)
     */
    private suspend fun insertarInventarioParaVerificacion(
        idCabecera: Int,
        inventario: InventarioPendienteExportar,
        detalles: List<DetalleInventarioExportar>
    ) {
        var connection: java.sql.Connection? = null
        
        try {
            connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            connection.autoCommit = false
            
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Iniciando inserción para verificación del inventario #${inventario.winvdNroInv}")
            
            // 1. Insertar cabecera STK
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Insertando cabecera STK...")
            val tipoToma = if (inventario.tipoToma == "MANUAL") "M" else "C"
            
            val sqlCabecera = """
                INSERT INTO ADCS.WEB_STK_CARGA_INV  
                (INVE_NUMERO, INVE_EMPR, INVE_SUC, INVE_DEP, INVE_FEC, INVE_CANT_TOMA, 
                 INVE_LOGIN, INVE_TIPO_TOMA, INVE_REF) 
                VALUES (?, '1', ?, ?, CURRENT_TIMESTAMP, '1', UPPER(?), ?, ?)
            """.trimIndent()
            
            val psCabecera = connection.prepareStatement(sqlCabecera)
            psCabecera.setInt(1, idCabecera)
            psCabecera.setInt(2, inventario.ardeSuc)
            psCabecera.setInt(3, inventario.winveDep)
            psCabecera.setString(4, inventario.winveLoginCerradoWeb)
            psCabecera.setString(5, tipoToma)
            psCabecera.setInt(6, inventario.winvdNroInv)
            
            val filasCabecera = psCabecera.executeUpdate()
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Cabecera insertada. Filas afectadas: $filasCabecera")
            psCabecera.close()
            
            // 2. Insertar detalles STK
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Insertando ${detalles.size} detalles STK...")
            
            for (detalle in detalles) {
                val sqlDetalle = """
                    INSERT INTO ADCS.WEB_STK_CARGA_INV_DET   
                    (INVD_NRO_INV, INVD_ART, INVD_SECU, INVD_CANT_INV, 
                     INVD_FEC_VTO, INVD_LOTE, INVD_UM) 
                    VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), ?, '')
                """.trimIndent()
                
                val psDetalle = connection.prepareStatement(sqlDetalle)
                psDetalle.setInt(1, idCabecera)
                psDetalle.setString(2, detalle.winvdArt)
                psDetalle.setInt(3, detalle.winvdSecu)
                psDetalle.setString(4, detalle.winvdCantInv)
                psDetalle.setString(5, detalle.winvdFecVto)
                psDetalle.setString(6, detalle.winvdLote)
                
                val filasDetalle = psDetalle.executeUpdate()
                Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Detalle ${detalle.winvdSecu} insertado. Filas afectadas: $filasDetalle")
                psDetalle.close()
            }
            
            // 3. Commit de la transacción (SIN actualizar web_inventario)
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Commit de transacción para verificación...")
            connection.commit()
            Log.d("EXPORTACIONES_LOG", "✅ [VERIFICACION] Inserción para verificación completada exitosamente")
            
        } catch (e: Exception) {
            Log.d("EXPORTACIONES_LOG", "❌ [VERIFICACION] Error en inserción para verificación: ${e.message}")
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Realizando rollback...")
            connection?.rollback()
            throw Exception("Error al insertar inventario para verificación: ${e.message}")
        } finally {
            connection?.close()
        }
    }

    /**
     * Obtiene inventarios pendientes de exportar desde SQLite
     * Consulta la tabla local STKW002INV en lugar de Oracle
     */
    private suspend fun obtenerInventariosPendientesDesdeSQLite(
        idSucursal: String,
        userLogin: String
    ): List<InventarioPendienteExportar> {
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Consultando inventarios pendientes en SQLite...")
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Parámetros de búsqueda: sucursal='$idSucursal', usuario='$userLogin'")
        
        // Primero, obtener TODOS los inventarios con estado 'P' para debug
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Obteniendo todos los inventarios con estado 'P'...")
        val todosInventariosP = inventarioDetalleDao.getInventariosDetalleByEstado("P").first()
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Total inventarios con estado 'P': ${todosInventariosP.size}")
        
            if (todosInventariosP.isNotEmpty()) {
                Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Primeros 3 inventarios con estado 'P':")
                todosInventariosP.take(3).forEach { inv ->
                    Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] - Inventario #${inv.winvd_nro_inv}, Sucursal: '${inv.winve_suc}', Usuario: '${inv.winve_login}', Estado: '${inv.estado}'")
                }
            }
        
        // Ahora hacer la consulta solo por estado (sin filtro de sucursal)
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Consultando solo por estado 'P' (sin filtro de sucursal)...")
        val inventariosDetalle = inventarioDetalleDao.getInventariosDetalleByEstado("P")
        
        // Convertir a Flow y tomar el primer valor
        val inventarios = inventariosDetalle.first()
        
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Inventarios encontrados con estado 'P': ${inventarios.size}")
        
            if (inventarios.isNotEmpty()) {
                Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Inventarios encontrados:")
                inventarios.take(3).forEach { inv ->
                    Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] - Inventario #${inv.winvd_nro_inv}, Sucursal: '${inv.winve_suc}', Usuario: '${inv.winve_login}'")
                }
            }
        
            // Agrupar por número de inventario y crear objetos InventarioPendienteExportar
            val inventariosUnicos = inventarios
                .groupBy { it.winvd_nro_inv }
                .map { (nroInventario, detalles) ->
                    val primerDetalle = detalles.first()
                    InventarioPendienteExportar(
                        winvdNroInv = nroInventario,
                        winveLoginCerradoWeb = primerDetalle.winve_login, // Usar winve_login en lugar de WINVE_LOGIN_CERRADO_WEB
                        winveDep = primerDetalle.winve_dep.toIntOrNull() ?: 0,
                        ardeSuc = primerDetalle.ARDE_SUC,
                        tipoToma = primerDetalle.tipo_toma,
                        tomaRegistro = primerDetalle.toma_registro,
                        winveFecha = primerDetalle.winve_fec,
                        winveSucursal = primerDetalle.sucursal
                    )
                }
        
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Inventarios únicos antes de filtrar por usuario: ${inventariosUnicos.size}")
        
        // Filtrar por usuario (hacer más flexible)
        val inventariosFiltrados = inventariosUnicos.filter { 
            val coincideUsuario = it.winveLoginCerradoWeb.equals(userLogin, ignoreCase = true)
            Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Usuario '${it.winveLoginCerradoWeb}' coincide con '$userLogin': $coincideUsuario")
            coincideUsuario
        }
        
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Inventarios únicos después de filtrar por usuario: ${inventariosFiltrados.size}")
        
        // Si no hay resultados con filtro de usuario, mostrar todos (para debug)
        if (inventariosFiltrados.isEmpty() && inventariosUnicos.isNotEmpty()) {
            Log.d("EXPORTACIONES_LOG", "⚠️ [SQLITE] No se encontraron inventarios para el usuario '$userLogin', pero hay ${inventariosUnicos.size} inventarios disponibles")
            Log.d("EXPORTACIONES_LOG", "⚠️ [SQLITE] Usuarios disponibles:")
            inventariosUnicos.take(5).forEach { inv ->
                Log.d("EXPORTACIONES_LOG", "⚠️ [SQLITE] - Usuario: '${inv.winveLoginCerradoWeb}'")
            }
            
            // TEMPORAL: Retornar todos los inventarios para debug
            Log.d("EXPORTACIONES_LOG", "⚠️ [SQLITE] TEMPORAL: Retornando todos los inventarios para debug")
            return inventariosUnicos
        }
        
        return inventariosFiltrados
    }
    
    /**
     * Obtiene los detalles de un inventario desde SQLite
     * Convierte los datos de InventarioDetalle a DetalleInventarioExportar
     */
    private suspend fun obtenerDetallesInventarioDesdeSQLite(nroInventario: Int): List<DetalleInventarioExportar> {
        Log.d("EXPORTACIONES_LOG", "🔍 [DETALLES] Obteniendo detalles del inventario #$nroInventario desde SQLite...")
        
        val inventariosDetalle = inventarioDetalleDao.getInventarioDetalleByNumero(nroInventario).first()
        
        Log.d("EXPORTACIONES_LOG", "🔍 [DETALLES] Encontrados ${inventariosDetalle.size} detalles para inventario #$nroInventario")
        
            val detalles = inventariosDetalle.map { detalle ->
                Log.d("EXPORTACIONES_LOG", "🔍 [DETALLES] Fecha vencimiento original: '${detalle.winvd_fec_vto}'")
                DetalleInventarioExportar(
                    winvdNroInv = detalle.winvd_nro_inv,
                    winvdLote = detalle.winvd_lote,
                    winvdArt = detalle.winvd_art,
                    winvdFecVto = detalle.winvd_fec_vto,
                    winvdArea = detalle.winvd_area.toString(),
                    winvdDpto = detalle.winvd_dpto.toString(),
                    winvdSecc = detalle.winvd_secc.toString(),
                    winvdFlia = detalle.winvd_flia.toString(),
                    winvdGrupo = detalle.winvd_grupo.toString(),
                    winvdCantAct = detalle.winvd_cant_act.toString(),
                    winvdCantInv = detalle.winvd_cant_inv.toString(),
                    winvdSecu = detalle.winvd_secu,
                    winveDep = detalle.winve_dep.toIntOrNull() ?: 0,
                    winveSuc = detalle.ARDE_SUC,
                    caja = detalle.caja,
                    gruesa = detalle.GRUESA,
                    unidInd = detalle.UNID_IND,
                    artDesc = detalle.art_desc,
                    areaDesc = detalle.area_desc,
                    dptoDesc = detalle.dpto_desc,
                    seccDesc = detalle.secc_desc,
                    fliaDesc = detalle.flia_desc,
                    grupDesc = detalle.grup_desc,
                    winvdSubgr = detalle.winvd_subgr,
                    estado = detalle.estado,
                    winveLoginCerradoWeb = detalle.WINVE_LOGIN_CERRADO_WEB,
                    tipoToma = detalle.tipo_toma,
                    winveLogin = detalle.winve_login,
                    winvdConsolidado = detalle.winvd_consolidado,
                    descGrupoParcial = detalle.desc_grupo_parcial,
                    descFamilia = detalle.desc_familia,
                    winveFec = detalle.winve_fec,
                    tomaRegistro = detalle.toma_registro,
                    codBarra = detalle.cod_barra,
                    sucursal = detalle.sucursal,
                    deposito = detalle.deposito
                )
            }
        
        Log.d("EXPORTACIONES_LOG", "🔍 [DETALLES] Convertidos ${detalles.size} detalles para exportación")
        
        return detalles
    }

    suspend fun verificarInventarioAnulado(numeroInventario: Int): Boolean = withContext(Dispatchers.IO) {
        var connection: Connection? = null

        try {
            connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")

            val sql = """
                SELECT COUNT(*) as count
                FROM ADCS.web_inventario 
                WHERE WINVE_NUMERO = ? AND WINVE_ESTADO_WEB = 'A'
            """.trimIndent()

            val ps = connection.prepareStatement(sql)
            ps.setInt(1, numeroInventario)

            val rs = ps.executeQuery()
            val count = if (rs.next()) rs.getInt("count") else 0

            rs.close()
            ps.close()

            count == 0 // Si count es 0, significa que está anulado

        } catch (e: Exception) {
            Log.e("InventarioSincronizacion", "Error al verificar inventario anulado: ${e.message}", e)
            false // En caso de error, asumir que no está anulado
        } finally {
            connection?.close()
        }
    }

    /**
     * Actualiza inventario para reinventario (solo cantidades)
     */
    suspend fun actualizarInventarioReinventario(
        numeroInventario: Int,
        detalles: List<DetalleInventarioExportar>
    ) = withContext(Dispatchers.IO) {
        var connection: Connection? = null

        try {
            connection = ConnectionOracle.getConnection() ?: throw Exception("No se pudo conectar a la base de datos")
            connection.autoCommit = false

            for (detalle in detalles) {
                val sql = """
                    UPDATE ADCS.STKW002INV 
                    SET winvd_cant_inv = ?
                    WHERE winvd_nro_inv = ? AND winvd_art = ? AND winvd_lote = ?
                """.trimIndent()

                val ps = connection.prepareStatement(sql)
                ps.setString(1, detalle.winvdCantInv)
                ps.setInt(2, numeroInventario)
                ps.setString(3, detalle.winvdArt)
                ps.setString(4, detalle.winvdLote)

                ps.executeUpdate()
                ps.close()
            }

            connection.commit()

        } catch (e: Exception) {
            connection?.rollback()
            throw Exception("Error al actualizar inventario reinventario: ${e.message}")
        } finally {
            connection?.close()
        }
    }

}
