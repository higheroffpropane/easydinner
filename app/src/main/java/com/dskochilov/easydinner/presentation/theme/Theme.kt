package com.dskochilov.easydinner.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF256D5A),
    onPrimary = Color.White,
    secondary = Color(0xFF725B2F),
    tertiary = Color(0xFFB44B31),
    background = Color(0xFFF8F7F2),
    surface = Color(0xFFFFFBF5),
    surfaceVariant = Color(0xFFE8E3D7),
    onSurface = Color(0xFF1E1F1B),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7FD0B9),
    secondary = Color(0xFFD8BD82),
    tertiary = Color(0xFFFFB19F),
    background = Color(0xFF141512),
    surface = Color(0xFF1D1E1A),
    surfaceVariant = Color(0xFF3D4038),
    onSurface = Color(0xFFE7E3D8),
)

@Composable
fun EasyDinnerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
