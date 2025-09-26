package com.gloria.domain.usecase.auth

import com.gloria.repository.AuthResult
import javax.inject.Inject

/**
 * Caso de uso para registrar un nuevo usuario
 * Nota: El registro de usuarios no está disponible ya que los usuarios se crean en Oracle
 */
class RegisterUseCase @Inject constructor() {
    
    /**
     * Ejecuta el registro de un nuevo usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return AuthResult con el resultado del registro
     */
    suspend operator fun invoke(username: String, password: String): AuthResult {
        return AuthResult.Error("El registro de usuarios no está disponible. Los usuarios deben ser creados en Oracle.")
    }
}
