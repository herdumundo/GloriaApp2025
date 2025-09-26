package com.gloria.domain.usecase.auth

import com.gloria.data.repository.LoggedUserRepository
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
            // Solo necesitamos limpiar la sesión local, no hacer llamadas a API
            loggedUserRepository.clearLoggedUsers()
        } catch (e: Exception) {
            // Log del error si es necesario
        }
    }
}
