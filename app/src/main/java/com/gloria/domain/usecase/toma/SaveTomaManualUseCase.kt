package com.gloria.domain.usecase.toma

import com.gloria.data.model.ArticuloLote
import com.gloria.repository.AuthRepository
import com.gloria.repository.InventarioRepository
import com.gloria.repository.SincronizacionCompletaRepository
import javax.inject.Inject

/**
 * Caso de uso para guardar una toma manual
 */
class SaveTomaManualUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val inventarioRepository: InventarioRepository,
    private val sincronizacionRepository: SincronizacionCompletaRepository
) {
    
        /**
     * Guarda una toma manual de artículos
     * @param articulosSeleccionados Lista de artículos seleccionados
     * @param nroInventario Número del inventario (opcional, se crea uno nuevo si es null)
     */
    suspend operator fun invoke(
        articulosSeleccionados: List<ArticuloLote>,
        nroInventario: Int? = null
    ): Result<Int> {
        return try {
            // Obtener usuario logueado
            val loggedUser = authRepository.getLoggedUser()
            if (loggedUser == null) {
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // Determinar número de inventario
            val inventarioNum = nroInventario ?: generateInventarioNumber()
            
            // Guardar toma manual a través del repositorio
            val saveResult = inventarioRepository.saveTomaManual(
                articulosSeleccionados = articulosSeleccionados,
                nroInventario = inventarioNum,
                username = loggedUser.username
            )
            
            if (saveResult.isSuccess) {
                // Sincronizar con Oracle
                val syncResult = sincronizacionRepository.sincronizarTodasLasTablas()
                
                if (syncResult.isSuccess) {
                    Result.success(inventarioNum)
                } else {
                    Result.failure(syncResult.exceptionOrNull() ?: Exception("Error al sincronizar toma manual"))
                }
            } else {
                Result.failure(saveResult.exceptionOrNull() ?: Exception("Error al guardar toma manual"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Genera un número de inventario único
     */
    private suspend fun generateInventarioNumber(): Int {
        return inventarioRepository.generateInventarioNumber()
    }
}
