package com.gloria.data.repository

import android.util.Log
import com.gloria.data.entity.api.InsertarConteosRequest
import com.gloria.data.entity.api.InsertarConteosResponse
import com.gloria.data.service.InsertarConteosApiService
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertarConteosApiRepository @Inject constructor(
    private val apiService: InsertarConteosApiService
) {
    
    suspend fun insertarConteos(request: InsertarConteosRequest): Result<InsertarConteosResponse> {
        return try {
            Log.d("InsertarConteosApi", "🌐 Enviando conteos al backend...")
            Log.d("InsertarConteosApi", "📊 Total conteos: ${request.conteos.size}")
            Log.d("InsertarConteosApi", "👤 Usuario: ${request.userdb}")

            // Log completo del payload JSON que se envía
            try {
                val json = Gson().toJson(request)
                Log.d("InsertarConteosApi", "📦 Payload JSON → ${json}")
            } catch (e: Exception) {
                Log.w("InsertarConteosApi", "⚠️ No se pudo serializar el payload a JSON para logging: ${e.message}")
            }
            
            val response = apiService.insertarConteos(request)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d("InsertarConteosApi", "✅ Respuesta exitosa: ${body.message}")
                Log.d("InsertarConteosApi", "📈 Total conteos: ${body.totalConteos}")
                Log.d("InsertarConteosApi", "📋 Conteos insertados: ${body.conteosInsertados.size}")
                body.conteosInsertados.forEach { ci ->
                    Log.d(
                        "InsertarConteosApi",
                        "#${ci.conteoIndex} idCab=${ci.idCabecera}, det=${ci.detallesInsertados}/total=${ci.totalDetalles}, padre=${ci.inventarioPadreActualizado}, filasPadre=${ci.filasActualizadasPadre}"
                    )
                }

                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("API Error: ${body.message}"))
                }
            } else {
                val errorMsg = "HTTP Error: ${response.code()} - ${response.message()}"
                Log.e("InsertarConteosApi", "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("InsertarConteosApi", "❌ Error en inserción: ${e.message}")
            Result.failure(e)
        }
    }
}
