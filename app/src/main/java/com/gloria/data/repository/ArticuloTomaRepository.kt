package com.gloria.data.repository

import com.gloria.data.dao.ArticuloTomaDao
import com.gloria.data.model.ArticuloToma
import com.gloria.domain.usecase.exportacion.InventarioPendienteExportar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar las operaciones de artículos de toma
 */
@Singleton
class ArticuloTomaRepository @Inject constructor(
    private val articuloTomaDao: ArticuloTomaDao
) {
    
    /**
     * Obtiene los artículos de una toma específica
     */
    suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma> {
        return articuloTomaDao.getArticulosToma(nroToma)
    }
    
    /**
     * Obtiene inventarios pendientes de exportar
     */
    suspend fun getInventariosPendientesExportar(
        idSucursal: String,
        userLogin: String
    ): List<InventarioPendienteExportar> {
        return articuloTomaDao.getInventariosPendientesExportar(idSucursal, userLogin)
    }
    
    /**
     * Obtiene los detalles de un inventario específico
     */
    suspend fun getDetallesInventario(nroInventario: Int): List<DetalleInventarioExportar> {
        return articuloTomaDao.getDetallesInventario(nroInventario)
    }
    
    /**
     * Marca un inventario como anulado
     */
    suspend fun marcarInventarioComoAnulado(nroInventario: Int) {
        articuloTomaDao.marcarInventarioComoAnulado(nroInventario)
    }
    
    /**
     * Marca un inventario como cerrado
     */
    suspend fun marcarInventarioComoCerrado(nroInventario: Int) {
        articuloTomaDao.marcarInventarioComoCerrado(nroInventario)
    }
}

data class DetalleInventarioExportar(
    val winvdNroInv: Int,
    val winvdLote: String,
    val winvdArt: String,
    val winvdFecVto: String,
    val winvdArea: String,
    val winvdDpto: String,
    val winvdSecc: String,
    val winvdFlia: String,
    val winvdGrupo: String,
    val winvdCantAct: String,
    val winvdCantInv: String,
    val winvdSecu: Int,
    val winveDep: Int,
    val winveSuc: Int,
    val caja: Int,
    val gruesa: Int,
    val unidInd: Int,
    val artDesc: String,
    val areaDesc: String,
    val dptoDesc: String,
    val seccDesc: String,
    val fliaDesc: String,
    val grupDesc: String,
    val winvdSubgr: Int,
    val estado: String,
    val winveLoginCerradoWeb: String,
    val tipoToma: String,
    val winveLogin: String,
    val winvdConsolidado: String,
    val descGrupoParcial: String,
    val descFamilia: String,
    val winveFec: String,
    val tomaRegistro: String,
    val codBarra: String,
    val sucursal: String,
    val deposito: String
)
