package com.kristianskokars.catnexus.feature.favourites.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.BottomBarDestination
import com.kristianskokars.catnexus.core.presentation.components.CatNexusBottomBar
import com.kristianskokars.catnexus.core.presentation.components.CatNexusDefaultTopBar
import com.kristianskokars.catnexus.core.presentation.components.LoadingCats
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Inter
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.feature.appDestination
import com.kristianskokars.catnexus.feature.cat_list.presentation.components.CatGrid
import com.kristianskokars.catnexus.feature.destinations.CatDetailsScreenDestination
import com.kristianskokars.catnexus.feature.destinations.CatListScreenDestination
import com.kristianskokars.catnexus.lib.navigateToBottomBarDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

object FavouritesTransitions : DestinationStyle.Animated {
    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            CatListScreenDestination -> EnterTransition.None
            else -> null
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            CatListScreenDestination -> EnterTransition.None
            else -> null
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            CatListScreenDestination -> ExitTransition.None
            else -> null
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            CatListScreenDestination -> ExitTransition.None
            else -> null
        }
    }
}

@Destination(style = FavouritesTransitions::class)
@Composable
fun FavouritesScreen(
    viewModel: FavouritesViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(
        navigator = navigator,
        state = state
    )
}

@Composable
private fun Content(
    navigator: DestinationsNavigator,
    state: FavouritesState,
) {
    val hazeState = remember { HazeState() }
    val lazyGridState = rememberLazyGridState()
    val noFavouriteString = buildAnnotatedString {
        var start = 0
        var end = 0

        withStyle(style = SpanStyle(color = White)) {
            append("No favourites added yet. Head to")
        }
        withStyle(style = SpanStyle(color = Orange)) {
            start = length
            append(" home ")
            end = length
        }
        withStyle(style = SpanStyle(color = White)) {
            append("and add some!")
        }

        addStringAnnotation("navigate", "home", start, end)
    }

    Scaffold(
        topBar = {
            CatNexusDefaultTopBar(
                hazeState = hazeState,
                isBorderVisible = lazyGridState.canScrollBackward
            )
        },
        bottomBar = {
            CatNexusBottomBar(
                hazeState = hazeState,
                currentDestination = BottomBarDestination.FAVOURITES,
                onHomeClick = { navigator.navigateToBottomBarDestination(CatListScreenDestination) },
                onFavouritesClick = { /* IGNORED */ }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            if (state.cats == null) {
                LoadingCats()
            } else if (state.cats.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ClickableText(
                        modifier = Modifier.padding(top = padding.calculateTopPadding()).padding(48.dp),
                        text = noFavouriteString,
                        style = TextStyle.Default.copy(textAlign = TextAlign.Center, fontFamily = Inter),
                        onClick = { offset ->
                            noFavouriteString
                                .getStringAnnotations("navigate", offset, offset)
                                .firstOrNull()?.item ?: return@ClickableText

                            navigator.navigateToBottomBarDestination(CatListScreenDestination)
                        }
                    )
                }
            } else {
                CatGrid(
                    modifier = Modifier
                        .haze(
                            state = hazeState,
                            style = HazeStyle(tint = Black.copy(alpha = 0.72f), blurRadius = 24.dp)
                        ),
                    topContentPadding = PaddingValues(top = padding.calculateTopPadding()),
                    state = lazyGridState,
                    cats = state.cats,
                    onCatClick = { navigator.navigate(CatDetailsScreenDestination(it)) },
                )
            }

        }
    }
}

@Preview
@Composable
private fun Preview() {
    BackgroundSurface {
        Content(
            navigator = EmptyDestinationsNavigator,
            state = FavouritesState()
        )
    }
}
