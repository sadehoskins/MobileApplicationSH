package com.example.myapplicationtestsade.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ******************** USER ENTITY ********************
 * Room database entity for storing user data locally
 * Maps RandomUser API model to database table structure
 * Nesting objects into individual columns for simpler database schema
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uniqueId: String,

    // ******************** BASIC USER INFO ********************
    val gender: String,
    val email: String,
    val phone: String,
    val cell: String,
    val nat: String, // Nationality code

    // ******************** NAME COMPONENTS (FLATTENED) ********************
    val nameTitle: String,   // Mr, Ms, Dr, etc.
    val nameFirst: String,   // First name
    val nameLast: String,    // Last name

    // ******************** LOCATION COMPONENTS (FLATTENED) ********************
    val locationStreetNumber: Int,
    val locationStreetName: String,
    val locationCity: String,
    val locationState: String,
    val locationCountry: String,
    val locationPostcode: String,
    val locationCoordinatesLatitude: String,
    val locationCoordinatesLongitude: String,
    val locationTimezoneOffset: String,
    val locationTimezoneDescription: String,

    // ******************** DATE OF BIRTH ********************
    val dobDate: String,     // Birth date in ISO format
    val dobAge: Int,         // Calculated age in years

    // ******************** REGISTRATION INFO ********************
    val registeredDate: String,  // Registration date in ISO format
    val registeredAge: Int,      // Years since registration

    // ******************** LOGIN CREDENTIALS ********************
    val loginUuid: String,       // API UUID (different from our uniqueId)
    val loginUsername: String,
    val loginPassword: String,
    val loginSalt: String,
    val loginMd5: String,
    val loginSha1: String,
    val loginSha256: String,

    // ******************** ID INFORMATION (NULLABLE) ********************
    val idName: String?,     // ID type name (e.g., "SSN", "BSN") -> can be null
    val idValue: String?,    // ID number - can be null

    // ******************** PICTURE URLS ********************
    val pictureLarge: String,      // Large profile image URL (128x128)
    val pictureMedium: String,     // Medium profile image URL (100x100)
    val pictureThumbnail: String,  // Small thumbnail URL (50x50)

    // ******************** METADATA ********************
    val createdAt: Long = System.currentTimeMillis(), // When user was added to local DB
    val isFromApi: Boolean = true // Distinguish API users from manually created users
)