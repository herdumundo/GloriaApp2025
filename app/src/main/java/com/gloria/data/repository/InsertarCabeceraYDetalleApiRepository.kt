package com.gloria.data.repository

import com.gloria.data.entity.api.InsertarCabeceraYDetalleRequest
import com.gloria.data.service.InsertarCabeceraYDetalleApiService
import com.gloria.data.service.InsertarCabeceraYDetalleResponse
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertarCabeceraYDetalleApiRepository @Inject constructor(
    private val apiService: InsertarCabeceraYDetalleApiService
) {
    
    suspend fun insertarCabeceraYDetalle(
        request: InsertarCabeceraYDetalleRequest
    ): Result<InsertarCabeceraYDetalleResponse> {
        return try {
            Log.d("InsertarCabeceraYDetalleApi", "🌐 Enviando inserción al backend...")
            Log.d("InsertarCabeceraYDetalleApi", "📊 Cabecera: sucursal=${request.cabecera.sucursal}, deposito=${request.cabecera.deposito}")
            Log.d("InsertarCabeceraYDetalleApi", "📋 Detalles: ${request.detalle.size} elementos")
            
            val response = apiService.insertarCabeceraYDetalle(request)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d("InsertarCabeceraYDetalleApi", "✅ Inserción exitosa: ${body.message}")
                Result.success(body)
            } else {
                val errorMsg = "HTTP Error: ${response.code()} - ${response.message()}"
                Log.e("InsertarCabeceraYDetalleApi", "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("InsertarCabeceraYDetalleApi", "❌ Error en inserción: ${e.message}")
            Result.failure(e)
        }
    }
}
