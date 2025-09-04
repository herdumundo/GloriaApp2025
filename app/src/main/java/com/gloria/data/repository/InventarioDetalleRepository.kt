package com.gloria.data.repository

import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.model.ArticuloInventario
import com.gloria.data.model.InventarioCard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositorio para operaciones de InventarioDetalle
 */
class InventarioDetalleRepository @Inject constructor(
    private val inventarioDetalleDao: InventarioDetalleDao
) {
    fun getInventariosCardsDistinct(sucursal: Int): Flow<List<InventarioCard>> {
        return inventarioDetalleDao.getInventariosCardsDistinct(sucursal)
    }
    
    fun getArticulosInventario(nroInventario: Int): Flow<List<ArticuloInventario>> {
        return inventarioDetalleDao.getArticulosInventario(nroInventario)
    }
    
    suspend fun actualizarCantidadInventario(
        numeroInventario: Int,
        secuencia: Int,
        cantidad: Int,
        estado: String
    ) {
        return inventarioDetalleDao.actualizarCantidadInventario(
            numeroInventario, secuencia, cantidad, estado
        )
    }
    
    suspend fun actualizarEstadoInventario(
        numeroInventario: Int,
        estado: String
    ) {
        return inventarioDetalleDao.actualizarEstadoInventario(
            numeroInventario, estado
        )
    }
}
