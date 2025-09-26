package com.gloria.data.service

import com.gloria.data.entity.api.InsertarCabeceraYDetalleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InsertarCabeceraYDetalleApiService {

    @POST("api/oracle/insertar-cabecera-y-detalle")
    suspend fun insertarCabeceraYDetalle(
        @Body request: InsertarCabeceraYDetalleRequest
    ): Response<InsertarCabeceraYDetalleResponse>
}

data class InsertarCabeceraYDetalleResponse(
    val success: Boolean,
    val idCabecera: Int,
    val totalArticulosInsertados: Int,
    val totalArticulosProcesados: Int,
    val message: String
)
