package com.kristianskokars.catnexus.lib

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations

val screenSlideTransitionAnimations get() = RootNavGraphDefaultAnimations(
    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
    exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
    popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
    popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
)
