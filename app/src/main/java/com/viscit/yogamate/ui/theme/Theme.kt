package com.viscit.yogamate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Aplikasi hanya punya tema terang sesuai desain Figma (#FFFCF7)
private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = Cream,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = LavenderLight,
    onSurfaceVariant = Muted,
    outline = OutlineSoft,
    error = Danger,
    secondary = Lavender,
    tertiary = Lime,
)

@Composable
fun YogamateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
