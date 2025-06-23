package com.example.myapplicationtestsade

import android.app.Application
import com.example.myapplicationtestsade.data.database.AppDatabase

/**
 * ******************** APPLICATION CLASS ********************
 * Custom Application class for app-wide initialization
 * Responsibilities:
 * - Initialize database instance
 * - Setup global configurations
 * - Provide application context for Singletons
 */
class RandomUserApplication : Application() {

    /**
     * Database instance - lazily initialized
     * Provides single database instance across the app
     */
    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()

        // Database is initialized lazily when first accessed
        // Any other app-wide initialization can go here:
        // - Logging setup
        // - Crash reporting
        // - Analytics initialization
    }
}