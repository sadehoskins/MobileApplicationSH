package com.example.myapplicationtestsade.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * ******************** QR CODE ANALYZER ********************
 * Analyzes camera frames to detect QR codes using ML Kit
 * Features:
 * - Real-time QR code detection
 * - Extract text content from QR codes
 * - Parse user information from QR code data
 * - Callback system for detected QR codes
 */
class QRCodeAnalyzer(
    private val onQRCodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // ML Kit barcode scanner instance
    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // Convert CameraX ImageProxy to ML Kit InputImage
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            // Process the image for barcodes/QR codes
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Handle detected QR codes
                    processBarcodes(barcodes)
                }
                .addOnFailureListener { exception ->
                    // Handle detection errors
                    exception.printStackTrace()
                }
                .addOnCompleteListener {
                    // Always close the image to prevent memory leaks
                    imageProxy.close()
                }
        } else {
            // No image available -> close the proxy
            imageProxy.close()
        }
    }

    /**
     * Process detected barcodes and extract QR code content
     */
    private fun processBarcodes(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            // Check if it's a QR code
            if (barcode.format == Barcode.FORMAT_QR_CODE) {
                // Extract the raw text content
                val qrContent = barcode.rawValue

                if (!qrContent.isNullOrEmpty()) {
                    // Call the callback with detected QR content
                    onQRCodeDetected(qrContent)
                    break // Process only the first QR code found
                }
            }
        }
    }
}

/**
 * ******************** QR CODE PARSER ********************
 */
object QRCodeParser {

    /**
     * Parse user information from QR code text
     * Expected format:
     * UserID: uuid_timestamp
     * Name: Mr John Doe
     * Email: john@example.com
     * Phone: +1234567890
     * Location: City, Country
     */
    fun parseUserData(qrContent: String): QRUserData? {
        return try {
            val lines = qrContent.split("\n")
            var userId: String? = null
            var name: String? = null
            var email: String? = null
            var phone: String? = null
            var location: String? = null

            for (line in lines) {
                when {
                    line.startsWith("UserID:") -> {
                        userId = line.substringAfter("UserID:").trim()
                    }
                    line.startsWith("Name:") -> {
                        name = line.substringAfter("Name:").trim()
                    }
                    line.startsWith("Email:") -> {
                        email = line.substringAfter("Email:").trim()
                    }
                    line.startsWith("Phone:") -> {
                        phone = line.substringAfter("Phone:").trim()
                    }
                    line.startsWith("Location:") -> {
                        location = line.substringAfter("Location:").trim()
                    }
                }
            }

            // Return parsed data if we have essential information
            if (!userId.isNullOrEmpty()) {
                QRUserData(
                    userId = userId,
                    name = name,
                    email = email,
                    phone = phone,
                    location = location
                )
            } else {
                null // Not a user QR code
            }
        } catch (e: Exception) {
            null // Parsing failed
        }
    }

    /**
     * Extract just the database unique ID from QR code
     */
    fun extractUserId(qrContent: String): String? {
        return parseUserData(qrContent)?.userId
    }
}

/**
 * Data class to hold parsed QR code user information
 */
data class QRUserData(
    val userId: String,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val location: String? = null
)