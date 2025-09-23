package com.gloria.domain.usecase

import com.gloria.data.repository.AuthSessionRepository
import javax.inject.Inject

class AuthSessionUseCase @Inject constructor(
    private val authSessionRepository: AuthSessionRepository
) {
    
    suspend fun saveUserSession(username: String, password: String, sucursalId: Int? = null, sucursalNombre: String? = null, modoDark: Boolean = false) {
        authSessionRepository.saveUserSession(username, password, sucursalId, sucursalNombre, modoDark)
    }

    suspend fun updateModoDark(modoDark: Boolean) {
        authSessionRepository.updateModoDark(modoDark)
    }
    
    suspend fun getCurrentUser() = authSessionRepository.getCurrentUserSync()
    
    suspend fun isUserLoggedIn() = authSessionRepository.isUserLoggedIn()
    
    suspend fun clearUserSession() {
        authSessionRepository.clearUserSession()
    }
    
    fun getCurrentUserFlow() = authSessionRepository.getCurrentUser()
}
