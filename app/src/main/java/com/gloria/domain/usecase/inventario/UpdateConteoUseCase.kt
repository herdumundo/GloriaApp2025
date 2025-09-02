package com.gloria.domain.usecase.inventario

import com.gloria.repository.ConteoRepository
import javax.inject.Inject

/**
 * Caso de uso para actualizar el conteo de un artículo
 */
class UpdateConteoUseCase @Inject constructor(
    private val conteoRepository: ConteoRepository
) {
    
    /**
     * Actualiza el conteo de un artículo en el inventario
     * @param nroInventario Número del inventario
     * @param codigoArticulo Código del artículo
     * @param cantidadContada Nueva cantidad contada
     * @param observaciones Observaciones del conteo
     */
    suspend operator fun invoke(
        nroInventario: Int,
        codigoArticulo: String,
        cantidadContada: Double,
        observaciones: String? = null
    ): Result<Unit> {
        return conteoRepository.updateConteo(
            nroInventario = nroInventario,
            codigoArticulo = codigoArticulo,
            cantidadContada = cantidadContada,
            observaciones = observaciones
        )
    }
}
