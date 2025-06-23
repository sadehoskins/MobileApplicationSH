package com.example.myapplicationtestsade.data.database.dao

import androidx.room.*
import com.example.myapplicationtestsade.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * ******************** USER DAO ********************
 * Data Access Object for user database operations
 * Provides CRUD operations and queries for UserEntity
 * Uses Flow for reactive database updates
 */
@Dao
interface UserDao {

    // ******************** INSERT OPERATIONS ********************

    /**
     * Insert a single user into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Insert multiple users into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>): List<Long>

    // ******************** QUERY OPERATIONS ********************

    /**
     * Get all users ordered by creation date -> newest first
     */
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * Get user by unique ID
     */
    @Query("SELECT * FROM users WHERE uniqueId = :uniqueId")
    suspend fun getUserById(uniqueId: String): UserEntity?

    /**
     * Get users sorted by name (A-Z)
     */
    @Query("SELECT * FROM users ORDER BY nameFirst ASC, nameLast ASC")
    fun getUsersSortedByName(): Flow<List<UserEntity>>

    /**
     * Get users sorted by country
     */
    @Query("SELECT * FROM users ORDER BY locationCountry ASC, locationCity ASC")
    fun getUsersSortedByLocation(): Flow<List<UserEntity>>

    /**
     * Search users by name or email
     */
    @Query("""
        SELECT * FROM users 
        WHERE nameFirst LIKE '%' || :searchQuery || '%' 
        OR nameLast LIKE '%' || :searchQuery || '%' 
        OR email LIKE '%' || :searchQuery || '%'
        ORDER BY nameFirst ASC
    """)
    fun searchUsers(searchQuery: String): Flow<List<UserEntity>>

    /**
     * Get total user count
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    /**
     * Get users from API vs manual creation
     */
    @Query("SELECT * FROM users WHERE isFromApi = :isFromApi ORDER BY createdAt DESC")
    fun getUsersBySource(isFromApi: Boolean): Flow<List<UserEntity>>

    // ******************** UPDATE OPERATIONS ********************

    /**
     * Update existing user
     */
    @Update
    suspend fun updateUser(user: UserEntity): Int

    /**
     * Update user's contact information
     */
    @Query("""
        UPDATE users 
        SET email = :email, phone = :phone, cell = :cell 
        WHERE uniqueId = :uniqueId
    """)
    suspend fun updateUserContact(uniqueId: String, email: String, phone: String, cell: String)

    // ******************** DELETE OPERATIONS ********************

    /**
     * Delete user by ID
     */
    @Query("DELETE FROM users WHERE uniqueId = :uniqueId")
    suspend fun deleteUserById(uniqueId: String): Int

    /**
     * Delete specific user entity
     */
    @Delete
    suspend fun deleteUser(user: UserEntity): Int

    /**
     * Delete all users (for "Empty Database" feature)
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers(): Int

    /**
     * Delete users by source (API vs manual)
     */
    @Query("DELETE FROM users WHERE isFromApi = :isFromApi")
    suspend fun deleteUsersBySource(isFromApi: Boolean): Int
}