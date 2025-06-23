package com.example.myapplicationtestsade.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplicationtestsade.data.database.dao.UserDao
import com.example.myapplicationtestsade.data.database.entities.UserEntity

/**
 * ******************** APP DATABASE ********************
 * Main Room database class for the application
 * Manages database creation, versioning, and provides DAO access
 * Implements Singleton pattern for single database instance
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false // Set to true in production for schema export
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to User DAO
     */
    abstract fun userDao(): UserDao

    companion object {
        // Database name
        private const val DATABASE_NAME = "random_user_database"

        // Singleton instance
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get database instance (Singleton pattern)
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // For development - removes data on schema changes
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}