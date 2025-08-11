package com.kristianskokars.catnexus.feature.favourites.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
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
import coil.ImageLoader
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.BottomBarDestination
import com.kristianskokars.catnexus.core.presentation.components.CatGrid
import com.kristianskokars.catnexus.core.presentation.components.CatNexusBottomBar
import com.kristianskokars.catnexus.core.presentation.components.CatNexusDefaultTopBar
import com.kristianskokars.catnexus.core.presentation.components.LoadingCats
import com.kristianskokars.catnexus.core.presentation.scrollToReturnedItemIndex
import com.kristianskokars.catnexus.core.presentation.theme.Inter
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.feature.cat_detail.presentation.CatDetailsScreenNavArgs
import com.kristianskokars.catnexus.lib.navigateToBottomBarDestination
import com.kristianskokars.catnexus.nav.FavouriteGraph
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.CatDetailsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CatListScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.utils.destination
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

object FavouritesTransitions : DestinationStyle.Animated() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        when (initialState.destination()) {
            CatListScreenDestination -> EnterTransition.None
            else -> null
        }
    }

    override val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        when (initialState.destination()) {
            CatListScreenDestination -> EnterTransition.None
            else -> null
        }
    }

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        when (initialState.destination()) {
            CatListScreenDestination -> ExitTransition.None
            else -> null
        }
    }

    override val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        when (initialState.destination()) {
            CatListScreenDestination -> ExitTransition.None
            else -> null
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Destination<FavouriteGraph>(start = true, style = FavouritesTransitions::class)
@Composable
fun SharedTransitionScope.FavouritesScreen(
    viewModel: FavouritesViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<CatDetailsScreenDestination, Int>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isInCarMode by viewModel.isInCarMode.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()

    resultRecipient.scrollToReturnedItemIndex(lazyGridState = lazyGridState)

    Content(
        navigator = navigator,
        animatedVisibilityScope = animatedVisibilityScope,
        lazyGridState = lazyGridState,
        state = state,
        isInCarMode = isInCarMode,
        onCatNexusLogoClick = viewModel::onCatNexusLogoClick
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.Content(
    navigator: DestinationsNavigator,
    animatedVisibilityScope: AnimatedVisibilityScope,
    lazyGridState: LazyGridState,
    state: FavouritesState,
    isInCarMode: Boolean,
    onCatNexusLogoClick: () -> Unit,
) {
    val hazeState = remember { HazeState() }
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
                isBorderVisible = lazyGridState.canScrollBackward,
                navigator = navigator,
                isInCarMode = isInCarMode,
                onCatNexusLogoClick = onCatNexusLogoClick,
                animatedVisibilityScope = animatedVisibilityScope
            )
        },
        bottomBar = {
            CatNexusBottomBar(
                navigator = navigator,
                hazeState = hazeState,
                animatedVisibilityScope = animatedVisibilityScope,
                currentDestination = BottomBarDestination.FAVOURITES,
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
                        modifier = Modifier
                            .padding(top = padding.calculateTopPadding())
                            .padding(48.dp),
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
                    modifier = Modifier.hazeSource(state = hazeState),
                    animatedVisibilityScope = animatedVisibilityScope,
                    topContentPadding = PaddingValues(top = padding.calculateTopPadding()),
                    state = lazyGridState,
                    cats = state.cats,
                    onCatClick = {
                        navigator.navigate(
                            CatDetailsScreenDestination(CatDetailsScreenNavArgs(it, showFavourites = true))
                        )
                    },
                    imageLoader = ImageLoader(LocalContext.current)
                )
            }

        }
    }
}

@Preview
@Composable
private fun Preview() {
    BackgroundSurface {
//        Content(
//            navigator = EmptyDestinationsNavigator,
//            lazyGridState = rememberLazyGridState(),
//            state = FavouritesState(),
//            isInCarMode = false,
//            onCatNexusLogoClick = {}
//        )
    }
}
