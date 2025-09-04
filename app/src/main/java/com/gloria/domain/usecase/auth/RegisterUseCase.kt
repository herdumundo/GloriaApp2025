package com.gloria.domain.usecase.auth

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.repository.AuthRepository
import com.gloria.repository.AuthResult
import javax.inject.Inject

/**
 * Caso de uso para registrar un nuevo usuario
 */
class RegisterUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    
    /**
     * Ejecuta el registro de un nuevo usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return AuthResult con el resultado del registro
     */
    suspend operator fun invoke(username: String, password: String): AuthResult {
        return try {
            val authRepository = AuthRepository(loggedUserRepository)
            authRepository.registerUser(username, password)
        } catch (e: Exception) {
            AuthResult.Error("Error durante el registro: ${e.message}")
        }
    }
}
