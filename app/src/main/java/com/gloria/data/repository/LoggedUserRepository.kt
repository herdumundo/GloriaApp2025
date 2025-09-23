package com.gloria.data.repository

import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.entity.LoggedUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositorio para manejar las operaciones del usuario logueado
 */
class LoggedUserRepository @Inject constructor(
    private val loggedUserDao: LoggedUserDao
) {
    
    /**
     * Obtiene el usuario logueado como Flow
     */
    fun getLoggedUser(): Flow<LoggedUser?> {
        return loggedUserDao.getCurrentUser()
    }
    
    /**
     * Obtiene el usuario logueado de forma síncrona
     */
    suspend fun getLoggedUserSync(): LoggedUser? {
        return loggedUserDao.getCurrentUserSync()
    }
    
    /**
     * Inserta o actualiza el usuario logueado
     */
    suspend fun insertLoggedUser(user: LoggedUser) {
        loggedUserDao.insertUser(user)
    }
    
    /**
     * Elimina un usuario logueado
     */
    suspend fun deleteLoggedUser(user: LoggedUser) {
        loggedUserDao.deleteUser(user.id)
    }
    
    /**
     * Limpia todos los usuarios logueados
     */
    suspend fun clearLoggedUsers() {
        loggedUserDao.clearAllUsers()
    }
}
