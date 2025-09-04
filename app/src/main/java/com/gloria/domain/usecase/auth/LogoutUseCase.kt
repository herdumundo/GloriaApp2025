package com.gloria.domain.usecase.auth

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para cerrar sesión del usuario
 */
class LogoutUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    
    /**
     * Ejecuta el logout del usuario
     */
    suspend operator fun invoke() {
        try {
            val authRepository = AuthRepository(loggedUserRepository)
            authRepository.logout()
        } catch (e: Exception) {
            // Log del error si es necesario
        }
    }
}
