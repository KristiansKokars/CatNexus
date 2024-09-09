package com.kristianskokars.catnexus.feature.cat_list.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import coil.ImageLoader
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.components.BottomBarDestination
import com.kristianskokars.catnexus.core.presentation.components.CatGrid
import com.kristianskokars.catnexus.core.presentation.components.CatNexusBottomBar
import com.kristianskokars.catnexus.core.presentation.components.CatNexusDefaultTopBar
import com.kristianskokars.catnexus.core.presentation.components.ErrorGettingCats
import com.kristianskokars.catnexus.core.presentation.components.LoadingCats
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.scrollToReturnedItemIndex
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.nav.HomeGraph
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.CatDetailsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.FavouritesScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.utils.destination
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

object HomeTransitions : DestinationStyle.Animated() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        when (initialState.destination()) {
            FavouritesScreenDestination -> EnterTransition.None
            SettingsScreenDestination -> slideInHorizontally(initialOffsetX = { it })
            else -> fadeIn()
        }
    }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        when (initialState.destination()) {
            FavouritesScreenDestination -> EnterTransition.None
            SettingsScreenDestination -> slideInHorizontally(initialOffsetX = { -it })
            else -> fadeIn()
        }
    }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        when (targetState.destination()) {
            FavouritesScreenDestination -> ExitTransition.None
            SettingsScreenDestination -> slideOutHorizontally(targetOffsetX = { -it })
            else -> fadeOut()
        }
    }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        when (targetState.destination()) {
            FavouritesScreenDestination -> ExitTransition.None
            SettingsScreenDestination -> slideOutHorizontally(targetOffsetX = { it })
            else -> fadeOut()
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Destination<HomeGraph>(start = true, style = HomeTransitions::class)
@Composable
fun SharedTransitionScope.CatListScreen(
    viewModel: CatListViewModel = hiltViewModel(),
    imageLoader: ImageLoader,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<CatDetailsScreenDestination, Int>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isInCarMode by viewModel.isInCarMode.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()

    resultRecipient.scrollToReturnedItemIndex(lazyGridState = lazyGridState)

    CatListContent(
        state = state,
        imageLoader = imageLoader,
        lazyGridState = lazyGridState,
        animatedVisibilityScope = animatedVisibilityScope,
        navigator = navigator,
        isInCarMode = isInCarMode,
        onFetchMoreCats = viewModel::fetchCats,
        onRetry = viewModel::retryFetch,
        onCatNexusLogoClick = viewModel::onCatNexusLogoClick,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CatListContent(
    state: CatListState,
    imageLoader: ImageLoader,
    animatedVisibilityScope: AnimatedVisibilityScope,
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
                navigator = navigator,
                animatedVisibilityScope = animatedVisibilityScope,
            )
        },
        bottomBar = {
           CatNexusBottomBar(
               hazeState = hazeState,
               animatedVisibilityScope = animatedVisibilityScope,
               navigator = navigator,
               currentDestination = BottomBarDestination.HOME,
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
                    animatedVisibilityScope = animatedVisibilityScope,
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
                    imageLoader = imageLoader
                )
                is CatListState.Error -> ErrorGettingCats(onRetry)
                CatListState.Loading -> LoadingCats()
                CatListState.NoCats -> Text(text = stringResource(R.string.no_cats_found))
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CatListContentPreview() {
//    BackgroundSurface {
//        SharedTransitionLayout {
//            AnimatedVisibility(visible = true) {
//                CatListContent(
//                    state = CatListState.Loading,
//                    animatedVisibilityScope = this,
//                    isInCarMode = false,
//                    lazyGridState = rememberLazyGridState(),
//                    navigator = EmptyDestinationsNavigator,
//                    onFetchMoreCats = {},
//                    onRetry = {},
//                    onCatNexusLogoClick = {},
//                    imageLoader = ImageLoader(LocalContext.current)
//                )
//            }
//        }
//    }
}
