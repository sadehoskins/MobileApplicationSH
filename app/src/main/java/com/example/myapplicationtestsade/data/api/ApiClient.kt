package com.example.myapplicationtestsade.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ******************** API CLIENT CONFIGURATION ********************
 * Singleton object that configures and provides Retrofit instance
 *
 * This object handles:
 * - HTTP client configuration
 * - Request/response logging
 * - JSON serialization/deserialization
 * - API interface creation
 */
object ApiClient {

    // ******************** CONFIGURATION ********************

    /**
     * Base URL for the Random User API
     * All API endpoints will be relative to this URL
     */
    private const val BASE_URL = "https://randomuser.me/"

    // ******************** HTTP LOGGING ********************

    /**
     * HTTP logging interceptor for debugging network requests
     * Logs full request/response bodies in debug builds
     *
     * Log levels:
     * - NONE: No logging
     * - BASIC: Request/response line only
     * - HEADERS: Request/response line + headers
     * - BODY: Everything including request/response bodies
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY    // Full logging for development
    }

    // ******************** HTTP CLIENT ********************

    /**
     * OkHttp client with custom configuration
     * Includes logging interceptor for network debugging
     *
     * Additional interceptors can be added here for:
     * - Authentication headers
     * - Request/response modification
     * - Network timeout configuration
     * - Cache control
     */
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)         // Add logging for debugging
        .build()

    // ******************** RETROFIT INSTANCE ********************

    /**
     * Main Retrofit instance configured with:
     * - Base URL for the API
     * - Custom HTTP client with logging
     * - Gson converter for JSON serialization
     *
     * Retrofit automatically converts:
     * - Kotlin data classes ↔ JSON
     * - Suspend functions ↔ Asynchronous HTTP calls
     * - Response<T> ↔ HTTP responses with status codes
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)                          // Set the API base URL
        .client(httpClient)                         // Use our configured HTTP client
        .addConverterFactory(GsonConverterFactory.create())  // JSON ↔ Kotlin objects
        .build()

    // ******************** API INTERFACE ********************

    /**
     * Creates and provides the RandomUserApi interface implementation
     * Retrofit automatically generates the implementation at runtime
     * This single instance is reused throughout the app for efficiency
     * All API calls go through this interface
     */
    val randomUserApi: RandomUserApi = retrofit.create(RandomUserApi::class.java)
}