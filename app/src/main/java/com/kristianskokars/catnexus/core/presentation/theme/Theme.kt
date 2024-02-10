package com.kristianskokars.catnexus.core.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = Orange,
    surface = Black,
    background = Black,
    error = Red
)

@Composable
fun SimpleCatAppTheme(content: @Composable () -> Unit) {
    val colors = DarkColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
