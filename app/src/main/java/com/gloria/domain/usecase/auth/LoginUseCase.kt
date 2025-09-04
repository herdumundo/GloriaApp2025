package com.gloria.domain.usecase.auth

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.repository.AuthRepository
import com.gloria.repository.AuthResult
import javax.inject.Inject

/**
 * Caso de uso para autenticar un usuario
 */
class LoginUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    
    /**
     * Ejecuta el login del usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return AuthResult con el resultado de la autenticación
     */
    suspend operator fun invoke(username: String, password: String): AuthResult {
        return try {
            val authRepository = AuthRepository(loggedUserRepository)
            authRepository.authenticateUser(username, password)
        } catch (e: Exception) {
            AuthResult.Error("Error durante el login: ${e.message}")
        }
    }
}
