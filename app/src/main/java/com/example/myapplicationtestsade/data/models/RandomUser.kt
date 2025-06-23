package com.example.myapplicationtestsade.data.models

import com.google.gson.annotations.SerializedName

/**
 * ******************** API RESPONSE MODELS ********************
 * These data classes represent the JSON structure from randomuser.me API
 * Using @SerializedName for fields that don't match Kotlin naming conventions
 * Root response wrapper from randomuser.me API
 * Contains the list of users and metadata about the request
 */
data class RandomUserResponse(
    val results: List<RandomUser>,    // Array of user objects
    val info: Info                    // Metadata about the API response
)

/**
 * Main user data class - represents a single random person
 * Each user gets a unique ID for QR code generation
 */
data class RandomUser(
    val gender: String,               // "male, "female", "non-binary" or "other"
    val name: Name,                   // Full name with title/first/last
    val location: Location,           // Address and geographic data
    val email: String,                // Email address
    val login: Login,                 // Login credentials and UUID
    val dob: DateOfBirth,             // Date of birth and calculated age
    val registered: Registered,       // Registration date and age
    val phone: String,                // Phone number
    val cell: String,                 // Cell phone number
    val id: Id,                       // National ID (may be null)
    val picture: Picture,             // Profile pictures in different sizes
    val nat: String                   // Nationality code (e.g., "US", "GB")
)

/**
 * ******************** NAME COMPONENTS ********************
 * Person's full name broken into components
 */
data class Name(
    val title: String,                // Mr, Ms, Dr. etc.
    val first: String,                // First name
    val last: String                  // Last name
) {
    // Computed property: combines all name parts for easy display
    val fullName: String get() = "$title $first $last"
}

/**
 * ******************** LOCATION DATA ********************
 * Complete address and location information
 * Includes street, city, country, and geographic coordinates
 */
data class Location(
    val street: Street,               // Street number and name
    val city: String,                 // City name
    val state: String,                // State/province
    val country: String,              // Country name
    val postcode: String,             // Postal/ZIP code
    val coordinates: Coordinates,     // Latitude/longitude
    val timezone: Timezone            // Timezone information
)

/**
 * Street address components
 */
data class Street(
    val number: Int,                  // House/building number
    val name: String                  // Street name
)

/**
 * Geographic coordinates for mapping
 */
data class Coordinates(
    val latitude: String,             // Latitude as string (API format)
    val longitude: String             // Longitude as string (API format)
)

/**
 * Timezone information
 */
data class Timezone(
    val offset: String,               // UTC offset (e.g., "+5:30")
    val description: String           // Human readable description
)

/**
 * ******************** AUTHENTICATION DATA ********************
 * Login credentials and unique identifiers
 * Contains various hash formats for different security needs
 */
data class Login(
    val uuid: String,                 // Unique user identifier (used for our uniqueId)
    val username: String,             // Generated username
    val password: String,             // Generated password
    val salt: String,                 // Password salt
    val md5: String,                  // MD5 hash
    val sha1: String,                 // SHA1 hash
    val sha256: String                // SHA256 hash
)

/**
 * ******************** DATE INFORMATION ********************
 * Date of birth information
 */
data class DateOfBirth(
    val date: String,                 // Birth date in ISO format
    val age: Int                      // Calculated age in years
)

/**
 * Registration date information
 */
data class Registered(
    val date: String,                 // Registration date in ISO format
    val age: Int                      // Years since registration
)

/**
 * ******************** IDENTIFICATION ********************
 * National identification information
 * Note: May be null for some countries/users
 */
data class Id(
    val name: String?,                // ID type name (e.g., "SSN", "BSN")
    val value: String?                // ID number (may be null)
)

/**
 * ******************** PROFILE PICTURES ********************
 * Profile pictures in different resolutions
 * API provides three sizes for different UI needs
 */
data class Picture(
    val large: String,                // Large profile image (128x128)
    val medium: String,               // Medium profile image (100x100)
    val thumbnail: String             // Small thumbnail (50x50)
)

/**
 * ******************** API METADATA ********************
 * Information about the API response
 */
data class Info(
    val seed: String,                 // Random seed used for generation
    val results: Int,                 // Number of results returned
    val page: Int,                    // Current page number
    val version: String               // API version used
)