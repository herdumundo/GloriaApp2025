package com.gloria.domain.usecase

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.entity.LoggedUser
import com.gloria.repository.SucursalRepository
import com.gloria.repository.SucursalResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener las sucursales del usuario logueado
 * Reutilizable para diferentes partes de la aplicación
 */
class GetSucursalesUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener las sucursales
     * @return SucursalResult con la lista de sucursales o error
     */
    suspend operator fun invoke(): SucursalResult {
        try {
            // Obtener el usuario logueado de la base de datos local
            val loggedUser = loggedUserRepository.getLoggedUserSync()
            
            if (loggedUser == null) {
                return SucursalResult.Error("No hay usuario logueado")
            }
            
            // Crear repository localmente
            val sucursalRepository = SucursalRepository()
            
            // Usar las credenciales del usuario logueado para consultar Oracle
            return sucursalRepository.getSucursales(
                username = loggedUser.username,
                password = loggedUser.password
            )
            
        } catch (e: Exception) {
            return SucursalResult.Error("Error al obtener sucursales: ${e.message}")
        }
    }
}
