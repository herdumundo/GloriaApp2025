package com.gloria.domain.usecase.inventario

import com.gloria.data.entity.ConteosLogs
import com.gloria.data.repository.ConteosLogsRepository
import javax.inject.Inject

/**
 * Caso de uso para gestionar los registros de ConteosLogs.
 */
class ConteosLogsUseCase @Inject constructor(
    private val conteosLogsRepository: ConteosLogsRepository
) {

    suspend fun insert(conteoLog: ConteosLogs): Long {
        return conteosLogsRepository.insertConteoLog(conteoLog)
    }

    suspend fun deleteById(id: Long) {
        conteosLogsRepository.deleteConteoLogById(id)
    }

    suspend fun getByArticulo(numeroInventario: Int, secuencia: Int): List<ConteosLogs> {
        return conteosLogsRepository.getConteosLogsByArticulo(numeroInventario, secuencia)
    }

    suspend fun getByEstado(estado: String): List<ConteosLogs> {
        return conteosLogsRepository.getConteosByEstado(estado)
    }

    suspend fun getByInventario(numeroInventario: Int): List<ConteosLogs> {
        return conteosLogsRepository.getConteosByInventario(numeroInventario)
    }

    suspend fun getNextOrden(numeroInventario: Int, secuencia: Int): Int {
        val maxOrden = conteosLogsRepository.getMaxOrdenByArticulo(numeroInventario, secuencia) ?: 0
        return maxOrden + 1
    }

    suspend fun deleteByInventarioWithEstado(numeroInventario: Int, estado: String = "N") {
        conteosLogsRepository.deleteConteosByInventarioAndEstado(numeroInventario, estado)
    }

    suspend fun actualizarEstadoPorInventario(numeroInventario: Int, nuevoEstado: String) {
        conteosLogsRepository.actualizarEstadoConteosByInventario(numeroInventario, nuevoEstado)
    }

    suspend fun getByInventarioAndEstado(numeroInventario: Int, estado: String): List<ConteosLogs> {
        return conteosLogsRepository.getConteosByInventarioAndEstado(numeroInventario, estado)
    }

    suspend fun deleteByEstado(estado: String) {
        conteosLogsRepository.deleteConteosByEstado(estado)
    }

    suspend fun actualizarEstadoPorEstado(estadoActual: String, nuevoEstado: String) {
        conteosLogsRepository.actualizarEstadoConteosByEstado(estadoActual, nuevoEstado)
    }
}

