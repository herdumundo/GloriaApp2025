package com.gloria.repository

import com.gloria.data.dao.SucursalDepartamentoDao
import com.gloria.data.entity.SucursalDepartamento
import com.gloria.data.entity.Sucursal
import com.gloria.util.ConnectionOracle
import com.gloria.util.Controles
import com.gloria.util.Variables
import com.gloria.domain.usecase.AuthSessionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.ResultSet
import javax.inject.Inject

class SincronizacionRepository @Inject constructor(
    private val sucursalDepartamentoDao: SucursalDepartamentoDao,
    private val authSessionUseCase: AuthSessionUseCase
) {
    
    suspend fun sincronizarSucursalDepartamentos(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val connection = ConnectionOracle.getConnection(authSessionUseCase)
            
            if (connection == null) {
                return@withContext Result.failure(
                    Exception("Error de conexión: ${Controles.mensajeLogin}")
                )
            }
            
            val query = """
                SELECT SUC_CODIGO, SUC_DESC, DEP_CODIGO, DEP_DESC 
                FROM V_WEB_SUC_DEP 
                ORDER BY SUC_CODIGO, DEP_CODIGO
            """
            
            val statement = connection.createStatement()
            val resultSet: ResultSet = statement.executeQuery(query)
            
            val sucursalDepartamentos = mutableListOf<SucursalDepartamento>()
            
            while (resultSet.next()) {
                val sucursalDepartamento = SucursalDepartamento(
                    sucCodigo = resultSet.getInt("SUC_CODIGO"),
                    sucDesc = resultSet.getString("SUC_DESC") ?: "",
                    depCodigo = resultSet.getInt("DEP_CODIGO"),
                    depDesc = resultSet.getString("DEP_DESC") ?: "",
                    syncTimestamp = System.currentTimeMillis()
                )
                sucursalDepartamentos.add(sucursalDepartamento)
            }
            
            resultSet.close()
            statement.close()
            connection.close()
            
            // Limpiar datos anteriores y insertar nuevos
            sucursalDepartamentoDao.deleteAllSucursalDepartamentos()
            sucursalDepartamentoDao.insertAllSucursalDepartamentos(sucursalDepartamentos)
            
            Result.success(sucursalDepartamentos.size)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSucursalDepartamentosCount(): Int {
        return sucursalDepartamentoDao.getCount()
    }
    
    suspend fun getLastSyncTimestamp(): Long? {
        return sucursalDepartamentoDao.getLastSyncTimestamp()
    }
    
    suspend fun getSucursales(): List<Sucursal> {
        return sucursalDepartamentoDao.getSucursales()
    }
}
