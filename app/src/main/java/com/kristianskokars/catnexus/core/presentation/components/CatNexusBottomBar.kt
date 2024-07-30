package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Inter
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

enum class BottomBarDestination {
    HOME, FAVOURITES
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CatNexusBottomBar(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    hazeState: HazeState,
    currentDestination: BottomBarDestination,
    onHomeClick: () -> Unit,
    onFavouritesClick: () -> Unit,
) {
    with(animatedVisibilityScope) {
        Column(
            modifier = modifier
                .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                .animateEnterExit(
                    enter = fadeIn() + slideInVertically {
                        it
                    },
                    exit = fadeOut() + slideOutVertically {
                        it
                    }
                )
        ) {
            CatNexusDivider()
            NavigationBar(
                modifier = Modifier.hazeChild(hazeState),
                containerColor = Black,
                tonalElevation = 0.dp
            ) {
                val bottomBarColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Orange,
                    selectedTextColor = Orange,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                )

                CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                    NavigationBarItem(
                        colors = bottomBarColors,
                        selected = currentDestination == BottomBarDestination.HOME,
                        onClick = onHomeClick,
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_home),
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(R.string.home), fontFamily = Inter) }
                    )
                    NavigationBarItem(
                        colors = bottomBarColors,
                        selected = currentDestination == BottomBarDestination.FAVOURITES,
                        onClick = onFavouritesClick,
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_favourite),
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(R.string.favourites), fontFamily = Inter) }
                    )
                }
            }
        }
    }
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}
