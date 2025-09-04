package com.gloria.domain.usecase.sincronizacion

import com.gloria.data.repository.InventarioSincronizacionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para sincronizar inventarios desde Oracle
 */
class SincronizarInventariosUseCase @Inject constructor(
    private val inventarioSincronizacionRepository: InventarioSincronizacionRepository
) {
    operator fun invoke(
        onProgressUpdate: (String, Int, Int) -> Unit
    ): Flow<Result<Int>> {
        return inventarioSincronizacionRepository.sincronizarInventarios(onProgressUpdate)
    }
}
