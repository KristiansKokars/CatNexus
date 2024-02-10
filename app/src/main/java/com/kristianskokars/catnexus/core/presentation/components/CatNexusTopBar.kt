package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatNexusTopBar(
    hazeState: HazeState,
    isBorderVisible: Boolean,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            modifier = Modifier
                .hazeChild(state = hazeState)
                .fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            title = { content() }
        )
        AnimatedVisibility(visible = isBorderVisible) {
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.25f))
        }
    }
}
