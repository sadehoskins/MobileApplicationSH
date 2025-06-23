package com.example.myapplicationtestsade.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplicationtestsade.ui.screens.ARScreen
import com.example.myapplicationtestsade.ui.screens.SettingsScreen
import com.example.myapplicationtestsade.ui.screens.UserDetailScreen
import com.example.myapplicationtestsade.ui.screens.UserOverviewScreen
import com.example.myapplicationtestsade.viewmodel.UserViewModel

/**
 * ******************** NAVIGATION GRAPH ********************
 * Defines all app screens and navigation routes
 *
 * Navigation Structure:
 * 1. UserOverviewScreen (Start) → UserDetailScreen
 * 2. UserOverviewScreen → ARScreen (Camera)
 * 3. UserOverviewScreen → SettingsScreen
 * 4. All screens can navigate back to previous
 *
 * Uses single UserViewModel shared across screens for data consistency
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "user_overview"    // App starts with user list
    ) {

        // ******************** USER OVERVIEW SCREEN ********************
        // Main screen showing list of random users
        composable("user_overview") {
            UserOverviewScreen(
                userViewModel = userViewModel,
                // Navigate to user detail when user card is tapped
                onUserClick = { user ->
                    userViewModel.selectUser(user)           // Set selected user
                    navController.navigate("user_detail")    // Navigate to detail
                },
                // Navigate to camera/AR screen
                onCameraClick = {
                    navController.navigate("ar_screen")
                },
                // Navigate to settings screen
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }

        // ******************** USER DETAIL SCREEN ********************
        // Shows detailed info and QR code for selected user
        composable("user_detail") {
            UserDetailScreen(
                userViewModel = userViewModel,
                // Go back to user overview
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ******************** AR/CAMERA SCREEN ********************
        // Camera preview with QR code scanning capabilities
        composable("ar_screen") {
            ARScreen(
                userViewModel = userViewModel,  // ✅ Pass the UserViewModel
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ******************** SETTINGS SCREEN ********************
        // App configuration and preferences
        composable("settings") {
            SettingsScreen(
                // Go back to user overview
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}