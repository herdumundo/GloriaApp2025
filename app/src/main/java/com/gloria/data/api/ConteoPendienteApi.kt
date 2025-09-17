package com.gloria.data.api

import com.gloria.data.model.ConteoPendienteResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API interface para consultar conteos pendientes por fecha
 */
interface ConteoPendienteApi {
    
    /**
     * Obtiene conteos pendientes agrupados por fecha
     * @param fecha Fecha en formato YYYY-MM-DD
     * @return Respuesta con cabecera y detalles del conteo
     */
    @GET("api/stkw002inv/by-date-grouped/{fecha}")
    suspend fun getConteosPendientesByDate(@Path("fecha") fecha: String): ConteoPendienteResponse
}
