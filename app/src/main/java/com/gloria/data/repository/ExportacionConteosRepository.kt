package com.gloria.data.repository

import android.util.Log
import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.entity.api.ConteoRequest
import com.gloria.data.entity.api.CabeceraConteo
import com.gloria.data.entity.api.DetalleConteo
import com.gloria.data.entity.api.InsertarConteosRequest
import com.gloria.data.mapper.ConteoRequestMapper
import com.gloria.domain.usecase.enviarconteo.EnviarConteoVerificacionUseCase
import com.gloria.domain.usecase.exportacion.InventarioPendienteExportar
import com.gloria.domain.usecase.exportacion.DetalleInventarioExportar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar la exportación de conteos usando API REST
 * Migrado completamente de Oracle a API REST
 */
@Singleton
class ExportacionConteosRepository @Inject constructor(
    private val inventarioDetalleDao: InventarioDetalleDao,
    private val insertarConteosApiRepository: InsertarConteosApiRepository,
    private val loggedUserDao: LoggedUserDao,
    private val enviarConteoVerificacionUseCase: EnviarConteoVerificacionUseCase
) {
    
    /**
     * Exporta conteos realizados usando la nueva API unificada
     */
    suspend fun exportarConteosRealizados(
        idSucursal: String,
        userLogin: String
    ): Result<String> {
        return try {
            Log.d("EXPORTACIONES_LOG", "🔄 [API] Iniciando exportación de conteos realizados")
            
            // 1. Obtener usuario logueado
            val loggedUser = loggedUserDao.getCurrentUserSync()
            if (loggedUser == null) {
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // 2. Obtener inventarios pendientes
            val inventariosPendientes = obtenerInventariosPendientesDesdeSQLite(userLogin)
            
            if (inventariosPendientes.isEmpty()) {
                return Result.success("No se encontraron registros por exportar.")
            }
            
            Log.d("EXPORTACIONES_LOG", "📊 [API] Inventarios pendientes: ${inventariosPendientes.size}")
            
            // 3. Convertir a estructura de API
            val conteos = convertirInventariosAConteos(inventariosPendientes)
            
            // 4. Crear request
            val request = InsertarConteosRequest(
                userdb = loggedUser.username,
                passdb = loggedUser.password,
                conteos = conteos
            )
            
            Log.d("EXPORTACIONES_LOG", "🌐 [API] Enviando ${conteos.size} conteos al servidor...")
            
            // 5. Llamar a la API
            val apiResult = insertarConteosApiRepository.insertarConteos(request)
            
            if (apiResult.isFailure) {
                val error = apiResult.exceptionOrNull()?.message ?: "Error desconocido en API"
                Log.e("EXPORTACIONES_LOG", "❌ [API] Error: $error")
                return Result.failure(Exception("Error al exportar conteos: $error"))
            }
            
            val response = apiResult.getOrNull()!!
            Log.d("EXPORTACIONES_LOG", "✅ [API] Respuesta exitosa: ${response.message}")
            
            // 6. Marcar inventarios como cerrados en SQLite
            Log.d("EXPORTACIONES_LOG", "💾 [SQLITE] Marcando inventarios como cerrados...")
            for (inventario in inventariosPendientes) {
                inventarioDetalleDao.marcarInventarioComoCerrado(inventario.winvdNroInv)
            }
            
            val mensaje = "✅ Datos exportados exitosamente. Procesados: ${conteos.size} conteos via API."
            Log.d("EXPORTACIONES_LOG", "🎉 [API] $mensaje")
            
            Result.success(mensaje)
            
        } catch (e: Exception) {
            val error = "❌ Error al exportar conteos realizados: ${e.message}"
            Log.e("EXPORTACIONES_LOG", "[API] $error", e)
            Result.failure(Exception(error))
        }
    }
    
    /**
     * Exporta conteos para verificación usando la API (mantiene estado 'P')
     */
    suspend fun exportarConteosParaVerificacion(
        idSucursal: String,
        userLogin: String
    ): Result<String> {
        return try {
            Log.d("EXPORTACIONES_LOG", "🔍 [VERIFICACION] Iniciando envío de conteos para verificación")

            // 1) Obtener inventarios pendientes (P/F) del usuario
            val inventariosPendientes = obtenerInventariosPendientesDesdeSQLite(userLogin)
            if (inventariosPendientes.isEmpty()) {
                return Result.success("No se encontraron registros por exportar.")
            }

            // 2) Armar lista de ConteoRequest usando el mapper
            val conteoRequests = mutableListOf<com.gloria.data.model.ConteoRequest>()
            for (inventario in inventariosPendientes) {
                val detalles = obtenerDetallesInventarioDesdeSQLite(inventario.winvdNroInv)
                conteoRequests += ConteoRequestMapper.toConteoRequestListFromDetalleExportar(detalles)
            }

            if (conteoRequests.isEmpty()) {
                return Result.failure(Exception("No hay detalles de inventario para enviar"))
            }

            Log.d("EXPORTACIONES_LOG", "🌐 [VERIFICACION] Enviando ${conteoRequests.size} registros al endpoint de verificación...")

            // 3) Llamar al use case (usa EnviarConteoVerificacionApi internamente)
            val resultado = enviarConteoVerificacionUseCase(conteoRequests)

            resultado.fold(
                onSuccess = { resp ->
                    val msg = "Conteos enviados para verificación. Registros: ${resp.registrosInsertados ?: 0}."
                    Log.d("EXPORTACIONES_LOG", "✅ [VERIFICACION] $msg")
                    Result.success(msg)
                },
                onFailure = { err ->
                    Log.e("EXPORTACIONES_LOG", "❌ [VERIFICACION] Error: ${err.message}")
                    Result.failure(err)
                }
            )

        } catch (e: Exception) {
            val error = "❌ Error al exportar conteos para verificación: ${e.message}"
            Log.e("EXPORTACIONES_LOG", error, e)
            Result.failure(Exception(error))
        }
    }
    
    /**
     * Obtiene inventarios pendientes de exportar desde SQLite
     */
    private suspend fun obtenerInventariosPendientesDesdeSQLite(
        userLogin: String
    ): List<InventarioPendienteExportar> = withContext(Dispatchers.IO) {
        
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Consultando inventarios pendientes...")
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Usuario: '$userLogin'")
        
        // Obtener inventarios con estado 'P' (Pendiente)
        val inventariosDetalle = inventarioDetalleDao.getInventariosDetalleByEstado().first()
        
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Inventarios encontrados: ${inventariosDetalle.size}")
        
        // Agrupar por número de inventario
        val inventariosUnicos = inventariosDetalle
            .groupBy { it.winvd_nro_inv }
            .map { (nroInventario, detalles) ->
                val primerDetalle = detalles.first()
                InventarioPendienteExportar(
                    winvdNroInv = nroInventario,
                    winveLoginCerradoWeb = primerDetalle.winve_login,
                    winveDep = primerDetalle.winve_dep.toIntOrNull() ?: 0,
                    ardeSuc = primerDetalle.ARDE_SUC,
                    tipoToma = primerDetalle.tipo_toma,
                    tomaRegistro = primerDetalle.toma_registro,
                    winveFecha = primerDetalle.winve_fec,
                    winveSucursal = primerDetalle.sucursal,
                    estado = primerDetalle.estado
                )
            }
        
        // Filtrar por usuario
        val inventariosFiltrados = inventariosUnicos.filter { 
            it.winveLoginCerradoWeb.equals(userLogin, ignoreCase = true)
        }
        
        Log.d("EXPORTACIONES_LOG", "🔍 [SQLITE] Inventarios filtrados por usuario: ${inventariosFiltrados.size}")
        
        // Si no hay resultados, retornar todos para debug
        if (inventariosFiltrados.isEmpty() && inventariosUnicos.isNotEmpty()) {
            Log.w("EXPORTACIONES_LOG", "⚠️ [SQLITE] No se encontraron inventarios para '$userLogin', retornando todos")
            return@withContext inventariosUnicos
        }
        
        return@withContext inventariosFiltrados
    }
    
    /**
     * Obtiene los detalles de un inventario desde SQLite
     */
    private suspend fun obtenerDetallesInventarioDesdeSQLite(
        nroInventario: Int
    ): List<DetalleInventarioExportar> = withContext(Dispatchers.IO) {
        
        Log.d("EXPORTACIONES_LOG", "🔍 [DETALLES] Obteniendo detalles del inventario #$nroInventario")
        
        val inventariosDetalle = inventarioDetalleDao.getInventarioDetalleByNumero(nroInventario).first()
        
        Log.d("EXPORTACIONES_LOG", "🔍 [DETALLES] Encontrados ${inventariosDetalle.size} detalles")
        
        val detalles = inventariosDetalle.map { detalle ->
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
                gruesa = detalle.GRUESA.toInt(),
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
        
        Log.d("EXPORTACIONES_LOG", "🔍 [DETALLES] Convertidos ${detalles.size} detalles")
        
        return@withContext detalles
    }
    
    /**
     * Convierte inventarios locales a estructura de conteos para la API
     */
    private suspend fun convertirInventariosAConteos(
        inventarios: List<InventarioPendienteExportar>
    ): List<ConteoRequest> = withContext(Dispatchers.IO) {
        
        Log.d("EXPORTACIONES_LOG", "🔄 [CONVERTER] Convirtiendo ${inventarios.size} inventarios a conteos")
        
        val conteos = mutableListOf<ConteoRequest>()
        
        for (inventario in inventarios) {
            // Obtener detalles del inventario
            val detallesInventario = obtenerDetallesInventarioDesdeSQLite(inventario.winvdNroInv)
            
            if (detallesInventario.isNotEmpty()) {
                val cabecera = CabeceraConteo(
                    ardeSuc = inventario.ardeSuc,
                    winveDep = inventario.winveDep,
                    winveLoginCerradoWeb = inventario.winveLoginCerradoWeb,
                    usuarioQueConteo = inventario.winveLoginCerradoWeb,
                    tipoToma = if (inventario.tipoToma == "MANUAL") "M" else "C",
                    winvdNroInv = inventario.winvdNroInv,
                    estado = inventario.estado
                )

                val detalle = detallesInventario.map { d ->
                    DetalleConteo(
                        winvdArt = d.winvdArt,
                        winvdSecu = d.winvdSecu,
                        winvdCantInv = d.winvdCantInv,
                        winvdFecVto = d.winvdFecVto.take(10),
                        winvdLote = d.winvdLote
                    )
                }

                val conteo = ConteoRequest(
                    cabecera = cabecera,
                    detalle = detalle
                )
                
                conteos.add(conteo)
                
                Log.d("EXPORTACIONES_LOG", "✅ [CONVERTER] Inventario #${inventario.winvdNroInv} convertido con ${detalle.size} detalles")
            }
        }
        
        Log.d("EXPORTACIONES_LOG", "🔄 [CONVERTER] Conversión completada: ${conteos.size} conteos")
        
        return@withContext conteos
    }
}
