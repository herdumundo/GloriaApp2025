package com.gloria.domain.usecase.cancelacion

import com.gloria.data.model.CancelacionToma
import com.gloria.data.repository.CancelacionTomaRepository
import javax.inject.Inject

class GetCancelacionesTomaUseCase @Inject constructor(
    private val cancelacionTomaRepository: CancelacionTomaRepository
) {
    suspend operator fun invoke(userLogin: String): List<CancelacionToma> {
        return cancelacionTomaRepository.getCancelacionesToma(userLogin)
    }
}