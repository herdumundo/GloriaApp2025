package com.gloria.domain.usecase.sincronizacion

import com.gloria.repository.AuthRepository
import com.gloria.repository.SincronizacionCompletaRepository
import com.gloria.data.repository.InventarioSincronizacionRepository
import javax.inject.Inject

/**
 * Caso de uso para sincronizar todos los datos maestros
 */
class SincronizarDatosUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sincronizacionRepository: SincronizacionCompletaRepository,
    private val inventarioSincronizacionRepository: InventarioSincronizacionRepository
) {
    
    /**
     * Sincroniza todos los datos maestros desde Oracle
     */
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // Obtener usuario logueado
            val loggedUser = authRepository.getLoggedUser()
            if (loggedUser == null) {
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // Sincronizar datos maestros
            val syncResult = sincronizacionRepository.sincronizarTodasLasTablas()
            
            if (syncResult.isSuccess) {
                // Sincronizar inventarios
                val inventarioSyncFlow = inventarioSincronizacionRepository.sincronizarInventarios { message, current, total ->
                    // Callback para actualizar progreso
                }
                
                // Obtener el resultado del Flow
                var inventarioSyncResult: Result<Int>? = null
                inventarioSyncFlow.collect { result ->
                    inventarioSyncResult = result
                }
                
                if (inventarioSyncResult?.isSuccess == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(inventarioSyncResult?.exceptionOrNull() ?: Exception("Error al sincronizar inventarios"))
                }
            } else {
                Result.failure(syncResult.exceptionOrNull() ?: Exception("Error al sincronizar datos maestros"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
