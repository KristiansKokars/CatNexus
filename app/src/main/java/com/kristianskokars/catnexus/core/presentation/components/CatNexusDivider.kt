package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CatNexusDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = Color.Gray.copy(alpha = 0.25f))
}
