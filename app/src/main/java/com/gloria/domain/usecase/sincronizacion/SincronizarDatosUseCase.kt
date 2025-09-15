package com.gloria.domain.usecase.sincronizacion

import com.gloria.repository.SincronizacionCompletaRepository
import javax.inject.Inject

/**
 * Caso de uso para sincronizar todos los datos desde Oracle
 */
class SincronizarDatosUseCase @Inject constructor(
    private val sincronizacionCompletaRepository: SincronizacionCompletaRepository
) {
    suspend operator fun invoke(
        onProgress: (message: String, current: Int, total: Int) -> Unit = { _, _, _ -> }
    ): Result<com.gloria.repository.SincronizacionResult> {
        return try {
            val result = sincronizacionCompletaRepository.sincronizarTodasLasTablas(onProgress)
            if (result.isSuccess) {
                Result.success(result.getOrNull()!!)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
