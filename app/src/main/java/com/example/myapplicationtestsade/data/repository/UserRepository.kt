package com.example.myapplicationtestsade.data.repository

import com.example.myapplicationtestsade.data.api.ApiClient
import com.example.myapplicationtestsade.data.models.RandomUser

/**
 * ******************** USER REPOSITORY ********************
 * Repository pattern implementation for user data management
 *
 * Responsibilities:
 * - Abstract API calls from ViewModels
 * - Handle network errors and convert to app-specific errors
 * - Provide clean, type-safe interface for data operations
 * - Centralized error handling
 */
class UserRepository {

    // ******************** DEPENDENCIES ********************

    /**
     * API client instance for making network requests
     * Uses the configured Retrofit instance from ApiClient
     */
    private val api = ApiClient.randomUserApi

    // ******************** PUBLIC API METHODS ********************

    /**
     * Fetches multiple random users from the API
     *
     * @param count Number of users to fetch (default: 10)
     * @return Result<List<RandomUser>> - Success with user list or Failure with exception
     *
     */
    suspend fun getRandomUsers(count: Int = 10): Result<List<RandomUser>> {
        return try {
            // Make API call and await response
            val response = api.getRandomUsers(count)

            if (response.isSuccessful) {
                // Success: Extract user list from response body
                // Handle case where body might be null
                Result.success(response.body()?.results ?: emptyList())
            } else {
                // HTTP error (4xx, 5xx): Convert to app exception
                Result.failure(Exception("Failed to fetch users: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Network error, timeout, JSON parsing error, etc.
            // Wrap in Result.failure for consistent error handling
            Result.failure(e)
        }
    }

    /**
     * Fetches a single random user from the API
     * Used by "Add User" functionality (FAB button)
     *
     * @return Result<RandomUser> - Success with single user or Failure with exception
     *
     * Note: API always returns array format -> so extract first element
     */
    suspend fun getRandomUser(): Result<RandomUser> {
        return try {
            // Make API call for single user
            val response = api.getRandomUser()

            if (response.isSuccessful) {
                // Extract first user from results array
                val user = response.body()?.results?.firstOrNull()

                if (user != null) {
                    // Success: Return the user
                    Result.success(user)
                } else {
                    // Edge case: API returned success but no user data
                    Result.failure(Exception("No user data received"))
                }
            } else {
                // HTTP error: Convert to app exception
                Result.failure(Exception("Failed to fetch user: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Network/parsing error: Wrap in Result.failure
            Result.failure(e)
        }
    }
}