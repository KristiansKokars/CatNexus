package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kristianskokars.catnexus.core.presentation.theme.Gray

@Composable
fun CatNexusDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = Gray.copy(0.2f))
}
