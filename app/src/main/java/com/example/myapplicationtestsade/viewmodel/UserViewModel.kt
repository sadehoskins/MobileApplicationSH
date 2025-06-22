package com.example.myapplicationtestsade.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationtestsade.data.models.RandomUser
import com.example.myapplicationtestsade.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Random User data and UI state
 * Handles API calls, loading states, and user interactions
 */
class UserViewModel : ViewModel() {
    // Repository instance for API calls
    private val repository = UserRepository()

    // ******************** STATE MANAGEMENT ********************

    // List of all fetched users - private mutable, public read-only
    private val _users = mutableStateOf<List<RandomUser>>(emptyList())
    val users: State<List<RandomUser>> = _users

    // Currently selected user for detail view
    private val _selectedUser = mutableStateOf<RandomUser?>(null)
    val selectedUser: State<RandomUser?> = _selectedUser

    // Loading state for showing progress indicators
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Error state for displaying error messages to user
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // ******************** INITIALIZATION ********************

    init {
        // Load initial user data when ViewModel is created
        loadRandomUsers()
    }

    // ******************** PUBLIC FUNCTIONS ********************

    /**
     * Loads a fresh list of random users from the API
     * Updates loading state and handles errors
     */
    fun loadRandomUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Call repository and handle result
            repository.getRandomUsers(10).fold(
                onSuccess = { userList ->
                    _users.value = userList
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
     * Sets the selected user for navigation to detail screen
     * @param user The user to select for detail view
     */
    fun selectUser(user: RandomUser) {
        _selectedUser.value = user
    }

    /**
     * Adds a single new random user to the existing list
     * Used by the FAB (Floating Action Button) in the UI
     */
    fun addRandomUser() {
        viewModelScope.launch {
            repository.getRandomUser().fold(
                onSuccess = { newUser ->
                    // Add new user to existing list instead of replacing
                    _users.value = _users.value + newUser
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
        }
    }

    /**
     * Clears the current error state
     * Used when user dismisses error or retries action
     */
    fun clearError() {
        _error.value = null
    }
}