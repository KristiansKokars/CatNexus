package com.kristianskokars.catnexus.feature.cat_detail.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.presentation.DefaultHazeStyle
import com.kristianskokars.catnexus.core.presentation.ElevatedHazeStyle
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.CatNexusTopBarLayout
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.theme.Gray
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.core.presentation.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.flow.collectLatest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

data class CatDetailsScreenNavArgs(val catPageIndex: Int, val showFavourites: Boolean = false)

@OptIn(ExperimentalFoundationApi::class)
@Destination(navArgsDelegate = CatDetailsScreenNavArgs::class)
@Composable
fun CatDetailsScreen(
    viewModel: CatDetailsViewModel = hiltViewModel(),
    imageLoader: ImageLoader,
    navArgsDelegate: CatDetailsScreenNavArgs,
    resultNavigator: ResultBackNavigator<Int>
) {
    val context = LocalContext.current
    val cats by viewModel.cats.collectAsStateWithLifecycle()
    val pageCount by viewModel.pageCount.collectAsStateWithLifecycle()
    val isCatDownloading by viewModel.isCatDownloading.collectAsStateWithLifecycle(initialValue = false)
    var isDownloadPermissionGranted by remember { mutableStateOf(isPermissionToSavePicturesGranted(context)) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.saveCat()
        } else {
            isDownloadPermissionGranted = false
        }
    }
    val pagerState = rememberPagerState(initialPage = navArgsDelegate.catPageIndex, pageCount = { pageCount })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            viewModel.onPageSelected(page)
        }
    }

    BackHandler {
        resultNavigator.navigateBack(pagerState.currentPage)
    }

    CatDetailsContent(
        cats = cats,
        pagerState = pagerState,
        isCatDownloading = isCatDownloading,
        resultNavigator = resultNavigator,
        onDownloadClick = { askForStoragePermissionIfOnOlderAndroid(context, launcher, viewModel::saveCat) },
        imageLoader = imageLoader,
        isDownloadPermissionGranted = isDownloadPermissionGranted,
        onFavouriteClick = viewModel::toggleFavouriteCat,
        onShareCat = viewModel::shareCat
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatDetailsContent(
    cats: List<Cat>,
    pagerState: PagerState,
    isCatDownloading: Boolean,
    resultNavigator: ResultBackNavigator<Int>,
    imageLoader: ImageLoader,
    isDownloadPermissionGranted: Boolean?,
    onDownloadClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onShareCat: () -> Unit,
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

    Scaffold(
        topBar = {
            CatNexusTopBarLayout(hazeState = hazeState, isBorderVisible = zoomFactor != 1f) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { resultNavigator.navigateBack(pagerState.currentPage) },
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
                    val cat = cats[index]

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(cat.url)
                            .crossfade(true)
                            .build(),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        imageLoader = imageLoader,
                    )
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

@Composable
private fun BoxScope.ActionBar(
    cat: Cat,
    isCatDownloading: Boolean,
    isDownloadPermissionGranted: Boolean?,
    pictureHazeState: HazeState,
    onFavouriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onShareCat: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 36.dp)
            .border(Dp.Hairline, Gray.copy(alpha = 0.4f), CircleShape)
            .hazeChild(pictureHazeState, shape = CircleShape, style = ElevatedHazeStyle)
            .padding(8.dp)
            .align(Alignment.BottomCenter),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onFavouriteClick) {
            if (cat.isFavourited) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favourite_filled),
                    tint = Orange,
                    contentDescription = stringResource(R.string.unfavourite_cat),
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favourite),
                    contentDescription = stringResource(R.string.favourite_cat)
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        if (isCatDownloading) {
            LoadingSpinner(modifier = Modifier
                .padding(12.dp)
                .size(24.dp))
        } else {
            IconButton(
                onClick = {
                    if (isDownloadPermissionGranted == false) {
                        Toast.makeText(context, R.string.ask_for_storage_permission, Toast.LENGTH_SHORT).show()
                    } else {
                        onDownloadClick()
                    }
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = stringResource(R.string.save_cat),
                    tint = if (isDownloadPermissionGranted == false) Red else Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        IconButton(
            onClick = onShareCat,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = stringResource(R.string.share_cat),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun IconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    rippleRadius: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = rippleRadius),
            )
            .minimumInteractiveComponentSize(),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun CatDetailsScreenPreview() {
    val context = LocalContext.current

    BackgroundSurface {
        CatDetailsContent(
            cats = emptyList(),
            isCatDownloading = true,
            pagerState = rememberPagerState { 1 },
            resultNavigator = EmptyResultBackNavigator(),
            imageLoader = ImageLoader.Builder(context).build(),
            isDownloadPermissionGranted = null,
            onDownloadClick = {},
            onFavouriteClick = {},
            onShareCat = {}
        )
    }
}
