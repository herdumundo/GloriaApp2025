package com.gloria.domain.usecase.inventario

import com.gloria.data.repository.InventarioDetalleRepository
import javax.inject.Inject

/**
 * Caso de uso para actualizar estado de inventario
 */
class ActualizarEstadoInventarioUseCase @Inject constructor(
    private val inventarioDetalleRepository: InventarioDetalleRepository
) {
    suspend operator fun invoke(
        numeroInventario: Int,
        estado: String
    ) {
        return inventarioDetalleRepository.actualizarEstadoInventario(
            numeroInventario, estado
        )
    }
}
