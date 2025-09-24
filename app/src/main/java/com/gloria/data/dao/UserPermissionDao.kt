package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.model.UserPermission
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de permisos de usuario
 */
@Dao
interface UserPermissionDao {
    
    /**
     * Obtiene todos los permisos de un usuario específico
     */
    @Query("SELECT * FROM user_permissions WHERE username = :username")
    fun getUserPermissions(username: String): Flow<List<UserPermission>>
    
    /**
     * Obtiene todos los permisos de un usuario específico como lista suspendida
     */
    @Query("SELECT * FROM user_permissions WHERE username = :username")
    suspend fun getUserPermissionsSync(username: String): List<UserPermission>
    
    /**
     * Verifica si un usuario tiene un permiso específico
     */
    @Query("SELECT COUNT(*) > 0 FROM user_permissions WHERE username = :username AND formulario = :formulario AND granted = 1")
    suspend fun hasPermission(username: String, formulario: String): Boolean
    
    /**
     * Verifica si un usuario tiene permiso para una pantalla específica
     */
    @Query("""
        SELECT COUNT(*) > 0 FROM user_permissions 
        WHERE username = :username 
        AND formulario IN (
            CASE 
                WHEN :screenId = 'registro_toma' THEN 'STKW001'
                WHEN :screenId = 'registro_inventario' THEN 'STKW002'
                WHEN :screenId = 'cancelacion_inventario' THEN 'STKW004'
                WHEN :screenId = 'exportar_inventario' THEN 'STKW002'
                WHEN :screenId = 'sincronizar_datos' THEN 'STKW001'
                WHEN :screenId = 'informe_conteos_pendientes' THEN 'STKW005'
                ELSE ''
            END
        )
        AND granted = 1
    """)
    suspend fun hasScreenPermission(username: String, screenId: String): Boolean
    
    /**
     * Obtiene todos los códigos de formulario que tiene un usuario
     */
    @Query("SELECT formulario FROM user_permissions WHERE username = :username AND granted = 1")
    suspend fun getUserFormularios(username: String): List<String>
    
    /**
     * Inserta un permiso de usuario
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPermission(userPermission: UserPermission)
    
    /**
     * Inserta múltiples permisos de usuario
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPermissions(userPermissions: List<UserPermission>)
    
    /**
     * Actualiza un permiso de usuario
     */
    @Update
    suspend fun updateUserPermission(userPermission: UserPermission)
    
    /**
     * Elimina un permiso específico de un usuario
     */
    @Query("DELETE FROM user_permissions WHERE username = :username AND formulario = :formulario")
    suspend fun deleteUserPermission(username: String, formulario: String)
    
    /**
     * Elimina todos los permisos de un usuario
     */
    @Query("DELETE FROM user_permissions WHERE username = :username")
    suspend fun deleteAllUserPermissions(username: String)
    
    /**
     * Elimina todos los permisos de la tabla
     */
    @Query("DELETE FROM user_permissions")
    suspend fun deleteAllPermissions()
    
    /**
     * Obtiene el conteo total de permisos
     */
    @Query("SELECT COUNT(*) FROM user_permissions")
    suspend fun getPermissionCount(): Int
    
    /**
     * Obtiene el conteo de permisos de un usuario específico
     */
    @Query("SELECT COUNT(*) FROM user_permissions WHERE username = :username")
    suspend fun getUserPermissionCount(username: String): Int
}
