package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.Departamento
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartamentoDao {
    
    @Query("SELECT * FROM departamento ORDER BY dptoCodigo")
    fun getAllDepartamentos(): Flow<List<Departamento>>
    
    @Query("SELECT * FROM departamento WHERE dpto_area = :areaCodigo ORDER BY dptoCodigo")
    fun getDepartamentosByArea(areaCodigo: Int): Flow<List<Departamento>>
    
    @Query("SELECT * FROM departamento WHERE dptoCodigo = :dptoCodigo")
    suspend fun getDepartamentoByCodigo(dptoCodigo: Int): Departamento?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartamento(departamento: Departamento)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDepartamentos(departamentos: List<Departamento>)
    
    @Update
    suspend fun updateDepartamento(departamento: Departamento)
    
    @Delete
    suspend fun deleteDepartamento(departamento: Departamento)
    
    @Query("DELETE FROM departamento")
    suspend fun deleteAllDepartamentos()
    
    @Query("DELETE FROM departamento WHERE dpto_area = :areaCodigo")
    suspend fun deleteByArea(areaCodigo: Int)
    
    @Query("SELECT COUNT(*) FROM departamento")
    suspend fun getCount(): Int
}
