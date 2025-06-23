package com.example.myapplicationtestsade.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.myapplicationtestsade.ui.theme.CustomTypography

private val PinkLightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = PinkPrimaryLight,
    onPrimaryContainer = PinkOnBackground,

    secondary = PinkSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = PinkSecondaryLight,
    onSecondaryContainer = PinkOnBackground,

    background = PinkBackground,
    onBackground = PinkOnBackground,
    surface = PinkSurface,
    onSurface = PinkOnSurface,
    surfaceVariant = PinkSurfaceVariant,
    onSurfaceVariant = PinkOnBackground,

    error = PinkError,
    onError = PinkOnError
)

@Composable
fun MyApplicationTestSadeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = PinkLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}