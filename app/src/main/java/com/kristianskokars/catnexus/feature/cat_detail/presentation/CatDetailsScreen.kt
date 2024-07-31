package com.kristianskokars.catnexus.feature.cat_detail.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.kristianskokars.catnexus.core.domain.model.CatSwipeDirection
import com.kristianskokars.catnexus.core.presentation.DefaultHazeStyle
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.feature.cat_detail.presentation.components.CatDetailsTopBar
import com.kristianskokars.catnexus.feature.cat_detail.presentation.components.CatPictureActionBar
import com.kristianskokars.catnexus.feature.cat_detail.presentation.components.DeleteCatConfirmationDialog
import com.kristianskokars.catnexus.feature.cat_detail.presentation.components.ZoomableCatPicture
import com.kristianskokars.catnexus.nav.HomeGraph
import com.ramcosta.composedestinations.animations.defaults.DefaultFadingTransitions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CatDetailsScreenNavArgs(val catPageIndex: Int, val showFavourites: Boolean = false)

@OptIn(ExperimentalSharedTransitionApi::class)
@Destination<HomeGraph>(
    navArgs = CatDetailsScreenNavArgs::class,
    style = DefaultFadingTransitions::class
)
@Composable
fun SharedTransitionScope.CatDetailsScreen(
    viewModel: CatDetailsViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    imageLoader: ImageLoader,
    navArgsDelegate: CatDetailsScreenNavArgs,
    resultNavigator: ResultBackNavigator<Int>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isDownloadPermissionGranted by remember {
        mutableStateOf(
            isPermissionToSavePicturesGranted(
                context
            )
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(CatDetailsEvent.SaveCat)
        } else {
            isDownloadPermissionGranted = false
        }
    }
    val pagerState = rememberPagerState(initialPage = navArgsDelegate.catPageIndex, pageCount = { state.pageCount })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            viewModel.onEvent(CatDetailsEvent.OnPageSelected(page))
        }
    }

    BackHandler {
        resultNavigator.navigateBack(pagerState.currentPage)
    }

    CatDetailsContent(
        state = state,
        onEvent = viewModel::onEvent,
        animatedVisibilityScope = animatedVisibilityScope,
        pagerState = pagerState,
        resultNavigator = resultNavigator,
        onDownloadClick = {
            askForStoragePermissionIfOnOlderAndroid(context, launcher) {
                viewModel.onEvent(
                    CatDetailsEvent.SaveCat
                )
            }
        },
        imageLoader = imageLoader,
        isDownloadPermissionGranted = isDownloadPermissionGranted,
        onConfirmUnfavourite = {
            scope.launch {
                viewModel.onEvent(CatDetailsEvent.ConfirmUnfavourite)
                pagerState.scrollToPage(pagerState.currentPage - 1)
            }
        },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CatDetailsContent(
    state: CatDetailsState,
    onEvent: (CatDetailsEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    pagerState: PagerState,
    resultNavigator: ResultBackNavigator<Int>,
    imageLoader: ImageLoader,
    isDownloadPermissionGranted: Boolean?,
    onDownloadClick: () -> Unit,
    onConfirmUnfavourite: () -> Unit,
) {
    val hazeState = remember { HazeState() }
    val pictureHazeState = remember { HazeState() }
    val configuration = LocalConfiguration.current
    val isInLandscape by remember {
        derivedStateOf {
            configuration.screenWidthDp > configuration.screenHeightDp
        }
    }
    var zoomFactor by remember { mutableFloatStateOf(1f) }

    if (state.cats.getOrNull(pagerState.currentPage) == null || state.cats.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
        }
        return
    }

    if (state.isUnfavouritingSavedCatConfirmation) {
        DeleteCatConfirmationDialog(
            pictureHazeState = pictureHazeState,
            onEvent = onEvent,
            onConfirmUnfavourite = onConfirmUnfavourite
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CatDetailsTopBar(
                hazeState = hazeState,
                zoomFactor = zoomFactor,
                resultNavigator = resultNavigator,
                pagerState = pagerState,
                state = state,
                onEvent = onEvent
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .haze(state = hazeState, style = DefaultHazeStyle)
                .then(if (isInLandscape) Modifier.padding(padding) else Modifier.padding(bottom = padding.calculateBottomPadding()))
                .fillMaxSize(),
        ) {
            when (state.swipeDirection) {
                CatSwipeDirection.VERTICAL -> VerticalPager(
                    state = pagerState,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                    )
                ) { index ->
                    ZoomableCatPicture(
                        cats = state.cats,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onZoomFactorChange = { zoomFactor = it },
                        index = index,
                        pictureHazeState = pictureHazeState,
                        imageLoader = imageLoader
                    )
                }

                CatSwipeDirection.HORIZONTAL -> HorizontalPager(
                    state = pagerState,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                    )
                ) { index ->
                    ZoomableCatPicture(
                        cats = state.cats,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onZoomFactorChange = { zoomFactor = it },
                        index = index,
                        pictureHazeState = pictureHazeState,
                        imageLoader = imageLoader
                    )
                }
            }
            CatPictureActionBar(
                cat = state.cats[pagerState.currentPage],
                isCatDownloading = state.isCurrentCatDownloading,
                isDownloadPermissionGranted = isDownloadPermissionGranted,
                pictureHazeState = pictureHazeState,
                onFavouriteClick = { onEvent(CatDetailsEvent.ToggleFavouriteCat) },
                onDownloadClick = onDownloadClick,
                onShareCat = { onEvent(CatDetailsEvent.ShareCat) }
            )
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun CatDetailsScreenPreview() {
    val context = LocalContext.current

    BackgroundSurface {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CatDetailsContent(
                    state = CatDetailsState(),
                    onEvent = {},
                    animatedVisibilityScope = this,
                    pagerState = rememberPagerState { 1 },
                    resultNavigator = EmptyResultBackNavigator(),
                    imageLoader = ImageLoader.Builder(context).build(),
                    isDownloadPermissionGranted = null,
                    onDownloadClick = {},
                    onConfirmUnfavourite = {},
                )
            }
        }
    }
}
