package com.kristianskokars.catnexus.feature.cat_detail.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.CatNexusTopBar
import com.kristianskokars.catnexus.core.presentation.components.ZoomableBox
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.core.presentation.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

data class CatDetailsScreenNavArgs(val cat: Cat)

@Destination(navArgsDelegate = CatDetailsScreenNavArgs::class)
@Composable
fun CatDetailsScreen(
    viewModel: CatDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    imageLoader: ImageLoader,
) {
    val context = LocalContext.current
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
        cat = viewModel.cat,
        navigator = navigator,
        onDownloadClick = { askForStoragePermissionIfOnOlderAndroid(context, launcher, viewModel::saveCat) },
        imageLoader = imageLoader,
        isDownloadPermissionGranted = isDownloadPermissionGranted,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // we do not want the padding due to the navbar
@Composable
fun CatDetailsContent(
    cat: Cat,
    navigator: DestinationsNavigator,
    imageLoader: ImageLoader,
    isDownloadPermissionGranted: Boolean?,
    onDownloadClick: () -> Unit
) {
    val hazeState = remember { HazeState() }
    var zoomScale by remember { mutableFloatStateOf(1f) }

    Scaffold(
        topBar = {
            CatNexusTopBar(hazeState = hazeState, isBorderVisible = zoomScale != 1f) {
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
                    Row(
                        modifier = Modifier.padding(end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "${(zoomScale * 100).roundToDecimalPlaces(2)}%", fontSize = 16.sp)
                        Spacer(modifier = Modifier.size(4.dp))
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = R.drawable.ic_magnifying_glass),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.haze(
                state = hazeState,
                style = HazeStyle(tint = Black.copy(alpha = 0.72f), blurRadius = 24.dp)
            ).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ZoomableBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onScaleChange = { zoomScale = it }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cat.url)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
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
                    .padding(vertical = 8.dp)
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                if (isDownloadPermissionGranted == false) {
                    Text(
                        text = stringResource(R.string.ask_for_storage_permission),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Red
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DownloadButton(onDownloadClick = onDownloadClick)
                    }
                }
            }
        }
    }

}

@Composable
private fun DownloadButton(onDownloadClick: () -> Unit) {
    IconButton(
        modifier = Modifier.padding(vertical = 16.dp),
        onClick = onDownloadClick,
        rippleRadius = 40.dp,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_download),
            contentDescription = stringResource(R.string.save_cat),
            tint = Orange,
        )
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

private fun Float.roundToDecimalPlaces(places: Int) = ((this * 10 * places).toInt() / (10 * places))

@Preview
@Composable
private fun CatDetailsScreenPreview() {
    val context = LocalContext.current

    BackgroundSurface {
        CatDetailsContent(
            cat = Cat(id = "cat", url = "cat", name = "cat", fetchedDateInMillis = 0),
            navigator = EmptyDestinationsNavigator,
            imageLoader = ImageLoader.Builder(context).build(),
            isDownloadPermissionGranted = null
        ) {}
    }
}
