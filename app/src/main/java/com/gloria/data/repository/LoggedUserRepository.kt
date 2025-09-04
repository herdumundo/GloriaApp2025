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
        return loggedUserDao.getLoggedUser()
    }
    
    /**
     * Obtiene el usuario logueado de forma síncrona
     */
    suspend fun getLoggedUserSync(): LoggedUser? {
        return loggedUserDao.getLoggedUserSync()
    }
    
    /**
     * Inserta o actualiza el usuario logueado
     */
    suspend fun insertLoggedUser(user: LoggedUser) {
        loggedUserDao.insertLoggedUser(user)
    }
    
    /**
     * Elimina un usuario logueado
     */
    suspend fun deleteLoggedUser(user: LoggedUser) {
        loggedUserDao.deleteLoggedUser(user)
    }
    
    /**
     * Limpia todos los usuarios logueados
     */
    suspend fun clearLoggedUsers() {
        loggedUserDao.clearLoggedUsers()
    }
}
