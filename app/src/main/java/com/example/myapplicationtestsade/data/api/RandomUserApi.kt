package com.example.myapplicationtestsade.data.api

import com.example.myapplicationtestsade.data.models.RandomUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ******************** API INTERFACE ********************
 * Retrofit interface for communicating with randomuser.me API
 *
 * API Documentation: https://randomuser.me/documentation
 * Base URL: https://randomuser.me/
 *
 */
interface RandomUserApi {

    /**
     * Fetches multiple random users from the API
     *
     * @param results Number of users to fetch (default: 10)
     * @param page Page number for pagination (default: 1)
     * @return Response containing RandomUserResponse with user list
     *
     * Example API call: https://randomuser.me/api/?results=10&page=1
     */
    @GET("api/")
    suspend fun getRandomUsers(
        @Query("results") results: Int = 10,    // How many users to get
        @Query("page") page: Int = 1            // Which page (for pagination)
    ): Response<RandomUserResponse>

    /**
     * Fetches a single random user from the API
     * Used by the "Add User" functionality (FAB button)
     *
     * @return Response containing RandomUserResponse with single user
     *
     * Example API call: https://randomuser.me/api/
     * Note: API always returns an array, even for single user
     */
    @GET("api/")
    suspend fun getRandomUser(): Response<RandomUserResponse>
}