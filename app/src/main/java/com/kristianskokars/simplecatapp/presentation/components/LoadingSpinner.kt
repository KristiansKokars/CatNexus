package com.kristianskokars.simplecatapp.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kristianskokars.simplecatapp.presentation.ui.theme.Orange

@Composable
fun LoadingSpinner(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(48.dp),
        color = Orange,
    )
}
