package com.gloria.domain.usecase.sincronizacion

import com.gloria.repository.SincronizacionCompletaRepository
import com.gloria.repository.EstadisticasSincronizacion
import javax.inject.Inject

/**
 * Caso de uso para obtener estadísticas de sincronización
 */
class GetEstadisticasSincronizacionUseCase @Inject constructor(
    private val sincronizacionCompletaRepository: SincronizacionCompletaRepository
) {
    suspend operator fun invoke(): EstadisticasSincronizacion {
        return sincronizacionCompletaRepository.getEstadisticasSincronizacion()
    }
}
