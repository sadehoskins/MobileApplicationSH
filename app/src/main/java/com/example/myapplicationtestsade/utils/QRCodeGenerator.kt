package com.example.myapplicationtestsade.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*
import androidx.core.graphics.createBitmap

/**
 * ******************** QR CODE GENERATOR UTILITY ********************
 * Singleton object for generating QR codes from text data
 * Uses ZXing (Zebra Crossing) library for QR code generation
 * Converts text data into bitmap images that can be displayed in UI
 *
 * Common use cases:
 * - User information QR codes
 * - Contact sharing
 * - URL sharing
 * - Data export/import
 */
object QRCodeGenerator {

    /**
     * Generates a QR code bitmap from text data
     * )
     */
    fun generateQRCode(text: String, width: Int = 512, height: Int = 512): Bitmap? {
        return try {
            // ******************** QR WRITER SETUP ********************

            /**
             * QRCodeWriter: Main class for generating QR codes
             * Part of ZXing library for barcode generation
             */
            val writer = QRCodeWriter()

            /**
             * Encoding hints for QR code generation
             * Configures character encoding and margins
             */
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"    // Support international characters
            hints[EncodeHintType.MARGIN] = 1                 // Minimal white border around QR code

            // ******************** QR CODE GENERATION ********************

            /**
             * Generate the QR code bit matrix
             * BitMatrix: 2D array of black/white pixels representing the QR code
             */
            val bitMatrix: BitMatrix = writer.encode(
                text,                    // Text to encode
                BarcodeFormat.QR_CODE,   // Barcode format (QR code)
                width,                   // Image width
                height,                  // Image height
                hints                    // Encoding configuration
            )

            // ******************** BITMAP CREATION ********************

            /**
             * Create Android bitmap from bit matrix
             * ARGB_8888: Full color support
             */
            val bitmap = createBitmap(width, height)

            /**
             * Convert bit matrix to colored pixels
             * Using modern approach: create pixel array first, then set all at once
             */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    // Set pixel color: black for QR code data, white for background
                    pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }

            // Set all pixels at once
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

            // Return the generated bitmap
            bitmap

        } catch (e: WriterException) {
            // ******************** ERROR HANDLING ********************

            /**
             * Handle QR code generation errors:
             * - Text too long for QR code format
             * - Invalid characters
             * - Encoding configuration errors
             */
            e.printStackTrace()
            null    // Return null to indicate failure
        }
    }

}