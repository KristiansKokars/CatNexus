package com.kristianskokars.catnexus.feature.cat_list.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import coil.ImageLoader
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.presentation.DefaultHazeStyle
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.BottomBarDestination
import com.kristianskokars.catnexus.core.presentation.components.CatGrid
import com.kristianskokars.catnexus.core.presentation.components.CatNexusBottomBar
import com.kristianskokars.catnexus.core.presentation.components.CatNexusTopBarLayout
import com.kristianskokars.catnexus.core.presentation.components.ErrorGettingCats
import com.kristianskokars.catnexus.core.presentation.components.LoadingCats
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.scrollToReturnedItemIndex
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Gray
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.feature.appDestination
import com.kristianskokars.catnexus.feature.cat_detail.presentation.ActionBar
import com.kristianskokars.catnexus.feature.cat_detail.presentation.CatDetailsViewModel
import com.kristianskokars.catnexus.feature.cat_detail.presentation.IconButton
import com.kristianskokars.catnexus.feature.cat_detail.presentation.isPermissionToSavePicturesGranted
import com.kristianskokars.catnexus.feature.destinations.CatDetailsScreenDestination
import com.kristianskokars.catnexus.feature.destinations.FavouritesScreenDestination
import com.kristianskokars.catnexus.lib.navigateToBottomBarDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.skydoves.orbital.Orbital
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

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

data class SharedElementData(
    val index: Int,
    val cat: Cat,
    val catPicture: @Composable () -> Unit
)

