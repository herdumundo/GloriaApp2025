package com.gloria.domain.usecase.inventario

import com.gloria.repository.ConteoRepository
import com.gloria.repository.SincronizacionCompletaRepository
import javax.inject.Inject

/**
 * Caso de uso para guardar el conteo completo de un inventario
 */
class SaveConteoUseCase @Inject constructor(
    private val conteoRepository: ConteoRepository,
    private val sincronizacionRepository: SincronizacionCompletaRepository
) {
    
    /**
     * Guarda el conteo completo de un inventario
     * @param nroInventario Número del inventario
     */
    suspend operator fun invoke(nroInventario: Int): Result<Unit> {
        return try {
            // Obtener usuario logueado
            val loggedUser = conteoRepository.getLoggedUser()
            if (loggedUser == null) {
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // Marcar inventario como completado
            conteoRepository.marcarInventarioCompletado(nroInventario)
            
            // Sincronizar con Oracle
            val syncResult = sincronizacionRepository.sincronizarTodasLasTablas()
            
            if (syncResult.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(syncResult.exceptionOrNull() ?: Exception("Error al sincronizar conteo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
