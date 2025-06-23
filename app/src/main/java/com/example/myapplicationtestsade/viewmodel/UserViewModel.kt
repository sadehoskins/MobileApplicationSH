package com.example.myapplicationtestsade.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationtestsade.data.database.AppDatabase
import com.example.myapplicationtestsade.data.models.RandomUser
import com.example.myapplicationtestsade.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ******************** ENHANCED USER VIEW MODEL ********************
 * AndroidViewModel with Room database integration
 *
 * Features:
 * - Database-backed user management
 * - Reactive UI updates with Flow
 * - Offline capabilities
 * - Sorting and searching
 * - Manual user creation
 * - Settings operations (clear database, fill database)
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    // ******************** REPOSITORY AND DATABASE ********************

    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao())

    // ******************** UI STATE ********************

    // Users list - now backed by database Flow
    private val _users = MutableStateFlow<List<RandomUser>>(emptyList())
    val users: StateFlow<List<RandomUser>> = _users.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Selected user for detail view
    val selectedUser = mutableStateOf<RandomUser?>(null)

    // Sorting and filtering state
    private val _sortingMode = MutableStateFlow(SortingMode.NEWEST_FIRST)
    val sortingMode: StateFlow<SortingMode> = _sortingMode.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // User count for statistics
    private val _userCount = MutableStateFlow(0)
    val userCount: StateFlow<Int> = _userCount.asStateFlow()

    // ******************** ENUMS ********************

    enum class SortingMode {
        NEWEST_FIRST,
        OLDEST_FIRST,
        NAME_A_TO_Z,
        NAME_Z_TO_A,
        LOCATION_A_TO_Z,
        LOCATION_Z_TO_A
    }

    // ******************** INITIALIZATION ********************

    init {
        // Load users from database on startup
        loadUsersFromDatabase()

        // Load fresh data from API if database is empty
        viewModelScope.launch {
            if (repository.isDatabaseEmpty()) {
                loadRandomUsers(forceRefresh = true)
            }
        }

        // Update user count
        updateUserCount()
    }

    // ******************** DATABASE OPERATIONS ********************

    /**
     * Load users from database based on current sorting mode
     * This replaces the old API-only loading
     */
    private fun loadUsersFromDatabase() {
        viewModelScope.launch {
            try {
                // Collect from the appropriate Flow based on sorting mode
                val flow = when (_sortingMode.value) {
                    SortingMode.NEWEST_FIRST, SortingMode.OLDEST_FIRST -> {
                        repository.getAllUsers()
                    }
                    SortingMode.NAME_A_TO_Z, SortingMode.NAME_Z_TO_A -> {
                        repository.getUsersSortedByName()
                    }
                    SortingMode.LOCATION_A_TO_Z, SortingMode.LOCATION_Z_TO_A -> {
                        repository.getUsersSortedByLocation()
                    }
                }

                // Collect the Flow and update UI state
                flow.collect { userList ->
                    val sortedList = applySorting(userList, _sortingMode.value)
                    val filteredList = applySearch(sortedList, _searchQuery.value)
                    _users.value = filteredList
                    updateUserCount()
                }
            } catch (e: Exception) {
                _error.value = "Failed to load users from database: ${e.message}"
            }
        }
    }

    /**
     * Update user count from database
     */
    private fun updateUserCount() {
        viewModelScope.launch {
            try {
                _userCount.value = repository.getUserCount()
            } catch (e: Exception) {
                // Silent fail for count update
            }
        }
    }

    // ******************** API + DATABASE OPERATIONS ********************

    /**
     * Load random users from API and store in database
     * Enhanced version that works with database
     */
    fun loadRandomUsers(count: Int = 10, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.loadRandomUsers(count, forceRefresh).fold(
                onSuccess = { userList ->
                    // Users are automatically updated via Flow observation
                    // No need to manually update _users.value
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Add a single random user from API
     * Enhanced to work with database
     */
    fun addRandomUser() {
        viewModelScope.launch {
            repository.addRandomUser().fold(
                onSuccess = { newUser ->
                    // User automatically appears via Flow observation
                    // Optional: Show success message
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
        }
    }

    // ******************** USER MANAGEMENT ********************

    /**
     * Create a manual user (bonus feature)
     * @param user User data to create
     */
    fun createManualUser(user: RandomUser) {
        viewModelScope.launch {
            repository.createManualUser(user).fold(
                onSuccess = {
                    // User automatically appears via Flow observation
                },
                onFailure = { exception ->
                    _error.value = "Failed to create user: ${exception.message}"
                }
            )
        }
    }

    /**
     * Update existing user (bonus feature)
     * @param user Updated user data
     */
    fun updateUser(user: RandomUser) {
        viewModelScope.launch {
            repository.updateUser(user).fold(
                onSuccess = {
                    // Update is reflected via Flow observation
                    // Also update selected user if it's the same one
                    if (selectedUser.value?.login?.uuid == user.login.uuid) {
                        selectedUser.value = user
                    }
                },
                onFailure = { exception ->
                    _error.value = "Failed to update user: ${exception.message}"
                }
            )
        }
    }

    /**
     * Delete user by ID
     * @param uniqueId User ID to delete
     */
    fun deleteUser(uniqueId: String) {
        viewModelScope.launch {
            repository.deleteUser(uniqueId).fold(
                onSuccess = {
                    // User automatically removed via Flow observation
                    // Clear selected user if it was deleted
                    if (selectedUser.value?.login?.uuid == uniqueId) {
                        selectedUser.value = null
                    }
                },
                onFailure = { exception ->
                    _error.value = "Failed to delete user: ${exception.message}"
                }
            )
        }
    }

    // ******************** SETTINGS OPERATIONS ********************

    /**
     * Empty database (Settings screen feature)
     * Removes all users from local storage
     */
    fun emptyDatabase() {
        viewModelScope.launch {
            _isLoading.value = true

            repository.deleteAllUsers().fold(
                onSuccess = { deletedCount ->
                    _isLoading.value = false
                    selectedUser.value = null
                    // Users list automatically updates via Flow observation
                    // Optional: Show success message with count
                },
                onFailure = { exception ->
                    _error.value = "Failed to empty database: ${exception.message}"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Fill database (Settings screen feature)
     * Loads specified number of users from API
     */
    fun fillDatabase(count: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.fillDatabase(count).fold(
                onSuccess = { userList ->
                    _isLoading.value = false
                    // Users automatically appear via Flow observation
                },
                onFailure = { exception ->
                    _error.value = "Failed to fill database: ${exception.message}"
                    _isLoading.value = false
                }
            )
        }
    }

    // ******************** SORTING AND FILTERING ********************

    /**
     * Change sorting mode
     * @param mode New sorting mode
     */
    fun setSortingMode(mode: SortingMode) {
        if (_sortingMode.value != mode) {
            _sortingMode.value = mode
            loadUsersFromDatabase() // Reload with new sorting
        }
    }

    /**
     * Apply sorting to user list
     * @param users List to sort
     * @param mode Sorting mode
     * @return Sorted list
     */
    private fun applySorting(users: List<RandomUser>, mode: SortingMode): List<RandomUser> {
        return when (mode) {
            SortingMode.NEWEST_FIRST -> users // Already sorted by createdAt DESC in query
            SortingMode.OLDEST_FIRST -> users.reversed()
            SortingMode.NAME_A_TO_Z -> users // Already sorted by name in query
            SortingMode.NAME_Z_TO_A -> users.reversed()
            SortingMode.LOCATION_A_TO_Z -> users // Already sorted by location in query
            SortingMode.LOCATION_Z_TO_A -> users.reversed()
        }
    }

    /**
     * Update search query
     * @param query Search term
     */
    fun setSearchQuery(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
            applyCurrentSearch()
        }
    }

    /**
     * Apply current search query
     */
    private fun applyCurrentSearch() {
        viewModelScope.launch {
            try {
                val flow = if (_searchQuery.value.isBlank()) {
                    // No search - use normal sorting
                    when (_sortingMode.value) {
                        SortingMode.NEWEST_FIRST, SortingMode.OLDEST_FIRST -> {
                            repository.getAllUsers()
                        }
                        SortingMode.NAME_A_TO_Z, SortingMode.NAME_Z_TO_A -> {
                            repository.getUsersSortedByName()
                        }
                        SortingMode.LOCATION_A_TO_Z, SortingMode.LOCATION_Z_TO_A -> {
                            repository.getUsersSortedByLocation()
                        }
                    }
                } else {
                    // Apply search
                    repository.searchUsers(_searchQuery.value)
                }

                flow.collect { userList ->
                    val sortedList = applySorting(userList, _sortingMode.value)
                    _users.value = sortedList
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            }
        }
    }

    /**
     * Apply search filter to user list
     * @param users List to filter
     * @param query Search term
     * @return Filtered list
     */
    private fun applySearch(users: List<RandomUser>, query: String): List<RandomUser> {
        if (query.isBlank()) return users

        val lowerQuery = query.lowercase()
        return users.filter { user ->
            user.name.fullName.lowercase().contains(lowerQuery) ||
                    user.email.lowercase().contains(lowerQuery) ||
                    user.location.city.lowercase().contains(lowerQuery) ||
                    user.location.country.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Clear search query
     */
    fun clearSearch() {
        setSearchQuery("")
    }

    // ******************** NAVIGATION HELPERS ********************

    /**
     * Select user for detail view
     * @param user User to select
     */
    fun selectUser(user: RandomUser) {
        selectedUser.value = user
    }

    /**
     * Clear selected user
     */
    fun clearSelectedUser() {
        selectedUser.value = null
    }

    // ******************** ERROR HANDLING ********************

    /**
     * Clear current error state
     */
    fun clearError() {
        _error.value = null
    }

    // ******************** UTILITY FUNCTIONS ********************

    /**
     * Get user by ID (for QR code scanning)
     * @param uniqueId User unique identifier
     * @return User if found, null otherwise
     */
    fun getUserById(uniqueId: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserById(uniqueId)
                if (user != null) {
                    selectUser(user)
                } else {
                    _error.value = "User not found with ID: $uniqueId"
                }
            } catch (e: Exception) {
                _error.value = "Failed to find user: ${e.message}"
            }
        }
    }

    /**
     * Check if database is empty
     * @return Boolean indicating if database has users
     */
    suspend fun isDatabaseEmpty(): Boolean {
        return repository.isDatabaseEmpty()
    }

    /**
     * Get statistics about stored users
     * @return Map with user statistics
     */
    fun getUserStatistics() {
        viewModelScope.launch {
            try {
                // Could be expanded to show API vs manual users, countries, etc.
                updateUserCount()
            } catch (e: Exception) {
                // Silent fail for statistics
            }
        }
    }

    // ******************** LIFECYCLE ********************

    override fun onCleared() {
        super.onCleared()
        // ViewModel cleanup is handled automatically by StateFlow and viewModelScope
    }
}