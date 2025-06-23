package com.example.myapplicationtestsade.data.repository

import com.example.myapplicationtestsade.data.api.ApiClient
import com.example.myapplicationtestsade.data.database.dao.UserDao
import com.example.myapplicationtestsade.data.mappers.UserMapper
import com.example.myapplicationtestsade.data.models.RandomUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ******************** USER REPOSITORY ********************
 * Repository pattern implementation with Room database integration
 * Responsibilities:
 * - Manage data from both API and local database
 * - Coordinate between network and local storage
 * - Provide single source of truth for user data
 * - Handle offline capabilities
 * - Centralized error handling
 */
class UserRepository(private val userDao: UserDao) {

    // ******************** DEPENDENCIES ********************

    /**
     * API client instance for making network requests
     * Uses the configured Retrofit instance from ApiClient
     */
    private val api = ApiClient.randomUserApi

    // ******************** DATABASE OPERATIONS ********************

    /**
     * Get all users from local database
     */
    fun getAllUsers(): Flow<List<RandomUser>> {
        return userDao.getAllUsers().map { entities ->
            UserMapper.toRandomUserList(entities)
        }
    }

    /**
     * Get users sorted by name
     */
    fun getUsersSortedByName(): Flow<List<RandomUser>> {
        return userDao.getUsersSortedByName().map { entities ->
            UserMapper.toRandomUserList(entities)
        }
    }

    /**
     * Get users sorted by location
     */
    fun getUsersSortedByLocation(): Flow<List<RandomUser>> {
        return userDao.getUsersSortedByLocation().map { entities ->
            UserMapper.toRandomUserList(entities)
        }
    }

    /**
     * Search users by name or email
     */
    fun searchUsers(searchQuery: String): Flow<List<RandomUser>> {
        return userDao.searchUsers(searchQuery).map { entities ->
            UserMapper.toRandomUserList(entities)
        }
    }

    /**
     * Get single user by ID from database
     */
    suspend fun getUserById(uniqueId: String): RandomUser? {
        return userDao.getUserById(uniqueId)?.let { entity ->
            UserMapper.toRandomUser(entity)
        }
    }

    /**
     * Get total user count from database
     */
    suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    // ******************** NETWORK + DATABASE OPERATIONS ********************

    /**
     * Load random users from API and store in database
     * Combines network fetch with local storage
     */
    suspend fun loadRandomUsers(count: Int = 10, forceRefresh: Boolean = false): Result<List<RandomUser>> {
        return try {
            // Check if we should load from cache first
            if (!forceRefresh) {
                val localCount = getUserCount()
                if (localCount >= count) {
                    // We have enough local data, return success
                    // Data will be provided via Flow observation
                    return Result.success(emptyList()) // Flow provides the actual data
                }
            }

            // Fetch from API
            val response = api.getRandomUsers(count)

            if (response.isSuccessful) {
                val users = response.body()?.results ?: emptyList()

                if (users.isNotEmpty()) {
                    // Store in database
                    val entities = UserMapper.toEntityList(users)
                    userDao.insertUsers(entities)

                    // Return the users
                    Result.success(users)
                } else {
                    Result.failure(Exception("No users received from API"))
                }
            } else {
                // API error - return error but data will come from database via Flow
                Result.failure(Exception("API unavailable: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Network error - return error but cached data available via Flow
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Add a single random user from API and store in database
     * Used by FAB button functionality
     */
    suspend fun addRandomUser(): Result<RandomUser> {
        return try {
            val response = api.getRandomUser()

            if (response.isSuccessful) {
                val user = response.body()?.results?.firstOrNull()

                if (user != null) {
                    // Store in database
                    val entity = UserMapper.toEntity(user)
                    userDao.insertUser(entity)

                    Result.success(user)
                } else {
                    Result.failure(Exception("No user data received"))
                }
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ******************** MANUAL USER OPERATIONS ********************

    /**
     * Create a manual user (not from API)
     */
    suspend fun createManualUser(user: RandomUser): Result<RandomUser> {
        return try {
            val entity = UserMapper.toEntity(user).copy(isFromApi = false)
            val rowId = userDao.insertUser(entity)

            if (rowId > 0) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to save user to database"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update existing user
     */
    suspend fun updateUser(user: RandomUser): Result<RandomUser> {
        return try {
            val entity = UserMapper.toEntity(user)
            val rowsAffected = userDao.updateUser(entity)

            if (rowsAffected > 0) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found or no changes made"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ******************** DELETE OPERATIONS ********************

    /**
     * Delete user by ID
     */
    suspend fun deleteUser(uniqueId: String): Result<Unit> {
        return try {
            val rowsDeleted = userDao.deleteUserById(uniqueId)
            if (rowsDeleted > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete all users (for "Empty Database" feature)
     */
    suspend fun deleteAllUsers(): Result<Int> {
        return try {
            val deletedCount = userDao.deleteAllUsers()
            Result.success(deletedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ******************** BULK OPERATIONS ********************

    /**
     * Fill database with multiple random users
     * Used by "Fill Database" settings button
     */
    suspend fun fillDatabase(count: Int = 10): Result<List<RandomUser>> {
        return loadRandomUsers(count, forceRefresh = true)
    }

    // ******************** UTILITY FUNCTIONS ********************

    /**
     * Check if database has any users
     */
    suspend fun isDatabaseEmpty(): Boolean {
        return getUserCount() == 0
    }

    /**
     * Get users by source type
     */
    fun getUsersBySource(isFromApi: Boolean): Flow<List<RandomUser>> {
        return userDao.getUsersBySource(isFromApi).map { entities ->
            UserMapper.toRandomUserList(entities)
        }
    }
}