package com.kristianskokars.catnexus.feature.cat_detail.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.model.PictureDoubleTapFunctionality
import com.kristianskokars.catnexus.core.presentation.DefaultHazeStyle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ZoomableCatPicture(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    cats: List<Cat>,
    onZoomFactorChange: (Float) -> Unit,
    index: Int,
    pictureHazeState: HazeState,
    imageLoader: ImageLoader,
    pictureDoubleTapFunctionality: PictureDoubleTapFunctionality,
    onFavouriteDoubleTap: () -> Unit

) {
    val zoomState = rememberZoomState()

    LaunchedEffect(key1 = zoomState.scale) {
        onZoomFactorChange(zoomState.scale)
    }

    Box(
        modifier = modifier
            .haze(state = pictureHazeState, style = DefaultHazeStyle)
            .fillMaxWidth()
            .fillMaxHeight()
            .zoomable(
                zoomState,
                onDoubleTap = { position ->
                    when (pictureDoubleTapFunctionality) {
                        PictureDoubleTapFunctionality.FAVORITE -> onFavouriteDoubleTap()
                        PictureDoubleTapFunctionality.ZOOM -> zoomState.toggleScale(2.5f, position)
                    }
                }
            )
    ) {
        val cat = remember(cats, index) { cats[index] }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cat.url)
                .placeholderMemoryCacheKey("cat-${cat.id}")
                .memoryCacheKey("cat-${cat.id}")
                .build(),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "cat-${cat.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            contentScale = ContentScale.Fit,
            contentDescription = null,
            imageLoader = imageLoader,
        )
    }
}
