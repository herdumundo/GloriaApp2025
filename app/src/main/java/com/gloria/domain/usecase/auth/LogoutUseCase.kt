package com.gloria.domain.usecase.auth

import com.gloria.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para cerrar sesión del usuario
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    /**
     * Ejecuta el logout del usuario
     */
    suspend operator fun invoke() {
        try {
            authRepository.logout()
        } catch (e: Exception) {
            // Log del error si es necesario
        }
    }
}
