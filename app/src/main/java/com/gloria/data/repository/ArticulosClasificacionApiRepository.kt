package com.gloria.data.repository

import com.gloria.data.entity.api.ArticulosClasificacionResponse
import com.gloria.data.api.ArticulosClasificacionApiService
import android.util.Log
import com.gloria.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para obtener artículos clasificación desde la API
 */
@Singleton
class ArticulosClasificacionApiRepository @Inject constructor(
    private val articulosClasificacionApiService: ArticulosClasificacionApiService
) {
    
    /**
     * Obtiene todos los artículos de todas las familias
     */
    suspend fun getArticulosTodasFamilias(
        userdb: String,
        passdb: String,
        ardeSuc: Int,
        ardeDep: Int,
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int
    ): Result<ArticulosClasificacionResponse> {
        return try {
            Log.d("ArticulosClasificacionApi", "🌐 Llamando API: articulos-clasificacion-por-familias-todas")
            val response = articulosClasificacionApiService.getArticulosTodasFamilias(
                userdb = userdb,
                passdb = passdb,
                ardeSuc = ardeSuc,
                ardeDep = ardeDep,
                areaCodigo = areaCodigo,
                dptoCodigo = dptoCodigo,
                seccCodigo = seccCodigo,
                BuildConfig.TOKEN_BACKEND
            )
            
            if (response.success) {
                Log.d("ArticulosClasificacionApi", "✅ Respuesta exitosa: ${response.length} artículos")
                Result.success(response)
            } else {
                Log.e("ArticulosClasificacionApi", "❌ Error en API: ${response.error}")
                Result.failure(Exception(response.error ?: "Error desconocido en la API"))
            }
        } catch (e: Exception) {
            Log.e("ArticulosClasificacionApi", "❌ Error de red: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene todos los artículos de todos los grupos de una familia específica
     */
    suspend fun getArticulosTodosGruposFamilia(
        userdb: String,
        passdb: String,
        ardeSuc: Int,
        ardeDep: Int,
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int,
        fliaCodigo: Int
    ): Result<ArticulosClasificacionResponse> {
        return try {
            Log.d("ArticulosClasificacionApi", "🌐 Llamando API: articulos-clasificacion-por-todos-los-grupos-de-una-familia")
            val response = articulosClasificacionApiService.getArticulosTodosGruposFamilia(
                userdb = userdb,
                passdb = passdb,
                ardeSuc = ardeSuc,
                ardeDep = ardeDep,
                areaCodigo = areaCodigo,
                dptoCodigo = dptoCodigo,
                seccCodigo = seccCodigo,
                fliaCodigo = fliaCodigo,
                BuildConfig.TOKEN_BACKEND
            )
            
            if (response.success) {
                Log.d("ArticulosClasificacionApi", "✅ Respuesta exitosa: ${response.length} artículos")
                Result.success(response)
            } else {
                Log.e("ArticulosClasificacionApi", "❌ Error en API: ${response.error}")
                Result.failure(Exception(response.error ?: "Error desconocido en la API"))
            }
        } catch (e: Exception) {
            Log.e("ArticulosClasificacionApi", "❌ Error de red: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene artículos de grupos específicos
     */
    suspend fun getArticulosGruposEspecificos(
        userdb: String,
        passdb: String,
        ardeSuc: Int,
        ardeDep: Int,
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int,
        fliaCodigo: Int,
        subgruposSeleccionados: List<Int>,
        gruposCodigos: List<Int>
    ): Result<ArticulosClasificacionResponse> {
        return try {
            Log.d("ArticulosClasificacionApi", "🌐 Llamando API: articulos-clasificacion-grupos-especificos")
            val response = articulosClasificacionApiService.getArticulosGruposEspecificos(
                userdb = userdb,
                passdb = passdb,
                ardeSuc = ardeSuc,
                ardeDep = ardeDep,
                areaCodigo = areaCodigo,
                dptoCodigo = dptoCodigo,
                seccCodigo = seccCodigo,
                fliaCodigo = fliaCodigo,
                subgruposSeleccionados = subgruposSeleccionados,
                gruposCodigos = gruposCodigos,
                token =BuildConfig.TOKEN_BACKEND
            )
            
            if (response.success) {
                Log.d("ArticulosClasificacionApi", "✅ Respuesta exitosa: ${response.length} artículos")
                Result.success(response)
            } else {
                Log.e("ArticulosClasificacionApi", "❌ Error en API: ${response.error}")
                Result.failure(Exception(response.error ?: "Error desconocido en la API"))
            }
        } catch (e: Exception) {
            Log.e("ArticulosClasificacionApi", "❌ Error de red: ${e.message}")
            Result.failure(e)
        }
    }
}
