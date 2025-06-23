package com.example.myapplicationtestsade.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplicationtestsade.utils.QRCodeGenerator
import com.example.myapplicationtestsade.viewmodel.UserViewModel

/**
 * ******************** USER DETAIL SCREEN ********************
 * Shows comprehensive user information and generated QR code
 * Features:
 * - Large profile picture
 * - Complete user information (name, contact, location)
 * - Auto-generated QR code with user data
 * - Scrollable content for all screen sizes
 * - Back navigation to user list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit
) {
    // ******************** STATE OBSERVING ********************

    // Get the currently selected user from ViewModel
    val selectedUser by userViewModel.selectedUser

    // ******************** UI STRUCTURE ********************

    selectedUser?.let { user ->
        Scaffold(
            // ******************** TOP BAR WITH BACK BUTTON ********************
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = user.name.fullName,
                            maxLines = 1
                        )
                    },
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

                // ******************** PROFILE PICTURE ********************
                AsyncImage(
                    model = user.picture.large,    // Use large image for detail view
                    contentDescription = "Profile Picture for ${user.name.fullName}",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ******************** PERSONAL INFORMATION CARD ********************
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        DetailRow("Full Name", user.name.fullName)
                        DetailRow("Email", user.email)
                        DetailRow("Phone", user.phone)
                        DetailRow("Cell", user.cell)
                        DetailRow("Gender", user.gender.replaceFirstChar { it.uppercase() })
                        DetailRow("Age", "${user.dob.age} years old")
                        DetailRow("Nationality", user.nat)
                        DetailRow("Username", user.login.username)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ******************** LOCATION INFORMATION CARD ********************
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Location",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        DetailRow("Street", "${user.location.street.number} ${user.location.street.name}")
                        DetailRow("City", user.location.city)
                        DetailRow("State", user.location.state)
                        DetailRow("Country", user.location.country)
                        DetailRow("Postcode", user.location.postcode)
                        DetailRow("Coordinates", "${user.location.coordinates.latitude}, ${user.location.coordinates.longitude}")
                        DetailRow("Timezone", "${user.location.timezone.offset} (${user.location.timezone.description})")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ******************** QR CODE CARD ********************
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "User QR Code",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // ******************** QR CODE GENERATION ********************
                        val qrBitmap by remember {
                            derivedStateOf {
                                // Generate QR code with user information
                                QRCodeGenerator.generateQRCode(
                                    text = buildString {
                                        appendLine("UserID: ${user.login.uuid}")
                                        appendLine("Name: ${user.name.fullName}")
                                        appendLine("Email: ${user.email}")
                                        appendLine("Phone: ${user.phone}")
                                        appendLine("Location: ${user.location.city}, ${user.location.country}")
                                    },
                                    width = 300,
                                    height = 300
                                )
                            }
                        }

                        // Display QR code if generated successfully
                        qrBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "QR Code containing information for ${user.name.fullName}",
                                modifier = Modifier.size(250.dp)
                            )
                        } ?: run {
                            // Show error if QR generation failed
                            Text(
                                text = "Failed to generate QR code",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Scan this QR code to get user information",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Add bottom padding for better scrolling
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    } ?: run {
        // ******************** NO USER SELECTED STATE ********************
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No user selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBackClick) {
                    Text("Go Back")
                }
            }
        }
    }
}

/**
 * ******************** DETAIL ROW COMPONENT ********************
 * Reusable component for displaying label-value pairs
 * Used throughout the detail screen for consistent formatting
 */
@Composable
fun DetailRow(label: String, value: String) {
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