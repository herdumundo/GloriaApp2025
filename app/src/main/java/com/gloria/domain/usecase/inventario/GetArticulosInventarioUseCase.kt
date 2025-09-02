package com.gloria.domain.usecase.inventario

import com.gloria.data.model.ArticuloInventario
import com.gloria.repository.InventarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener los artículos de un inventario específico
 */
class GetArticulosInventarioUseCase @Inject constructor(
    private val inventarioRepository: InventarioRepository
) {
    
    /**
     * Obtiene los artículos de un inventario como Flow
     * @param nroInventario Número del inventario
     */
    fun getArticulosFlow(nroInventario: Int): Flow<List<ArticuloInventario>> {
        return inventarioRepository.getArticulosInventarioFlow(nroInventario)
    }
    
    /**
     * Obtiene los artículos de un inventario de forma síncrona
     * @param nroInventario Número del inventario
     */
    suspend operator fun invoke(nroInventario: Int): Result<List<ArticuloInventario>> {
        return try {
            val articulos = inventarioRepository.getArticulosInventario(nroInventario)
            Result.success(articulos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
