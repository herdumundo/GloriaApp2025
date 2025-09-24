package com.gloria.domain.usecase.permission

import com.gloria.data.repository.UserPermissionRepository
import com.gloria.domain.model.MenuItem
import com.gloria.domain.model.MenuItems
import javax.inject.Inject

/**
 * UseCase para obtener las pantallas permitidas para un usuario
 */
class GetUserAllowedScreensUseCase @Inject constructor(
    private val userPermissionRepository: UserPermissionRepository
) {
    
    /**
     * Obtiene todas las pantallas permitidas para un usuario
     */
    suspend operator fun invoke(username: String): List<MenuItem> {
        val allowedScreenIds = userPermissionRepository.getAllowedScreens(username)
        return MenuItems.items.filter { menuItem ->
            allowedScreenIds.contains(menuItem.id)
        }
    }
}
