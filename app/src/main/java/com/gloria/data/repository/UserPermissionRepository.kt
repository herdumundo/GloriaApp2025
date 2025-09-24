package com.gloria.data.repository

import android.util.Log
import com.gloria.data.dao.UserPermissionDao
import com.gloria.data.model.UserPermission
import com.gloria.data.model.PermissionMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositorio para manejar operaciones de permisos de usuario
 */
class UserPermissionRepository @Inject constructor(
    private val userPermissionDao: UserPermissionDao
) {
    
    /**
     * Obtiene todos los permisos de un usuario
     */
    fun getUserPermissions(username: String): Flow<List<UserPermission>> {
        return userPermissionDao.getUserPermissions(username)
    }
    
    /**
     * Obtiene todos los permisos de un usuario como lista suspendida
     */
    suspend fun getUserPermissionsSync(username: String): List<UserPermission> {
        return userPermissionDao.getUserPermissionsSync(username)
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico
     */
    suspend fun hasPermission(username: String, formulario: String): Boolean {
        return userPermissionDao.hasPermission(username, formulario)
    }
    
    /**
     * Verifica si un usuario tiene permiso para una pantalla específica
     */
    suspend fun hasScreenPermission(username: String, screenId: String): Boolean {
        return userPermissionDao.hasScreenPermission(username, screenId)
    }
    
    /**
     * Obtiene todos los códigos de formulario que tiene un usuario
     */
    suspend fun getUserFormularios(username: String): List<String> {
        return userPermissionDao.getUserFormularios(username)
    }
    
    /**
     * Inserta un permiso de usuario
     */
    suspend fun insertUserPermission(userPermission: UserPermission) {
        userPermissionDao.insertUserPermission(userPermission)
    }
    
    /**
     * Inserta múltiples permisos de usuario
     */
    suspend fun insertUserPermissions(userPermissions: List<UserPermission>) {
        userPermissionDao.insertUserPermissions(userPermissions)
    }
    
    /**
     * Sincroniza los permisos de un usuario desde una lista de códigos de formulario
     */
    suspend fun syncUserPermissions(username: String, formularios: List<String>, nombre: String = "INVENTARIO") {
        // Primero eliminar todos los permisos existentes del usuario
        userPermissionDao.deleteAllUserPermissions(username)
        
        // Luego insertar los nuevos permisos
        val permissions = formularios.map { formulario ->
            UserPermission(
                id = "${username}_$formulario",
                username = username,
                formulario = formulario,
                nombre = nombre,
                granted = true
            )
        }
        
        userPermissionDao.insertUserPermissions(permissions)
    }
    
    /**
     * Sincroniza los permisos de un usuario desde el resultado de la consulta Oracle
     * formato: lista de pares (formulario, nombre)
     */
    suspend fun syncUserPermissionsFromOracle(username: String, formulariosConNombre: List<Pair<String, String>>) {
        Log.d("UserPermissionRepository", "=== SINCRONIZANDO PERMISOS EN ROOM ===")
        Log.d("UserPermissionRepository", "Usuario: $username")
        Log.d("UserPermissionRepository", "Formularios recibidos: $formulariosConNombre")
        
        // Primero eliminar todos los permisos existentes del usuario
        Log.d("UserPermissionRepository", "Eliminando permisos existentes para $username...")
        userPermissionDao.deleteAllUserPermissions(username)
        
        if (formulariosConNombre.isEmpty()) {
            Log.w("UserPermissionRepository", "⚠️ No hay formularios para insertar para $username")
            return
        }
        
        // Luego insertar los nuevos permisos
        val permissions = formulariosConNombre.map { (formulario, nombre) ->
            val permission = UserPermission(
                id = "${username}_$formulario",
                username = username,
                formulario = formulario,
                nombre = nombre,
                granted = true
            )
            Log.d("UserPermissionRepository", "Creando permiso: $permission")
            permission
        }
        
        Log.d("UserPermissionRepository", "Insertando ${permissions.size} permisos en Room...")
        userPermissionDao.insertUserPermissions(permissions)
        
        // Verificar que se insertaron correctamente
        val insertedCount = userPermissionDao.getUserPermissionCount(username)
        Log.d("UserPermissionRepository", "✅ Permisos insertados en Room: $insertedCount")
        
        if (insertedCount > 0) {
            val insertedPermissions = userPermissionDao.getUserPermissionsSync(username)
            Log.d("UserPermissionRepository", "Permisos en Room para $username: $insertedPermissions")
        }
    }
    
    /**
     * Actualiza un permiso de usuario
     */
    suspend fun updateUserPermission(userPermission: UserPermission) {
        userPermissionDao.updateUserPermission(userPermission)
    }
    
    /**
     * Elimina un permiso específico de un usuario
     */
    suspend fun deleteUserPermission(username: String, formulario: String) {
        userPermissionDao.deleteUserPermission(username, formulario)
    }
    
    /**
     * Elimina todos los permisos de un usuario
     */
    suspend fun deleteAllUserPermissions(username: String) {
        userPermissionDao.deleteAllUserPermissions(username)
    }
    
    /**
     * Elimina todos los permisos de la tabla
     */
    suspend fun deleteAllPermissions() {
        userPermissionDao.deleteAllPermissions()
    }
    
    /**
     * Obtiene el conteo total de permisos
     */
    suspend fun getPermissionCount(): Int {
        return userPermissionDao.getPermissionCount()
    }
    
    /**
     * Obtiene el conteo de permisos de un usuario específico
     */
    suspend fun getUserPermissionCount(username: String): Int {
        return userPermissionDao.getUserPermissionCount(username)
    }
    
    /**
     * Obtiene las pantallas permitidas para un usuario
     */
    suspend fun getAllowedScreens(username: String): List<String> {
        val formularios = getUserFormularios(username)
        val allowedScreens = mutableSetOf<String>()
        
        formularios.forEach { formulario ->
            PermissionMapper.formularioToScreen[formulario]?.let { screens ->
                allowedScreens.addAll(screens)
            }
        }
        
        return allowedScreens.toList()
    }
}
