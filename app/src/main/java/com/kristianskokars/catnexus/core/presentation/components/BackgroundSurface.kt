package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kristianskokars.catnexus.core.presentation.theme.SimpleCatAppTheme

@Composable
fun BackgroundSurface(block: @Composable () -> Unit) {
    SimpleCatAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background,
        ) {
            block()
        }
    }
}
