package com.gloria.data.api

import com.gloria.data.model.ConteoPendienteResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API interface para consultar conteos pendientes por fecha
 */
interface ConteoPendienteApi {
    
    /**
     * Obtiene conteos pendientes agrupados por inventario individual
     * @param fecha Fecha en formato YYYY-MM-DD
     * @param token Token de autenticación
     * @return Respuesta con lista de inventarios, cada uno con su cabecera y detalles
     */
    @GET("api/stkw002inv/by-date-grouped-by-inventory/{fecha}")
    suspend fun getConteosPendientesByDate(
        @Path("fecha") fecha: String,
        @Query("token") token: String
    ): ConteoPendienteResponse
}
