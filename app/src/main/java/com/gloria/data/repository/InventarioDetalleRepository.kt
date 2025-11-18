package com.gloria.data.repository

import android.util.Log
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
        estado: String,
        usuarioCerrado: String
    ) {
        Log.d("LogConteo", "=== DEBUG REPOSITORY ===")
        Log.d("LogConteo", "Parámetros recibidos:")
        Log.d("LogConteo", "- numeroInventario: $numeroInventario")
        Log.d("LogConteo", "- secuencia: $secuencia")
        Log.d("LogConteo", "- cantidad: $cantidad")
        Log.d("LogConteo", "- estado: '$estado'")
        Log.d("LogConteo", "- usuarioCerrado: '$usuarioCerrado'")
        Log.d("LogConteo", "- longitud usuarioCerrado: ${usuarioCerrado.length}")
        Log.d("LogConteo", "=== FIN DEBUG REPOSITORY ===")
        
        return inventarioDetalleDao.actualizarCantidadInventario(
            numeroInventario, secuencia, cantidad, estado, usuarioCerrado
        )
    }

    suspend fun actualizarCantidadInventarioSoloCantidad(
        numeroInventario: Int,
        secuencia: Int,
        codigoArticulo: String,
        cantidad: Int
    ) {
        Log.d("LogConteo", "=== DEBUG REPOSITORY SOLO CANTIDAD ===")
        Log.d("LogConteo", "- numeroInventario: $numeroInventario")
        Log.d("LogConteo", "- secuencia: $secuencia")
        Log.d("LogConteo", "- codigoArticulo: $codigoArticulo")
        Log.d("LogConteo", "- cantidad: $cantidad")
        Log.d("LogConteo", "=== FIN DEBUG REPOSITORIO SOLO CANTIDAD ===")

        return inventarioDetalleDao.actualizarCantidadInventarioSoloCantidad(
            numeroInventario, secuencia, codigoArticulo, cantidad
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

    suspend fun actualizarUsuarioCerradoPorInventario(
        numeroInventario: Int,
        usuarioCerrado: String
    ) {
        inventarioDetalleDao.actualizarUsuarioCerradoPorInventario(
            numeroInventario, usuarioCerrado
        )
    }
    
    suspend fun getTipoInventario(nroInventario: Int): String? {
        return inventarioDetalleDao.getTipoInventario(nroInventario)
    }
}
