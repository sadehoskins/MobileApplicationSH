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

/**
 * ******************** AR/CAMERA SCREEN ********************
 * Camera preview screen with AR overlay capabilities
 *
 * Features:
 * - Camera permission handling
 * - Live camera preview
 * - Flash toggle control
 * - QR code scanning capability (framework ready)
 * - AR overlay UI elements
 * - Back navigation
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ARScreen(
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
                    // Show camera preview with AR overlay
                    CameraPreview(
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

                        // Instructions for future QR scanning
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
                                    text = "🔍 QR Scanner Ready",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Scanning capability will be added in future updates",
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
                            text = "This app needs camera access to scan QR codes and provide AR features",
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
 * Handles camera setup and preview display
 *
 * Features:
 * - Camera lifecycle management
 * - Preview surface setup
 * - Flash control
 * - Image analysis pipeline (ready for QR scanning)
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier
) {
    // ******************** ANDROID CONTEXT AND LIFECYCLE ********************
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Flash control state
    var flashEnabled by remember { mutableStateOf(false) }

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

                // ******************** IMAGE ANALYSIS ********************
                // Setup for future QR code scanning
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { imageProxy ->
                            // TODO: Add QR code scanning logic here
                            // For now, just close the image to prevent memory leaks
                            imageProxy.close()
                        }
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

    // ******************** FLASH TOGGLE BUTTON ********************
    Box(modifier = modifier) {
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
    }
}