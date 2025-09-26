package com.gloria.data.service

import com.gloria.data.entity.api.ArticulosClasificacionResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio API para obtener artículos clasificación
 */
interface ArticulosClasificacionApiService {
    
    /**
     * Obtiene todos los artículos de todas las familias
     */
    @GET("api/oracle/articulos-clasificacion-por-familias-todas")
    suspend fun getArticulosTodasFamilias(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("ardeSuc") ardeSuc: Int,
        @Query("ardeDep") ardeDep: Int,
        @Query("areaCodigo") areaCodigo: Int,
        @Query("dptoCodigo") dptoCodigo: Int,
        @Query("seccCodigo") seccCodigo: Int
    ): ArticulosClasificacionResponse
    
    /**
     * Obtiene todos los artículos de todos los grupos de una familia específica
     */
    @GET("api/oracle/articulos-clasificacion-por-todos-los-grupos-de-una-familia")
    suspend fun getArticulosTodosGruposFamilia(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("ardeSuc") ardeSuc: Int,
        @Query("ardeDep") ardeDep: Int,
        @Query("areaCodigo") areaCodigo: Int,
        @Query("dptoCodigo") dptoCodigo: Int,
        @Query("seccCodigo") seccCodigo: Int,
        @Query("fliaCodigo") fliaCodigo: Int
    ): ArticulosClasificacionResponse
    
    /**
     * Obtiene artículos de grupos específicos
     */
    @GET("api/oracle/articulos-clasificacion-grupos-especificos")
    suspend fun getArticulosGruposEspecificos(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("ardeSuc") ardeSuc: Int,
        @Query("ardeDep") ardeDep: Int,
        @Query("areaCodigo") areaCodigo: Int,
        @Query("dptoCodigo") dptoCodigo: Int,
        @Query("seccCodigo") seccCodigo: Int,
        @Query("fliaCodigo") fliaCodigo: Int,
        @Query("subgruposSeleccionados") subgruposSeleccionados: List<Int>,
        @Query("gruposCodigos") gruposCodigos: List<Int>
    ): ArticulosClasificacionResponse
}
