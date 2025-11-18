package com.gloria.domain.usecase.inventario

import android.util.Log
import com.gloria.data.model.ConteosLogPayload
import com.gloria.data.repository.ConteosLogsConsultaRepository
import javax.inject.Inject

class GetConteosLogsRemotosUseCase @Inject constructor(
    private val conteosLogsConsultaRepository: ConteosLogsConsultaRepository
) {

    suspend operator fun invoke(numeroInventario: Int): Result<List<ConteosLogPayload>> {
        return try {
            val logs = conteosLogsConsultaRepository.obtenerConteosPorInventario(numeroInventario)
            Log.d("GetConteosLogsRemotos", "Recibidos ${logs.size} logs remotos para inventario $numeroInventario")
            Result.success(logs)
        } catch (e: Exception) {
            Log.e("GetConteosLogsRemotos", "Error al consultar logs remotos", e)
            Result.failure(e)
        }
    }
}

