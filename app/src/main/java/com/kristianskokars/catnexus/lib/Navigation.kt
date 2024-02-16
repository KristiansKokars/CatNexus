package com.kristianskokars.catnexus.lib

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.kristianskokars.catnexus.feature.NavGraphs
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.Direction

val screenSlideTransitionAnimations get() = RootNavGraphDefaultAnimations(
    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
    exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
    popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
    popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
)

fun DestinationsNavigator.navigateToBottomBarDestination(destination: Direction) {
    navigate(destination) {
        popUpTo(NavGraphs.root) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
