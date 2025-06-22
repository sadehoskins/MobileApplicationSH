package com.example.myapplicationtestsade.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * ******************** SETTINGS SCREEN ********************
 * App configuration and preferences screen
 *
 * Features:
 * - App behavior settings (dark mode, notifications, etc.)
 * - Storage management (cache size, clear cache)
 * - About section with app information
 * - Privacy policy and terms links
 * - User preferences with persistent state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    // ******************** SETTINGS STATE ********************
    // These would normally be saved to SharedPreferences or DataStore
    // For now -> using local state (resets when app restarts)

    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var autoRefresh by remember { mutableStateOf(false) }
    var cacheSize by remember { mutableStateOf(50f) }
    var analyticsEnabled by remember { mutableStateOf(true) }

    // ******************** UI STRUCTURE ********************

    Scaffold(
        // ******************** TOP BAR ********************
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back to User List"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        // ******************** SCROLLABLE CONTENT ********************
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ******************** APP SETTINGS SECTION ********************
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ******************** DARK MODE TOGGLE ********************
                    SettingToggleRow(
                        title = "Dark Mode",
                        description = "Enable dark theme for better night viewing",
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // ******************** NOTIFICATIONS TOGGLE ********************
                    SettingToggleRow(
                        title = "Notifications",
                        description = "Receive app notifications and updates",
                        checked = notifications,
                        onCheckedChange = { notifications = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // ******************** AUTO REFRESH TOGGLE ********************
                    SettingToggleRow(
                        title = "Auto Refresh",
                        description = "Automatically refresh user list periodically",
                        checked = autoRefresh,
                        onCheckedChange = { autoRefresh = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // ******************** ANALYTICS TOGGLE ********************
                    SettingToggleRow(
                        title = "Analytics",
                        description = "Help improve the app by sharing usage data",
                        checked = analyticsEnabled,
                        onCheckedChange = { analyticsEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ******************** STORAGE SETTINGS SECTION ********************
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Storage",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ******************** CACHE SIZE SLIDER ********************
                    Text(
                        text = "Cache Size: ${cacheSize.toInt()} MB",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Slider(
                        value = cacheSize,
                        onValueChange = { cacheSize = it },
                        valueRange = 10f..200f,
                        steps = 18,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Controls how much storage the app uses for cached images and data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ******************** CLEAR CACHE BUTTON ********************
                    Button(
                        onClick = {
                            // TODO: Implement cache clearing logic
                            // For now, just reset cache size
                            cacheSize = 50f
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear Cache")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ******************** STORAGE INFO ********************
                    Text(
                        text = "Used: ~${(cacheSize * 0.7).toInt()} MB | Available: ${(200 - cacheSize).toInt()} MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ******************** ABOUT SECTION ********************
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ******************** APP INFO ********************
                    InfoRow("App Name", "Random User App")
                    InfoRow("Version", "1.0.0")
                    InfoRow("Build", "2025.01.22")
                    InfoRow("API Source", "randomuser.me")
                    InfoRow("Developer", "Your Name")

                    Spacer(modifier = Modifier.height(16.dp))

                    // ******************** LEGAL BUTTONS ********************
                    OutlinedButton(
                        onClick = {
                            // TODO: Open privacy policy
                            // For now, just a placeholder
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Privacy Policy")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            // TODO: Open terms of service
                            // For now, just a placeholder
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Terms of Service")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            // TODO: Open licenses page
                            // For now, just a placeholder
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Source Licenses")
                    }
                }
            }

            // Add bottom padding for better scrolling
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * ******************** SETTING TOGGLE ROW COMPONENT ********************
 * Reusable component for settings with toggle switches
 */
@Composable
fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * ******************** INFO ROW COMPONENT ********************
 * Reusable component for displaying app information
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}