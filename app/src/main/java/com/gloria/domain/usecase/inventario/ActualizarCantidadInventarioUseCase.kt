package com.gloria.domain.usecase.inventario

import com.gloria.data.repository.InventarioDetalleRepository
import javax.inject.Inject

/**
 * Caso de uso para actualizar cantidad de inventario
 */
class ActualizarCantidadInventarioUseCase @Inject constructor(
    private val inventarioDetalleRepository: InventarioDetalleRepository
) {
    suspend operator fun invoke(
        numeroInventario: Int,
        secuencia: Int,
        cantidad: Int,
        estado: String,
        usuarioCerrado: String
    ) {
        return inventarioDetalleRepository.actualizarCantidadInventario(
            numeroInventario, secuencia, cantidad, estado, usuarioCerrado
        )
    }
}
