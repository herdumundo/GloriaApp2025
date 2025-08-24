package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.SucursalDepartamento
import com.gloria.data.entity.Sucursal
import kotlinx.coroutines.flow.Flow

@Dao
interface SucursalDepartamentoDao {
    
    @Query("SELECT * FROM sucursal_departamento ORDER BY suc_codigo, dep_codigo")
    fun getAllSucursalDepartamentos(): Flow<List<SucursalDepartamento>>
    
    @Query("SELECT * FROM sucursal_departamento WHERE suc_codigo = :sucCodigo ORDER BY dep_codigo")
    fun getDepartamentosBySucursal(sucCodigo: Int): Flow<List<SucursalDepartamento>>
    
    @Query("SELECT DISTINCT suc_codigo, suc_desc FROM sucursal_departamento ORDER BY suc_codigo")
    suspend fun getSucursales(): List<Sucursal>
    
    @Query("SELECT * FROM sucursal_departamento WHERE suc_codigo = :sucCodigo ORDER BY dep_codigo")
    suspend fun getDepartamentosBySucursalSuspend(sucCodigo: Int): List<SucursalDepartamento>
    
    @Query("SELECT * FROM sucursal_departamento WHERE suc_codigo = :sucCodigo AND dep_codigo = :depCodigo")
    suspend fun getSucursalDepartamento(sucCodigo: Int, depCodigo: Int): SucursalDepartamento?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSucursalDepartamento(sucursalDepartamento: SucursalDepartamento)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSucursalDepartamentos(sucursalDepartamentos: List<SucursalDepartamento>)
    
    @Update
    suspend fun updateSucursalDepartamento(sucursalDepartamento: SucursalDepartamento)
    
    @Delete
    suspend fun deleteSucursalDepartamento(sucursalDepartamento: SucursalDepartamento)
    
    @Query("DELETE FROM sucursal_departamento")
    suspend fun deleteAllSucursalDepartamentos()
    
    @Query("DELETE FROM sucursal_departamento WHERE suc_codigo = :sucCodigo")
    suspend fun deleteBySucursal(sucCodigo: Int)
    
    @Query("SELECT COUNT(*) FROM sucursal_departamento")
    suspend fun getCount(): Int
    
    @Query("SELECT MAX(sync_timestamp) FROM sucursal_departamento")
    suspend fun getLastSyncTimestamp(): Long?
}
