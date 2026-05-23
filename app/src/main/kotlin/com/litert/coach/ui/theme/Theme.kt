package com.litert.coach.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CoachGreenLight,
    onPrimary = CoachSurface,
    primaryContainer = CoachGreen,
    onPrimaryContainer = CoachGreenContainer,
    secondary = CoachBlueLight,
    surface = CoachSurface,
    onSurface = CoachOnSurface,
    surfaceVariant = CoachSurfaceVariant,
    error = CoachError
)

private val LightColorScheme = lightColorScheme(
    primary = CoachGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = CoachGreenContainer,
    onPrimaryContainer = CoachOnPrimaryContainer,
    secondary = CoachBlue,
    onSecondary = CoachOnSecondary,
    secondaryContainer = CoachSecondaryContainer,
    onSecondaryContainer = CoachOnSecondaryContainer,
    background = CoachBackground,
    onBackground = CoachOnBackground,
    surface = CoachBackground,
    onSurface = CoachOnSurfaceLight,
    surfaceVariant = CoachSurfaceVariantLight,
    onSurfaceVariant = CoachOnSurfaceVariantLight,
    outline = CoachOutline,
    error = CoachErrorLight,
    onError = CoachOnError,
    errorContainer = CoachErrorContainer,
    onErrorContainer = CoachOnErrorContainer,
)

@Composable
fun CoachTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = CoachTypography, content = content)
}
