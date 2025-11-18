package com.gloria.data.repository

import com.gloria.data.dao.ConteosLogsDao
import com.gloria.data.entity.ConteosLogs
import javax.inject.Inject

/**
 * Repositorio para gestionar los registros de conteos locales.
 */
class ConteosLogsRepository @Inject constructor(
    private val conteosLogsDao: ConteosLogsDao
) {

    suspend fun insertConteoLog(conteoLog: ConteosLogs): Long {
        return conteosLogsDao.insertConteoLog(conteoLog)
    }

    suspend fun deleteConteoLogById(id: Long) {
        conteosLogsDao.deleteConteoLogById(id)
    }

    suspend fun getConteosLogsByArticulo(numeroInventario: Int, secuencia: Int): List<ConteosLogs> {
        return conteosLogsDao.getConteosLogsByArticulo(numeroInventario, secuencia)
    }

    suspend fun getConteosByEstado(estado: String): List<ConteosLogs> {
        return conteosLogsDao.getConteosByEstado(estado)
    }

    suspend fun getMaxOrdenByArticulo(numeroInventario: Int, secuencia: Int): Int? {
        return conteosLogsDao.getMaxOrdenByArticulo(numeroInventario, secuencia)
    }

    suspend fun deleteConteosByInventarioAndEstado(numeroInventario: Int, estado: String) {
        conteosLogsDao.deleteConteosByInventarioAndEstado(numeroInventario, estado)
    }

    suspend fun actualizarEstadoConteosByInventario(numeroInventario: Int, nuevoEstado: String) {
        conteosLogsDao.actualizarEstadoConteosByInventario(numeroInventario, nuevoEstado)
    }

    suspend fun deleteConteosByEstado(estado: String) {
        conteosLogsDao.deleteConteosByEstado(estado)
    }

    suspend fun getConteosByInventarioAndEstado(numeroInventario: Int, estado: String): List<ConteosLogs> {
        return conteosLogsDao.getConteosByInventarioAndEstado(numeroInventario, estado)
    }

    suspend fun getConteosByInventario(numeroInventario: Int): List<ConteosLogs> {
        return conteosLogsDao.getConteosByInventario(numeroInventario)
    }

    suspend fun actualizarEstadoConteosByEstado(estadoActual: String, nuevoEstado: String) {
        conteosLogsDao.actualizarEstadoConteosByEstado(estadoActual, nuevoEstado)
    }
}

