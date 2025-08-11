package com.kristianskokars.catnexus.core.presentation

import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Gray900
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint

val DefaultHazeStyle = HazeStyle(tint = HazeTint(Black.copy(alpha = 0.72f)), blurRadius = 24.dp)
val ElevatedHazeStyle = HazeStyle(tint = HazeTint(Gray900.copy(alpha = 0.72f)), blurRadius = 24.dp)
