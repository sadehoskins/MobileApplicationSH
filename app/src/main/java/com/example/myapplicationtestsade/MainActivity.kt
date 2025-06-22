package com.example.myapplicationtestsade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myapplicationtestsade.navigation.NavGraph
import com.example.myapplicationtestsade.ui.theme.MyApplicationTestSadeTheme
import com.example.myapplicationtestsade.viewmodel.UserViewModel

/**
 * ******************** MAIN ACTIVITY ********************
 * Entry point of the Random User App
 *
 * Responsibilities:
 * - Initialize the app theme
 * - Create and manage the UserViewModel
 * - Setup navigation controller
 * - Provide the main navigation graph
 *
 * Architecture:
 * - Single Activity with Compose Navigation
 * - Shared ViewModel across all screens
 * - Material Design 3 theming
 */
class MainActivity : ComponentActivity() {

    /**
     * ******************** VIEW MODEL ********************
     * Create UserViewModel using Android's by viewModels() delegate
     * This ensures ViewModel survives configuration changes
     * ViewModel is shared across all navigation destinations
     */
    private val userViewModel: UserViewModel by viewModels()

    /**
     * ******************** ACTIVITY LIFECYCLE ********************
     * Called when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ******************** COMPOSE UI SETUP ********************
        setContent {
            // Apply app theme (Material Design 3)
            MyApplicationTestSadeTheme {
                // Surface provides background color and handles theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ******************** NAVIGATION SETUP ********************
                    // Create navigation controller for screen transitions
                    val navController = rememberNavController()

                    // Setup navigation graph with all screens
                    NavGraph(
                        navController = navController,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }
}
