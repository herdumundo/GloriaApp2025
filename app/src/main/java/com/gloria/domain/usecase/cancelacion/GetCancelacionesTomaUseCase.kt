package com.gloria.domain.usecase.cancelacion

import com.gloria.data.model.CancelacionToma
import com.gloria.repository.CancelacionTomaRepository
import javax.inject.Inject

class GetCancelacionesTomaUseCase @Inject constructor(
    private val cancelacionTomaRepository: CancelacionTomaRepository
) {
    suspend operator fun invoke(userLogin: String): Result<List<CancelacionToma>> {
        return try {
            val cancelaciones = cancelacionTomaRepository.getCancelacionesToma(userLogin)
            Result.success(cancelaciones)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}