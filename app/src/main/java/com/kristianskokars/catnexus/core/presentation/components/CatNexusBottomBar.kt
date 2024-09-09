package com.kristianskokars.catnexus.core.presentation.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
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
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.navgraphs.FavouriteNavGraph
import com.ramcosta.composedestinations.generated.navgraphs.HomeNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.DirectionNavGraphSpec
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

fun DestinationsNavigator.navigateToBottomBarDestination(destination: Direction) {
    navigate(destination) {
        popUpTo(NavGraphs.main) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

enum class BottomBarDestination(
    @StringRes val label: Int,
    val icon: @Composable () -> Unit,
    val navGraph: DirectionNavGraphSpec
) {
    HOME(
        label = R.string.home,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = null
            )
        },
        navGraph = HomeNavGraph
    ),
    FAVOURITES(
        label = R.string.favourites,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_favourite),
                contentDescription = null
            )
        },
        navGraph = FavouriteNavGraph
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.CatNexusBottomBar(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    hazeState: HazeState,
    navigator: DestinationsNavigator,
    currentDestination: BottomBarDestination
) {
    with(animatedVisibilityScope) {
        Column(
            modifier = modifier
                .then(
                    if (currentDestination == BottomBarDestination.HOME) Modifier
                        .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                        .animateEnterExit(
                            enter = EnterTransition.None,
                            exit = fadeOut()
                        ) else Modifier
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

                CompositionLocalProvider(LocalRippleConfiguration provides null) {
                    BottomBarDestination.entries.forEach { bottomBarDestination ->
                        NavigationBarItem(
                            colors = bottomBarColors,
                            selected = currentDestination.navGraph() == bottomBarDestination.navGraph,
                            onClick = { navigator.navigateToBottomBarDestination(bottomBarDestination.navGraph) },
                            icon = bottomBarDestination.icon,
                            label = { Text(text = stringResource(id = bottomBarDestination.label), fontFamily = Inter) }
                        )
                    }
                }
            }
        }
    }
}
