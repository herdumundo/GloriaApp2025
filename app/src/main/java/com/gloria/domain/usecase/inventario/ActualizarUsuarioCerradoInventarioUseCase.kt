package com.gloria.domain.usecase.inventario

import com.gloria.data.repository.InventarioDetalleRepository
import javax.inject.Inject

class ActualizarUsuarioCerradoInventarioUseCase @Inject constructor(
    private val inventarioDetalleRepository: InventarioDetalleRepository
) {

    suspend operator fun invoke(
        numeroInventario: Int,
        usuarioCerrado: String
    ) {
        inventarioDetalleRepository.actualizarUsuarioCerradoPorInventario(
            numeroInventario,
            usuarioCerrado
        )
    }
}
