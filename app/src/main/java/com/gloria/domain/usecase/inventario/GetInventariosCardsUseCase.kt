package com.gloria.domain.usecase.inventario

import com.gloria.data.model.InventarioCard
import com.gloria.data.repository.InventarioDetalleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener inventarios cards
 */
class GetInventariosCardsUseCase @Inject constructor(
    private val inventarioDetalleRepository: InventarioDetalleRepository
) {
    operator fun invoke(sucursal: Int): Flow<List<InventarioCard>> {
        return inventarioDetalleRepository.getInventariosCardsDistinct(sucursal)
    }
}
