package com.gloria.repository

import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.entity.InventarioDetalle
import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.model.ArticuloInventario
import com.gloria.data.model.InventarioCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * Repositorio para operaciones de inventario
 */
class InventarioRepository @Inject constructor(
    private val inventarioDetalleDao: InventarioDetalleDao,
    private val loggedUserRepository: LoggedUserRepository
) {
    
    /**
     * Obtiene los inventarios como Flow
     */
    fun getInventariosFlow(): Flow<List<InventarioCard>> {
        // Convertir InventarioDetalle a InventarioCard
        return inventarioDetalleDao.getAllInventariosDetalle().map { detalles ->
            detalles.groupBy { it.winvd_nro_inv }.map { (nroInv, detallesInv) ->
                InventarioCard(
                    winvd_nro_inv = nroInv,
                    fecha_toma = detallesInv.first().winve_fec,
                    area_desc = detallesInv.first().area_desc,
                    dpto_desc = detallesInv.first().dpto_desc,
                    tipo_toma = detallesInv.first().tipo_toma,
                    secc_desc = detallesInv.first().secc_desc,
                    winvd_consolidado = detallesInv.first().winvd_consolidado.toString(),
                    desc_grupo_parcial = detallesInv.first().desc_grupo_parcial,
                    desc_familia = detallesInv.first().desc_familia,
                    sucursal = detallesInv.first().sucursal,
                    deposito = detallesInv.first().deposito,
                    estado= detallesInv.first().estado
                )
            }
        }
    }
    
    /**
     * Obtiene los inventarios de forma síncrona
     */
    suspend fun getInventarios(): List<InventarioCard> {
        val detalles = inventarioDetalleDao.getAllInventariosDetalle()
        var result: List<InventarioCard> = emptyList()
        detalles.collect { detallesList ->
            result = detallesList.groupBy { it.winvd_nro_inv }.map { (nroInv, detallesInv) ->
                InventarioCard(
                    winvd_nro_inv = nroInv,
                    fecha_toma = detallesInv.first().winve_fec,
                    area_desc = detallesInv.first().area_desc,
                    dpto_desc = detallesInv.first().dpto_desc,
                    tipo_toma = detallesInv.first().tipo_toma,
                    secc_desc = detallesInv.first().secc_desc,
                    winvd_consolidado = detallesInv.first().winvd_consolidado.toString(),
                    desc_grupo_parcial = detallesInv.first().desc_grupo_parcial,
                    desc_familia = detallesInv.first().desc_familia,
                    sucursal = detallesInv.first().sucursal,
                    deposito = detallesInv.first().deposito,
                    estado = detallesInv.first().estado
                )
            }
        }
        return result
    }
    
    /**
     * Obtiene los artículos de un inventario como Flow
     */
    fun getArticulosInventarioFlow(nroInventario: Int): Flow<List<ArticuloInventario>> {
        return inventarioDetalleDao.getInventarioDetalleByNumero(nroInventario).map { detalles ->
            detalles.map { detalle ->
                ArticuloInventario(
                    winvd_nro_inv = detalle.winvd_nro_inv,
                    artDesc = detalle.art_desc,
                    winvdLote = detalle.winvd_lote,
                    winvdArt = detalle.winvd_art,
                    winvdFecVto = detalle.winvd_fec_vto,
                    winvdArea = detalle.winvd_area,
                    winvdDpto = detalle.winvd_dpto,
                    winvdSecc = detalle.winvd_secc,
                    winvdFlia = detalle.winvd_flia,
                    winvdGrupo = detalle.winvd_grupo,
                    winvdCantAct = detalle.winvd_cant_act,
                    winvdCantInv = detalle.winvd_cant_inv,
                    winvdSecu = detalle.winvd_secu,
                    grupDesc = detalle.grup_desc,
                    fliaDesc = detalle.flia_desc,
                    tomaRegistro = detalle.toma_registro.toString(),
                    codBarra = detalle.cod_barra,
                    caja = detalle.caja,
                    gruesa = detalle.GRUESA,
                    stockVisible = detalle.stockVisible
                )
            }
        }
    }
    
    /**
     * Obtiene los artículos de un inventario de forma síncrona
     */
    suspend fun getArticulosInventario(nroInventario: Int): List<ArticuloInventario> {
        val detalles = inventarioDetalleDao.getInventarioDetalleByNumero(nroInventario)
        var result: List<ArticuloInventario> = emptyList()
        detalles.collect { detallesList ->
            result = detallesList.map { detalle ->
                ArticuloInventario(
                    winvd_nro_inv = detalle.winvd_nro_inv,
                    artDesc = detalle.art_desc,
                    winvdLote = detalle.winvd_lote,
                    winvdArt = detalle.winvd_art,
                    winvdFecVto = detalle.winvd_fec_vto,
                    winvdArea = detalle.winvd_area,
                    winvdDpto = detalle.winvd_dpto,
                    winvdSecc = detalle.winvd_secc,
                    winvdFlia = detalle.winvd_flia,
                    winvdGrupo = detalle.winvd_grupo,
                    winvdCantAct = detalle.winvd_cant_act,
                    winvdCantInv = detalle.winvd_cant_inv,
                    winvdSecu = detalle.winvd_secu,
                    grupDesc = detalle.grup_desc,
                    fliaDesc = detalle.flia_desc,
                    tomaRegistro = detalle.toma_registro.toString(),
                    codBarra = detalle.cod_barra,
                    caja = detalle.caja,
                    gruesa = detalle.GRUESA,
                    stockVisible=detalle.stockVisible
                )
            }
        }
        return result
    }
    
    /**
     * Actualiza el conteo de un artículo
     */
    suspend fun updateConteo(
        nroInventario: Int,
        codigoArticulo: String,
        cantidadContada: Double,
        observaciones: String? = null
    ) {
        // Buscar el detalle por número de inventario y código de artículo
        val detalles = inventarioDetalleDao.getInventarioDetalleByNumero(nroInventario)
        // Nota: Necesitamos implementar la lógica para encontrar el detalle correcto
        // Por ahora, usamos un método simplificado
    }
    
    /**
     * Marca un inventario como completado
     */
    suspend fun marcarInventarioCompletado(nroInventario: Int) {
        // Implementar lógica para marcar inventario como completado
        // Por ahora, usamos un método simplificado
    }
    
    /**
     * Obtiene el usuario logueado
     */
    suspend fun getLoggedUser() = loggedUserRepository.getLoggedUserSync()
    
    /**
     * Guarda una toma manual
     */
    suspend fun saveTomaManual(
        articulosSeleccionados: List<com.gloria.data.model.ArticuloLote>,
        nroInventario: Int,
        username: String
    ): Result<Unit> {
        return try {
            // Crear detalles de inventario para cada artículo
            val detallesInventario = articulosSeleccionados.mapIndexed { index, articulo ->
                InventarioDetalle(
                    winvd_nro_inv = nroInventario,
                    winvd_secu = index + 1,
                    winvd_cant_act = articulo.cantidad.toInt(),
                    winvd_cant_inv = 0,
                    winvd_fec_vto = articulo.vencimiento,
                    winve_fec = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date()),
                    ARDE_SUC = 1,
                    winvd_art = articulo.artCodigo,
                    art_desc = articulo.artDesc,
                    winvd_lote = articulo.ardeLote,
                    winvd_area = 0,
                    area_desc = "",
                    winvd_dpto = 0,
                    dpto_desc = "",
                    winvd_secc = 0,
                    secc_desc = "",
                    winvd_flia = articulo.fliaCodigo.toIntOrNull() ?: 0,
                    flia_desc = articulo.fliaDesc,
                    winvd_grupo = articulo.grupCodigo,
                    grup_desc = articulo.grupDesc,
                    winvd_subgr = articulo.sugrCodigo,
                    estado = "PENDIENTE",
                    WINVE_LOGIN_CERRADO_WEB = username,
                    tipo_toma = "MANUAL",
                    winve_login = username,
                    winvd_consolidado = "0",
                    desc_grupo_parcial = "",
                    desc_familia = "",
                    winve_dep = "0",
                    winve_suc = "1",
                    toma_registro = "0",
                    cod_barra = "",
                    caja = 0,
                    GRUESA = 0,
                    UNID_IND = 0,
                    sucursal = "",
                    deposito = "",
                    stockVisible =articulo.inventarioVisible
                )
            }
            
            // Insertar detalles en la base de datos
            detallesInventario.forEach { detalle ->
                inventarioDetalleDao.insertInventarioDetalle(detalle)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Genera un número de inventario único
     */
    suspend fun generateInventarioNumber(): Int {
        val ultimoInventario = inventarioDetalleDao.getMaxNumeroInventario()
        return (ultimoInventario ?: 0) + 1
    }
}
