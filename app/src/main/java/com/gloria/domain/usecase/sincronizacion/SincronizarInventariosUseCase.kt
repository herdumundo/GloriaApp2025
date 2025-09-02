package com.gloria.domain.usecase.sincronizacion

import com.gloria.repository.AuthRepository
import com.gloria.data.repository.InventarioSincronizacionRepository
import javax.inject.Inject

/**
 * Caso de uso para sincronizar solo los inventarios
 */
class SincronizarInventariosUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val inventarioSincronizacionRepository: InventarioSincronizacionRepository
) {
    
    /**
     * Sincroniza los inventarios desde Oracle
     */
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // Obtener usuario logueado
            val loggedUser = authRepository.getLoggedUser()
            if (loggedUser == null) {
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // Sincronizar inventarios
            val syncFlow = inventarioSincronizacionRepository.sincronizarInventarios { message, current, total ->
                // Callback para actualizar progreso
            }
            
            // Obtener el resultado del Flow
            var syncResult: Result<Int>? = null
            syncFlow.collect { result ->
                syncResult = result
            }
            
            if (syncResult?.isSuccess == true) {
                Result.success(Unit)
            } else {
                Result.failure(syncResult?.exceptionOrNull() ?: Exception("Error al sincronizar inventarios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
