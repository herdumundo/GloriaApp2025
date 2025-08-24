package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.Seccion
import kotlinx.coroutines.flow.Flow

@Dao
interface SeccionDao {
    
    @Query("SELECT * FROM seccion ORDER BY seccCodigo")
    fun getAllSecciones(): Flow<List<Seccion>>
    
    @Query("SELECT * FROM seccion WHERE secc_area = :areaCodigo ORDER BY seccCodigo")
    fun getSeccionesByArea(areaCodigo: Int): Flow<List<Seccion>>
    
    @Query("SELECT * FROM seccion WHERE secc_dpto = :dptoCodigo ORDER BY seccCodigo")
    fun getSeccionesByDepartamento(dptoCodigo: Int): Flow<List<Seccion>>
    
    @Query("SELECT * FROM seccion WHERE seccCodigo = :seccCodigo")
    suspend fun getSeccionByCodigo(seccCodigo: Int): Seccion?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeccion(seccion: Seccion)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSecciones(secciones: List<Seccion>)
    
    @Update
    suspend fun updateSeccion(seccion: Seccion)
    
    @Delete
    suspend fun deleteSeccion(seccion: Seccion)
    
    @Query("DELETE FROM seccion")
    suspend fun deleteAllSecciones()
    
    @Query("DELETE FROM seccion WHERE secc_area = :areaCodigo")
    suspend fun deleteByArea(areaCodigo: Int)
    
    @Query("DELETE FROM seccion WHERE secc_dpto = :dptoCodigo")
    suspend fun deleteByDepartamento(dptoCodigo: Int)
    
    @Query("SELECT COUNT(*) FROM seccion")
    suspend fun getCount(): Int
}
