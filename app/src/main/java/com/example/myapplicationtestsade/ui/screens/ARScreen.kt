package com.example.myapplicationtestsade.ui.screens

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// *********************** ADD THESE IMPORTS ***********************
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.myapplicationtestsade.viewmodel.UserViewModel
import com.example.myapplicationtestsade.utils.QRCodeAnalyzer
import com.example.myapplicationtestsade.utils.QRCodeParser

/**
 * ******************** AR/CAMERA SCREEN ********************
 * Camera preview screen with QR code scanning capabilities
 *
 * Features:
 * - Camera permission handling
 * - Live camera preview
 * - Flash toggle control
 * - Real-time QR code scanning
 * - AR overlay UI elements
 * - User lookup from database
 * - Back navigation
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ARScreen(
    userViewModel: UserViewModel, // ✅ ADD THIS PARAMETER
    onBackClick: () -> Unit
) {
    // ******************** PERMISSION HANDLING ********************

    // Request camera permission using Accompanist library
    val cameraPermissionState: PermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // Automatically request permission when screen loads
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // ******************** UI STRUCTURE ********************

    Scaffold(
        // ******************** TOP BAR ********************
        topBar = {
            TopAppBar(
                title = { Text("AR Camera") },
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

        // ******************** MAIN CONTENT ********************
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ******************** CAMERA PERMISSION GRANTED ********************
                cameraPermissionState.status.isGranted -> {
                    // Show camera preview with QR scanning
                    CameraPreview(
                        userViewModel = userViewModel, // ✅ PASS VIEWMODEL
                        modifier = Modifier.fillMaxSize()
                    )

                    // ******************** AR OVERLAY UI ********************
                    // Overlay UI elements on top of camera preview
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = "Point camera at QR codes to scan user data",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Scanner Status
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🔍 QR Scanner Active",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Scan QR codes to find users in database",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // ******************** CAMERA PERMISSION DENIED ********************
                else -> {
                    // Show permission request UI
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📷",
                            style = MaterialTheme.typography.displayMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Camera Permission Required",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "This app needs camera access to scan QR codes and find users",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { cameraPermissionState.launchPermissionRequest() }
                        ) {
                            Text("Grant Camera Permission")
                        }
                    }
                }
            }
        }
    }
}

/**
 * ******************** CAMERA PREVIEW COMPONENT ********************
 * Handles camera setup and preview display with QR code scanning
 *
 * Features:
 * - Camera lifecycle management
 * - Preview surface setup
 * - Flash control
 * - Real-time QR code scanning
 * - User database lookup
 * - AR overlay for detected users
 */
@Composable
fun CameraPreview(
    userViewModel: UserViewModel, // ✅ ADD THIS PARAMETER
    modifier: Modifier = Modifier
) {
    // ******************** ANDROID CONTEXT AND LIFECYCLE ********************
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Flash control state
    var flashEnabled by remember { mutableStateOf(false) }

    // QR scanning state variables
    var scanningActive by remember { mutableStateOf(true) }
    var detectedUser by remember { mutableStateOf<String?>(null) }
    var lastScannedId by remember { mutableStateOf<String?>(null) }
    var scanningStatus by remember { mutableStateOf("Scanning for QR codes...") }

    // ******************** QR CODE DETECTION CALLBACK ********************
    val onQrCodeDetected: (String) -> Unit = { qrContent ->
        if (scanningActive) {
            // Parse the QR content to extract user ID
            val userId = QRCodeParser.extractUserId(qrContent)

            if (userId != null && userId != lastScannedId) {
                // Update scanning status
                scanningStatus = "QR Code detected! Looking up user..."
                lastScannedId = userId

                // Look up user in database using ViewModel
                userViewModel.getUserById(userId)

                // Show detected content temporarily
                detectedUser = "User ID: $userId"
                scanningActive = false // Pause scanning to show result
            } else if (userId == null) {
                // Not a user QR code
                scanningStatus = "QR code detected but not a user code"
                detectedUser = "Unknown QR Code"
                scanningActive = false
            }
        }
    }

    // Re-enable scanning after 3 seconds
    LaunchedEffect(detectedUser) {
        if (detectedUser != null) {
            delay(3000) // Wait 3 seconds
            detectedUser = null
            scanningActive = true
            scanningStatus = "Scanning for QR codes..."
        }
    }

    // ******************** CAMERA PREVIEW SETUP ********************
    AndroidView(
        factory = { ctx ->
            // Create PreviewView for displaying camera feed
            val previewView = PreviewView(ctx)
            val executor = Executors.newSingleThreadExecutor()

            // Setup camera provider
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // ******************** CAMERA PREVIEW ********************
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // ******************** QR CODE ANALYSIS ********************
                // Setup QR code scanning with ML Kit
                val qrCodeAnalyzer = QRCodeAnalyzer(onQrCodeDetected)

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor, qrCodeAnalyzer)
                    }

                // ******************** CAMERA SELECTOR ********************
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind any previous camera instances
                    cameraProvider.unbindAll()

                    // Bind camera to lifecycle with preview and analysis
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )

                    // ******************** FLASH CONTROL ********************
                    // Enable/disable flash based on state
                    camera.cameraControl.enableTorch(flashEnabled)

                } catch (exc: Exception) {
                    // Handle camera setup errors
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )

    // ******************** OVERLAY UI ELEMENTS ********************
    Box(modifier = modifier) {
        // Flash toggle button
        FloatingActionButton(
            onClick = { flashEnabled = !flashEnabled },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = if (flashEnabled) "🔦" else "💡",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // ******************** QR DETECTION OVERLAY ********************
        if (detectedUser != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✓ QR Code Detected!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detectedUser ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Show selected user if found
                    userViewModel.selectedUser.value?.let { user ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Found: ${user.name.fullName}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // ******************** SCANNING STATUS ********************
        if (scanningActive && detectedUser == null) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "🔍 $scanningStatus",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}