data class CatTransitionData(
    val cat: Cat? = null,
    val isTransitioning: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Destination(style = HomeTransitions::class)
@RootNavGraph(start = true)
@Composable
fun CatListScreen(
    viewModel: CatListViewModel = hiltViewModel(),
    viewModel2: CatDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    imageLoader: ImageLoader,
    resultRecipient: ResultRecipient<CatDetailsScreenDestination, Int>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()
    var screen by remember { mutableStateOf<CatScreen>(CatScreen.List()) }

    resultRecipient.scrollToReturnedItemIndex(lazyGridState = lazyGridState)

    val context = LocalContext.current
    val cats by viewModel2.cats.collectAsStateWithLifecycle()
    val pageCount by viewModel2.pageCount.collectAsStateWithLifecycle()
    val isCatDownloading by viewModel2.isCatDownloading.collectAsStateWithLifecycle(initialValue = false)
    val isUnfavouritingSavedCatConfirmation by viewModel2.isUnfavouritingSavedCatConfirmation.collectAsStateWithLifecycle()
    var isDownloadPermissionGranted by remember { mutableStateOf(isPermissionToSavePicturesGranted(context)) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel2.saveCat()
        } else {
            isDownloadPermissionGranted = false
        }
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            viewModel2.onPageSelected(page)
        }
    }

    var currentCat by remember { mutableStateOf(CatTransitionData()) }
    var sharedElementParams by remember { mutableStateOf<SharedElementData?>(null) }


    Orbital {
        CompositionLocalProvider(LocalCurrentCat provides currentCat) {
            when (val currentScreen = screen) {
                is CatScreen.List -> {
                    CatListContent(
                        state = state,
                        lazyGridState = lazyGridState,
                        navigator = navigator,
                        currentScreen = currentScreen,
                        onFetchMoreCats = viewModel::fetchCats,
                        onRetry = viewModel::retryFetch,
                        onCatClick = { index, cat, catPicture ->
                            scope.launch {
                                sharedElementParams = SharedElementData(index, cat, catPicture)
                                screen = CatScreen.Details(cat, catPicture)
                                delay(50)
                                currentCat = CatTransitionData(currentCat.cat, true)
                            }
                        },
                        currentCat = currentCat.cat,
                        onBeginTransition = { index, cat ->
                            currentCat = CatTransitionData(cat, false)
                        }
                    )
                }
                is CatScreen.Details -> CatDetailsContent(
                    cat = currentScreen.cat,
                    sharedContent = currentScreen.catCard,
                    navigateToDetails = { catId, catPicture ->
                        scope.launch {
                            screen = CatScreen.List(catId, catPicture)
                            currentCat = CatTransitionData(currentCat.cat, true)
                            delay(210)
                            currentCat = CatTransitionData(null, false)
                        }
                    },
                    imageLoader = imageLoader,
                    cats = cats,
                    pagerState = pagerState,
                    isCatDownloading = isCatDownloading,
                    isUnfavouritingSavedCatConfirmation = isUnfavouritingSavedCatConfirmation,
                    isDownloadPermissionGranted = isDownloadPermissionGranted,
                    onDismissDeleteConfirmation = {},
                    onShareCat = {},
                    onFavouriteClick = {},
                    onDownloadClick = {},
                    onConfirmUnfavourite = {},
                )
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CatDetailsContent(
    cat: Cat,
    sharedContent: @Composable () -> Unit,
    navigateToDetails: (catId: String, cat: @Composable () -> Unit) -> Unit,
    cats: List<Cat>,
    pagerState: PagerState,
    isCatDownloading: Boolean,
    imageLoader: ImageLoader,
    isUnfavouritingSavedCatConfirmation: Boolean,
    isDownloadPermissionGranted: Boolean?,
    onDownloadClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onShareCat: () -> Unit,
    onDismissDeleteConfirmation: () -> Unit,
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

    if (cats.getOrNull(pagerState.currentPage) == null || cats.isEmpty()) {
        return
    }

    BackHandler {
        navigateToDetails(cat.id, sharedContent)
    }

    Scaffold(
        topBar = {
            CatNexusTopBarLayout(hazeState = hazeState, isBorderVisible = zoomFactor != 1f) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {  },
                        rippleRadius = 24.dp,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .haze(
                    state = hazeState,
                    style = DefaultHazeStyle
                )
                .then(if (isInLandscape) Modifier.padding(padding) else Modifier.padding(bottom = padding.calculateBottomPadding()))
                .fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                )
            ) { index ->
                val zoomState = rememberZoomState()

                LaunchedEffect(key1 = zoomState.scale) {
                    zoomFactor = zoomState.scale
                }

                Box(
                    modifier = Modifier
                        .haze(state = pictureHazeState, style = DefaultHazeStyle)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .zoomable(zoomState)
                ) {
                    if (isUnfavouritingSavedCatConfirmation) {
                        Dialog(
                            onDismissRequest = onDismissDeleteConfirmation
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 24.dp, horizontal = 36.dp)
                                    .border(
                                        Dp.Hairline,
                                        Gray.copy(alpha = 0.4f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .hazeChild(
                                        pictureHazeState,
                                        shape = RoundedCornerShape(4.dp),
                                        style = HazeStyle(
                                            tint = Black.copy(alpha = 0.55f),
                                            blurRadius = 24.dp
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Text(text = "Are you sure you want to unfavourite this cat? It will no longer be locally stored!")
                                Spacer(modifier = Modifier.padding(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    TextButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onDismissDeleteConfirmation
                                    ) {
                                        Text(text = "Cancel")
                                    }
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            onDismissDeleteConfirmation()
                                            onConfirmUnfavourite()
                                        }
                                    ) {
                                        Text(text = "OK")
                                    }
                                }
                            }

                        }
                    }

                    sharedContent()
//                    AsyncImage(
//                        model = ImageRequest.Builder(LocalContext.current)
//                            .data(cat.url)
//                            .crossfade(true)
//                            .build(),
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                            .fillMaxSize(),
//                        contentScale = ContentScale.Fit,
//                        contentDescription = null,
//                        imageLoader = imageLoader,
//                    )
                }
            }
            ActionBar(
                cat = cats[pagerState.currentPage],
                isCatDownloading = isCatDownloading,
                isDownloadPermissionGranted = isDownloadPermissionGranted,
                pictureHazeState = pictureHazeState,
                onFavouriteClick = onFavouriteClick,
                onDownloadClick = onDownloadClick,
                onShareCat = onShareCat
            )
        }
    }
}

val LocalCurrentCat = compositionLocalOf { CatTransitionData() }

@Composable
private fun CatListContent(
    state: CatListState,
    lazyGridState: LazyGridState,
    currentCat: Cat?,
    currentScreen: CatScreen.List,
    navigator: DestinationsNavigator,
    onBeginTransition: (index: Int, cat: Cat) -> Unit,
    onFetchMoreCats: () -> Unit,
    onCatClick: (index: Int, cat: Cat, catPicture: @Composable () -> Unit) -> Unit,
    onRetry: () -> Unit,
) {
    val hazeState = remember { HazeState() }

    Scaffold(
        topBar = {
            CatNexusTopBarLayout(
                hazeState = hazeState,
                isBorderVisible = lazyGridState.canScrollBackward
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cat_large),
                        contentDescription = null,
                        tint = Orange,
                    )
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(R.string.cat_infinity),
                        fontSize = 24.sp,
                    )
                }
            }
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
                    onCatClick = { index, cat, catPicture ->
                        onCatClick(index, cat, catPicture)
                    },
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
                    sharedContent = { catId, catCard ->
                        if (catId == currentScreen.catId) {
                            currentScreen.cat?.let { it() }
                        } else {
                            catCard()
                        }
                    },
                    onBeginTransition = onBeginTransition,
                    currentCat = currentCat,
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
private fun Preview() {
    BackgroundSurface {
//        CatListContent(
//            state = CatListState.Loading,
//            lazyGridState = rememberLazyGridState(),
//            navigator = EmptyDestinationsNavigator,
//            currentScreen = CatScreen.List(),
//            onFetchMoreCats = {},
//            onRetry = {},
//            onCatClick = { _, _, _ -> },
//            isClicked = false
//        )
    }
}
