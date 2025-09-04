package com.gloria.domain.usecase.auth

import com.gloria.data.entity.LoggedUser
import com.gloria.data.repository.LoggedUserRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener el usuario logueado
 */
class GetLoggedUserSyncUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    suspend operator fun invoke(): LoggedUser? {
        return loggedUserRepository.getLoggedUserSync()
    }
}
