package com.gloria.domain.usecase.cancelacion

import com.gloria.data.repository.CancelacionTomaRepository
import javax.inject.Inject

class CancelarTomaTotalUseCase @Inject constructor(
    private val cancelacionTomaRepository: CancelacionTomaRepository
) {
    suspend operator fun invoke(nroToma: Int, userLogin: String): Result<Int> {
        return try {
            val result = cancelacionTomaRepository.cancelarTomaTotal(nroToma, userLogin)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
