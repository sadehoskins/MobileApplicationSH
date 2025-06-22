package com.example.myapplicationtestsade.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationtestsade.R

// Create custom font family using just the regular font
val FunanFont = FontFamily(
    Font(R.font.furianhalftone_mawvr)  // Simplified approach
)

// ONLY ONE Typography declaration
val CustomTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FunanFont,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 1.sp
    )
)