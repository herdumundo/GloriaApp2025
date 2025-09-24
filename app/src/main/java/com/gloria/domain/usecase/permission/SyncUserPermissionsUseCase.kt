package com.gloria.domain.usecase.permission

import com.gloria.data.repository.UserPermissionRepository
import javax.inject.Inject

/**
 * UseCase para sincronizar permisos de usuario
 */
class SyncUserPermissionsUseCase @Inject constructor(
    private val userPermissionRepository: UserPermissionRepository
) {
    
    /**
     * Sincroniza los permisos de un usuario desde una lista de códigos de formulario
     */
    suspend operator fun invoke(username: String, formularios: List<String>, nombre: String = "INVENTARIO") {
        userPermissionRepository.syncUserPermissions(username, formularios, nombre)
    }
    
    /**
     * Sincroniza los permisos de un usuario desde el resultado de la consulta Oracle
     * formato: lista de pares (formulario, nombre)
     */
    suspend operator fun invoke(username: String, formulariosConNombre: List<Pair<String, String>>) {
        userPermissionRepository.syncUserPermissionsFromOracle(username, formulariosConNombre)
    }
}
