package com.kristianskokars.catnexus.feature.cat_list.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.BottomBarDestination
import com.kristianskokars.catnexus.core.presentation.components.CatGrid
import com.kristianskokars.catnexus.core.presentation.components.CatNexusBottomBar
import com.kristianskokars.catnexus.core.presentation.components.CatNexusDefaultTopBar
import com.kristianskokars.catnexus.core.presentation.components.ErrorGettingCats
import com.kristianskokars.catnexus.core.presentation.components.LoadingCats
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.scrollToReturnedItemIndex
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.feature.appDestination
import com.kristianskokars.catnexus.feature.destinations.CatDetailsScreenDestination
import com.kristianskokars.catnexus.feature.destinations.FavouritesScreenDestination
import com.kristianskokars.catnexus.lib.navigateToBottomBarDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.spec.DestinationStyle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

object HomeTransitions : DestinationStyle.Animated {
    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            FavouritesScreenDestination -> EnterTransition.None
            else -> null
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            FavouritesScreenDestination -> EnterTransition.None
            else -> null
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            FavouritesScreenDestination -> ExitTransition.None
            else -> null
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            FavouritesScreenDestination -> ExitTransition.None
            else -> null
        }
    }
}

@Destination(style = HomeTransitions::class)
@RootNavGraph(start = true)
@Composable
fun CatListScreen(
    viewModel: CatListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<CatDetailsScreenDestination, Int>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isInCarMode by viewModel.isInCarMode.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()

    resultRecipient.scrollToReturnedItemIndex(lazyGridState = lazyGridState)

    CatListContent(
        state = state,
        lazyGridState = lazyGridState,
        navigator = navigator,
        isInCarMode = isInCarMode,
        onFetchMoreCats = viewModel::fetchCats,
        onRetry = viewModel::retryFetch,
        onCatNexusLogoClick = viewModel::onCatNexusLogoClick
    )
}

@Composable
private fun CatListContent(
    state: CatListState,
    lazyGridState: LazyGridState,
    navigator: DestinationsNavigator,
    isInCarMode: Boolean,
    onFetchMoreCats: () -> Unit,
    onRetry: () -> Unit,
    onCatNexusLogoClick: () -> Unit,
) {
    val hazeState = remember { HazeState() }

    Scaffold(
        topBar = {
            CatNexusDefaultTopBar(
                hazeState = hazeState,
                isInCarMode = isInCarMode,
                isBorderVisible = lazyGridState.canScrollBackward,
                onCatNexusLogoClick = onCatNexusLogoClick,
                navigator = navigator
            )
        },
        bottomBar = {
           CatNexusBottomBar(
               hazeState = hazeState,
               currentDestination = BottomBarDestination.HOME,
               onHomeClick = { /* Ignored */ },
               onFavouritesClick = { navigator.navigateToBottomBarDestination(FavouritesScreenDestination) }
           )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            when (state) {
                is CatListState.Loaded -> CatGrid(
                    modifier = Modifier
                        .haze(
                            state = hazeState,
                            style = HazeStyle(tint = Black.copy(alpha = 0.72f), blurRadius = 24.dp)
                        ),
                    topContentPadding = PaddingValues(top = padding.calculateTopPadding()),
                    state = lazyGridState,
                    cats = state.cats,
                    onCatClick = { navigator.navigate(CatDetailsScreenDestination(it)) },
                    bottomSlot = {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.hasError != null) {
                                ErrorGettingCats(onRetry)
                            } else {
                                LoadingSpinner()
                            }
                        }
                    },
                    onScrolledToBottom = onFetchMoreCats,
                )
                is CatListState.Error -> ErrorGettingCats(onRetry)
                CatListState.Loading -> LoadingCats()
                CatListState.NoCats -> Text(text = stringResource(R.string.no_cats_found))
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CatListContentPreview() {
    BackgroundSurface {
        CatListContent(
            state = CatListState.Loading,
            isInCarMode = false,
            lazyGridState = rememberLazyGridState(),
            navigator = EmptyDestinationsNavigator,
            onFetchMoreCats = {},
            onRetry = {},
            onCatNexusLogoClick = {}
        )
    }
}
