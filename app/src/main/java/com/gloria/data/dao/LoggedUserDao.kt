package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.LoggedUser
import kotlinx.coroutines.flow.Flow

/**
 * DAO para manejar las operaciones del usuario logueado
 */
@Dao
interface LoggedUserDao {
    
    @Query("SELECT * FROM logged_user WHERE id = 1")
    fun getLoggedUser(): Flow<LoggedUser?>
    
    @Query("SELECT * FROM logged_user WHERE id = 1")
    suspend fun getLoggedUserSync(): LoggedUser?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoggedUser(user: LoggedUser)
    
    @Delete
    suspend fun deleteLoggedUser(user: LoggedUser)
    
    @Query("DELETE FROM logged_user")
    suspend fun clearLoggedUsers()
}
