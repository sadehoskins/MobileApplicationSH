package com.example.myapplicationtestsade.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplicationtestsade.data.models.RandomUser
import com.example.myapplicationtestsade.viewmodel.UserViewModel
import com.example.myapplicationtestsade.ui.theme.PinkTitleText

/**
 * ******************** USER OVERVIEW SCREEN ********************
 * Main screen displaying list of random users
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOverviewScreen(
    userViewModel: UserViewModel,
    onUserClick: (RandomUser) -> Unit,      // Navigate to user detail
    onCameraClick: () -> Unit,              // Navigate to AR/Camera screen
    onSettingsClick: () -> Unit             // Navigate to settings
) {
    // ******************** STATE OBSERVING ********************

    // Observe ViewModel state changes
    val users by userViewModel.users
    val isLoading by userViewModel.isLoading
    val error by userViewModel.error

    // ******************** UI STRUCTURE ********************

    Scaffold(
        // ******************** TOP BAR ********************
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Random Users",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = PinkTitleText
                        )
                    )
                },
                actions = {
                    // Camera button
                    TextButton(onClick = onCameraClick) {
                        Text("📷")
                    }
                    // Settings button
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },

        // ******************** FLOATING ACTION BUTTON ********************
        // Add new random user
        floatingActionButton = {
            FloatingActionButton(
                onClick = { userViewModel.addRandomUser() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Random User",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->

        // ******************** MAIN CONTENT AREA ********************
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ******************** LOADING STATE ********************
                // Show spinner when loading and no users yet
                isLoading && users.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // ******************** ERROR STATE ********************
                // Show error message with retry button
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                userViewModel.clearError()      // Clear error state
                                userViewModel.loadRandomUsers() // Retry loading
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }

                // ******************** SUCCESS STATE ********************
                // Show list of users
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users) { user ->
                            UserCard(
                                user = user,
                                onClick = { onUserClick(user) }    // Navigate to detail
                            )
                        }

                        // Add loading indicator at bottom when adding users
                        if (isLoading && users.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(16.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * ******************** USER CARD COMPONENT ********************
 * Individual user card showing basic info -> profile picture, name, email, and location
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user: RandomUser,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ******************** PROFILE PICTURE ********************
            AsyncImage(
                model = user.picture.thumbnail,    // Use thumbnail for list performance
                contentDescription = "Profile Picture for ${user.name.fullName}",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // ******************** USER INFO ********************
            Column {
                // Full name
                Text(
                    text = user.name.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Email address
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Location (city, country)
                Text(
                    text = "${user.location.city}, ${user.location.country}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}