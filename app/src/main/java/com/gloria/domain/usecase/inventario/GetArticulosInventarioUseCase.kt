package com.gloria.domain.usecase.inventario

import com.gloria.data.model.ArticuloInventario
import com.gloria.data.repository.InventarioDetalleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener artículos de un inventario
 */
class GetArticulosInventarioUseCase @Inject constructor(
    private val inventarioDetalleRepository: InventarioDetalleRepository
) {
    operator fun invoke(nroInventario: Int): Flow<List<ArticuloInventario>> {
        return inventarioDetalleRepository.getArticulosInventario(nroInventario)
    }
}
