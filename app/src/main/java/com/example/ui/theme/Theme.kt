package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberDarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = TextOnDark,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = EmeraldDark,
    secondary = AmberPrimary,
    onSecondary = TextOnDark,
    secondaryContainer = AmberLight,
    onSecondaryContainer = AmberText,
    tertiary = IndigoPrimary,
    onTertiary = TextOnDark,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = TextOnDark
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberDarkColorScheme,
        typography = Typography,
        content = content
    )
}

