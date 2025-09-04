package com.gloria.domain.usecase.toma

import com.gloria.data.entity.LoggedUser
import com.gloria.data.repository.LoggedUserRepository
import javax.inject.Inject

class GetLoggedUserUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    suspend operator fun invoke(): LoggedUser? {
        return loggedUserRepository.getLoggedUserSync()
    }
}
