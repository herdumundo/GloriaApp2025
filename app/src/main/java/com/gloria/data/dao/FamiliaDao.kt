package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.Familia
import kotlinx.coroutines.flow.Flow

@Dao
interface FamiliaDao {
    
    @Query("SELECT * FROM familia ORDER BY fliaCodigo")
    fun getAllFamilias(): Flow<List<Familia>>
    
    @Query("SELECT * FROM familia WHERE flia_area = :areaCodigo ORDER BY fliaCodigo")
    fun getFamiliasByArea(areaCodigo: Int): Flow<List<Familia>>
    
    @Query("SELECT * FROM familia WHERE flia_dpto = :dptoCodigo ORDER BY fliaCodigo")
    fun getFamiliasByDepartamento(dptoCodigo: Int): Flow<List<Familia>>
    
    @Query("SELECT * FROM familia WHERE flia_seccion = :seccCodigo ORDER BY fliaCodigo")
    fun getFamiliasBySeccion(seccCodigo: Int): Flow<List<Familia>>
    
    @Query("SELECT * FROM familia WHERE fliaCodigo = :fliaCodigo")
    suspend fun getFamiliaByCodigo(fliaCodigo: Int): Familia?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilia(familia: Familia)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFamilias(familias: List<Familia>)
    
    @Update
    suspend fun updateFamilia(familia: Familia)
    
    @Delete
    suspend fun deleteFamilia(familia: Familia)
    
    @Query("DELETE FROM familia")
    suspend fun deleteAllFamilias()
    
    @Query("DELETE FROM familia WHERE flia_area = :areaCodigo")
    suspend fun deleteByArea(areaCodigo: Int)
    
    @Query("DELETE FROM familia WHERE flia_dpto = :dptoCodigo")
    suspend fun deleteByDepartamento(dptoCodigo: Int)
    
    @Query("DELETE FROM familia WHERE flia_seccion = :seccCodigo")
    suspend fun deleteBySeccion(seccCodigo: Int)
    
    @Query("SELECT COUNT(*) FROM familia")
    suspend fun getCount(): Int
}
