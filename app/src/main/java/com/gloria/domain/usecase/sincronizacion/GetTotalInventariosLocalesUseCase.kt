package com.gloria.domain.usecase.sincronizacion

import com.gloria.data.repository.InventarioSincronizacionRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener el total de inventarios locales
 */
class GetTotalInventariosLocalesUseCase @Inject constructor(
    private val inventarioSincronizacionRepository: InventarioSincronizacionRepository
) {
    suspend operator fun invoke(): Int {
        return inventarioSincronizacionRepository.getTotalInventariosLocales()
    }
}
