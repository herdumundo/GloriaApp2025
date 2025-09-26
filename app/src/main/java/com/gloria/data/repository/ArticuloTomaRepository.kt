package com.gloria.data.repository

import com.gloria.data.dao.ArticuloTomaDao
import com.gloria.data.model.ArticuloToma
import com.gloria.domain.usecase.exportacion.DetalleInventarioExportar
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
