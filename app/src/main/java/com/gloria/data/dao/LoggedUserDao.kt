package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.LoggedUser
import kotlinx.coroutines.flow.Flow

@Dao
interface LoggedUserDao {
    
    @Query("SELECT * FROM logged_user ORDER BY loginTimestamp DESC LIMIT 1")
    fun getCurrentUser(): Flow<LoggedUser?>
    
    @Query("SELECT * FROM logged_user ORDER BY loginTimestamp DESC LIMIT 1")
    suspend fun getCurrentUserSync(): LoggedUser?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: LoggedUser)
    
    @Query("DELETE FROM logged_user")
    suspend fun clearAllUsers()
    
    @Query("DELETE FROM logged_user WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}