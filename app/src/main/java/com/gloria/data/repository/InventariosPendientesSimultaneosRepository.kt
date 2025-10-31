package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.api.InventariosPendientesSimultaneosApiService
import com.gloria.data.entity.api.InventarioPendienteSimultaneo
import com.gloria.data.repository.LoggedUserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar inventarios pendientes simultáneos
 */
@Singleton
class InventariosPendientesSimultaneosRepository @Inject constructor(
    private val apiService: InventariosPendientesSimultaneosApiService,
    private val loggedUserRepository: LoggedUserRepository
) {
    
    /**
     * Obtiene la lista de inventarios pendientes simultáneos
     */
    suspend fun getInventariosPendientesSimultaneos(): Result<List<InventarioPendienteSimultaneo>> {
        return try {
            val loggedUser = loggedUserRepository.getLoggedUserSync()
            
            val response = apiService.getInventariosPendientesSimultaneos(
                userdb = loggedUser?.username ?: "",
                passdb = loggedUser?.password ?: "",
                token = BuildConfig.TOKEN_BACKEND,
                idSucursal = loggedUser?.sucursalId ?: 0,
            )
            
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                if (responseBody.success) {
                    Result.success(responseBody.data)
                } else {
                    Result.failure(Exception(responseBody.message))
                }
            } else {
                Result.failure(Exception("Error en la respuesta del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
