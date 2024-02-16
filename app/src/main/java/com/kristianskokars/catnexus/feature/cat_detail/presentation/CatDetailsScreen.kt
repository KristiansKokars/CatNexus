package com.kristianskokars.catnexus.feature.cat_detail.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.CatNexusTopBarLayout
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.components.ZoomableBox
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.core.presentation.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

data class CatDetailsScreenNavArgs(val cat: Cat)

@Destination(navArgsDelegate = CatDetailsScreenNavArgs::class)
@Composable
fun CatDetailsScreen(
    viewModel: CatDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    imageLoader: ImageLoader,
) {
    val context = LocalContext.current
    val cat by viewModel.cat.collectAsStateWithLifecycle()
    val isCatDownloading by viewModel.isCatDownloading.collectAsStateWithLifecycle(initialValue = false)
    var isDownloadPermissionGranted by remember {
        mutableStateOf(isPermissionToSavePicturesGranted(context))
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.saveCat()
        } else {
            isDownloadPermissionGranted = false
        }
    }

    CatDetailsContent(
        cat = cat,
        isCatDownloading = isCatDownloading,
        navigator = navigator,
        onDownloadClick = { askForStoragePermissionIfOnOlderAndroid(context, launcher, viewModel::saveCat) },
        imageLoader = imageLoader,
        isDownloadPermissionGranted = isDownloadPermissionGranted,
        onFavouriteClick = viewModel::toggleFavouriteCat,
        onShareCat = viewModel::shareCat
    )
}

@Composable
fun CatDetailsContent(
    cat: Cat,
    isCatDownloading: Boolean,
    navigator: DestinationsNavigator,
    imageLoader: ImageLoader,
    isDownloadPermissionGranted: Boolean?,
    onDownloadClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onShareCat: () -> Unit,
) {
    val hazeState = remember { HazeState() }
    val pictureHazeState = remember { HazeState() }
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    var zoomScale by remember { mutableFloatStateOf(1f) }
    val isInLandscape by remember {
        derivedStateOf {
            configuration.screenWidthDp > configuration.screenHeightDp
        }
    }

    Scaffold(
        topBar = {
            CatNexusTopBarLayout(hazeState = hazeState, isBorderVisible = zoomScale != 1f) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = navigator::navigateUp,
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
            ZoomableBox(
                modifier = Modifier
                    .haze(state = pictureHazeState, style = DefaultHazeStyle)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                onScaleChange = { zoomScale = it }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cat.url)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .pointerInput(null) {
                            detectTapGestures(onDoubleTap = { position ->
                                zoomInOrOut(position)
                            })
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    imageLoader = imageLoader,
                )
            }
            Row(
                modifier = Modifier
                    .padding(vertical = 24.dp, horizontal = 36.dp)
                    .border(Dp.Hairline, Color.Gray.copy(alpha = 0.25f), CircleShape)
                    .hazeChild(pictureHazeState, shape = CircleShape)
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
                    LoadingSpinner(modifier = Modifier.padding(12.dp).size(24.dp))
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
    }

}

@Composable
private fun IconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    rippleRadius: Dp = 36.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = rippleRadius),
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Preview
@Composable
private fun CatDetailsScreenPreview() {
    val context = LocalContext.current

    BackgroundSurface {
        CatDetailsContent(
            cat = Cat(id = "cat", url = "cat", name = "cat", fetchedDateInMillis = 0),
            isCatDownloading = true,
            navigator = EmptyDestinationsNavigator,
            imageLoader = ImageLoader.Builder(context).build(),
            isDownloadPermissionGranted = null,
            onDownloadClick = {},
            onFavouriteClick = {},
            onShareCat = {}
        )
    }
}
