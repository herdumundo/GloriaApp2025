package com.gloria.domain.usecase.cancelacion

import com.gloria.data.repository.CancelacionTomaRepository
import javax.inject.Inject

class CancelarTomaParcialUseCase @Inject constructor(
    private val cancelacionTomaRepository: CancelacionTomaRepository
) {
    suspend operator fun invoke(nroToma: Int, secuencias: List<String>): Result<Int> {
        return try {
            val result = cancelacionTomaRepository.cancelarTomaParcial(nroToma, secuencias)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}