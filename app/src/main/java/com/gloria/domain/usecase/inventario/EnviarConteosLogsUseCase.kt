package com.gloria.domain.usecase.inventario

import android.util.Log
import com.gloria.data.model.ConteosLogPayload
import com.gloria.data.repository.ConteosLogsApiRepository
import javax.inject.Inject

class EnviarConteosLogsUseCase @Inject constructor(
    private val conteosLogsUseCase: ConteosLogsUseCase,
    private val conteosLogsApiRepository: ConteosLogsApiRepository
) {

    suspend operator fun invoke(): Result<Int> {
        return try {
            val logs = conteosLogsUseCase.getByEstado("P")
            if (logs.isEmpty()) {
                Log.d("EnviarConteosLogs", "No hay conteos con estado 'P' para enviar.")
                return Result.success(0)
            }

            val payload = logs.map { log ->
                ConteosLogPayload(
                    id = log.id.toString(),
                    orden = log.orden,
                    winvdNroInv = log.winvd_nro_inv,
                    winvdSecu = log.winvd_secu,
                    winvdArt = log.winvdArt,
                    winvdLote = log.winvdLote,
                    usuario = log.usuario,
                    createdAt = log.createdAt,
                    cantidadIngresada = log.cantidadIngresada,
                    cantidadConvertida = log.cantidadConvertida,
                    tipo = log.tipo
                )
            }

            val response = conteosLogsApiRepository.enviarConteosLogs(payload)
            Log.d("EnviarConteosLogs", "Resultado envío logs -> success=${response.success}, totalInserted=${response.totalInserted}")

            if (response.success) {
                conteosLogsUseCase.actualizarEstadoPorEstado("P", "C")
            }

            Result.success(response.totalInserted)
        } catch (e: Exception) {
            Log.e("EnviarConteosLogs", "Error al enviar conteos logs", e)
            Result.failure(e)
        }
    }
}

