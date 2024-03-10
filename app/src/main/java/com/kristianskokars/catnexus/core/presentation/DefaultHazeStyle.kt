package com.kristianskokars.catnexus.core.presentation

import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Gray900
import dev.chrisbanes.haze.HazeStyle

val DefaultHazeStyle = HazeStyle(tint = Black.copy(alpha = 0.72f), blurRadius = 24.dp)
val ElevatedHazeStyle = HazeStyle(tint = Gray900.copy(alpha = 0.72f), blurRadius = 24.dp)
