package com.gloria.domain.usecase.inventario

import com.gloria.data.model.InventarioCard
import com.gloria.repository.InventarioRepository
import com.gloria.repository.SincronizacionCompletaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener los inventarios disponibles
 */
class GetInventariosUseCase @Inject constructor(
    private val inventarioRepository: InventarioRepository,
    private val sincronizacionRepository: SincronizacionCompletaRepository
) {
    
    /**
     * Obtiene los inventarios como Flow para observación reactiva
     */
    fun getInventariosFlow(): Flow<List<InventarioCard>> {
        return inventarioRepository.getInventariosFlow()
    }
    
    /**
     * Refresca los inventarios desde Oracle
     */
    suspend operator fun invoke(): Result<List<InventarioCard>> {
        return try {
            // Obtener usuario logueado
            val loggedUser = inventarioRepository.getLoggedUser()
            if (loggedUser == null) {
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // Sincronizar inventarios desde Oracle
            val syncResult = sincronizacionRepository.sincronizarTodasLasTablas()
            
            if (syncResult.isSuccess) {
                // Obtener inventarios actualizados
                val inventarios = inventarioRepository.getInventarios()
                Result.success(inventarios)
            } else {
                Result.failure(syncResult.exceptionOrNull() ?: Exception("Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
