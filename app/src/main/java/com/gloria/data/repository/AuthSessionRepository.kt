package com.gloria.data.repository

import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.entity.LoggedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionRepository @Inject constructor(
    private val loggedUserDao: LoggedUserDao
) {
    
    fun getCurrentUser(): Flow<LoggedUser?> = loggedUserDao.getCurrentUser()
    
    suspend fun getCurrentUserSync(): LoggedUser? = withContext(Dispatchers.IO) {
        loggedUserDao.getCurrentUserSync()
    }
    
    suspend fun saveUserSession(username: String, password: String, sucursalId: Int? = null, sucursalNombre: String? = null, modoDark: Boolean = false) = withContext(Dispatchers.IO) {
        Log.d("AuthSessionRepository", "Guardando sesión para usuario: $username, sucursal: $sucursalNombre, modoDark: $modoDark")

        // Limpiar sesiones anteriores antes de insertar la nueva
        loggedUserDao.clearAllUsers()
        Log.d("AuthSessionRepository", "Sesiones anteriores limpiadas")

        val loggedUser = LoggedUser(
            username = username,
            password = password,
            sucursalId = sucursalId,
            sucursalNombre = sucursalNombre,
            modoDark = modoDark,
            loginTimestamp = System.currentTimeMillis()
        )
        loggedUserDao.insertUser(loggedUser)
        Log.d("AuthSessionRepository", "Sesión guardada exitosamente")
    }
    
    suspend fun updateModoDark(modoDark: Boolean) = withContext(Dispatchers.IO) {
        Log.d("AuthSessionRepository", "Actualizando modo oscuro: $modoDark")
        val currentUser = loggedUserDao.getCurrentUserSync()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(modoDark = modoDark)
            loggedUserDao.insertUser(updatedUser)
            Log.d("AuthSessionRepository", "Modo oscuro actualizado exitosamente")
        } else {
            Log.w("AuthSessionRepository", "No hay usuario logueado para actualizar modo oscuro")
        }
    }

    suspend fun clearUserSession() = withContext(Dispatchers.IO) {
        Log.d("AuthSessionRepository", "Limpiando sesión de usuario")
        loggedUserDao.clearAllUsers()
        Log.d("AuthSessionRepository", "Sesión limpiada exitosamente")
    }
    
    suspend fun isUserLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        val user = loggedUserDao.getCurrentUserSync()
        val isLoggedIn = user != null
        Log.d("AuthSessionRepository", "Verificando sesión: isLoggedIn=$isLoggedIn, user=$user")
        isLoggedIn
    }
}
