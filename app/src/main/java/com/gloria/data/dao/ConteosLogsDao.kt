package com.gloria.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gloria.data.entity.ConteosLogs

/**
 * DAO para la entidad ConteosLogs.
 */
@Dao
interface ConteosLogsDao {

    /**
     * Inserta un registro de conteo log.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConteoLog(conteoLog: ConteosLogs): Long

    /**
     * Elimina un registro de conteo log por id.
     */
    @Query("DELETE FROM conteos_logs WHERE id = :id")
    suspend fun deleteConteoLogById(id: Long)

    /**
     * Elimina todos los registros de un inventario con estado específico.
     */
    @Query("DELETE FROM conteos_logs WHERE winvd_nro_inv = :numeroInventario AND estado = :estado")
    suspend fun deleteConteosByInventarioAndEstado(numeroInventario: Int, estado: String)

    @Query("DELETE FROM conteos_logs WHERE estado = :estado")
    suspend fun deleteConteosByEstado(estado: String)

    @Query("UPDATE conteos_logs SET estado = :nuevoEstado WHERE estado = :estadoActual")
    suspend fun actualizarEstadoConteosByEstado(estadoActual: String, nuevoEstado: String)

    /**
     * Actualiza el estado de todos los conteos de un inventario.
     */
    @Query("UPDATE conteos_logs SET estado = :nuevoEstado WHERE winvd_nro_inv = :numeroInventario")
    suspend fun actualizarEstadoConteosByInventario(numeroInventario: Int, nuevoEstado: String)

    /**
     * Obtiene los registros de conteo para un artículo específico, ordenados por orden de captura.
     */
    @Query(
        """
        SELECT * FROM conteos_logs 
        WHERE winvd_nro_inv = :numeroInventario AND winvd_secu = :secuencia 
        ORDER BY orden ASC, created_at ASC
        """
    )
    suspend fun getConteosLogsByArticulo(numeroInventario: Int, secuencia: Int): List<ConteosLogs>

    @Query("SELECT * FROM conteos_logs WHERE estado = :estado ORDER BY created_at ASC")
    suspend fun getConteosByEstado(estado: String): List<ConteosLogs>

    /**
     * Obtiene todos los conteos por inventario y estado.
     */
    @Query("SELECT * FROM conteos_logs WHERE winvd_nro_inv = :numeroInventario AND estado = :estado ORDER BY orden ASC")
    suspend fun getConteosByInventarioAndEstado(numeroInventario: Int, estado: String): List<ConteosLogs>

    @Query("SELECT * FROM conteos_logs WHERE winvd_nro_inv = :numeroInventario ORDER BY orden ASC")
    suspend fun getConteosByInventario(numeroInventario: Int): List<ConteosLogs>

    /**
     * Obtiene el orden máximo registrado para un artículo específico.
     */
    @Query(
        """
        SELECT MAX(orden) FROM conteos_logs 
        WHERE winvd_nro_inv = :numeroInventario AND winvd_secu = :secuencia
        """
    )
    suspend fun getMaxOrdenByArticulo(numeroInventario: Int, secuencia: Int): Int?
}

