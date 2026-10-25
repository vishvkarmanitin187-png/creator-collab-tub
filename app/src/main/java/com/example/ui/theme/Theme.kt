package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkNavyPurpleColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = Color.White,
    secondary = ElectricCyan,
    onSecondary = Color.Black,
    secondaryContainer = NavySurfaceVariant,
    onSecondaryContainer = CyanAccent,
    tertiary = NeonPink,
    onTertiary = Color.White,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    outlineVariant = NavyBorder.copy(alpha = 0.5f),
    error = RoseError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkNavyPurpleColorScheme,
        typography = Typography,
        content = content
    )
}
