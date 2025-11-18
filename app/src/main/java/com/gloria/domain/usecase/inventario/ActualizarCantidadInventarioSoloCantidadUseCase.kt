package com.gloria.domain.usecase.inventario

import com.gloria.data.repository.InventarioDetalleRepository
import javax.inject.Inject

/**
 * Caso de uso para actualizar únicamente la cantidad inventariada de un artículo en STKW002INV.
 */
class ActualizarCantidadInventarioSoloCantidadUseCase @Inject constructor(
    private val inventarioDetalleRepository: InventarioDetalleRepository
) {

    suspend operator fun invoke(
        numeroInventario: Int,
        secuencia: Int,
        codigoArticulo: String,
        cantidad: Int
    ) {
        return inventarioDetalleRepository.actualizarCantidadInventarioSoloCantidad(
            numeroInventario,
            secuencia,
            codigoArticulo,
            cantidad
        )
    }
}

