package com.gloria.domain.usecase.permission

import com.gloria.data.repository.UserPermissionRepository
import javax.inject.Inject

/**
 * UseCase para verificar permisos de usuario
 */
class CheckUserPermissionUseCase @Inject constructor(
    private val userPermissionRepository: UserPermissionRepository
) {
    
    /**
     * Verifica si un usuario tiene permiso para una pantalla específica
     */
    suspend operator fun invoke(username: String, screenId: String): Boolean {
        return userPermissionRepository.hasScreenPermission(username, screenId)
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico por código de formulario
     */
    suspend operator fun invoke(username: String, formulario: String, isFormulario: Boolean = true): Boolean {
        return if (isFormulario) {
            userPermissionRepository.hasPermission(username, formulario)
        } else {
            userPermissionRepository.hasScreenPermission(username, formulario)
        }
    }
}
