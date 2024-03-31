package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.presentation.theme.DarkGray
import com.kristianskokars.catnexus.feature.cat_list.presentation.LocalCurrentCat
import com.skydoves.orbital.animateBounds
import com.skydoves.orbital.rememberMovableContentOf
import timber.log.Timber

@Composable
fun CatCard(
    modifier: Modifier = Modifier,
    cat: Cat,
    index: Int,
    onCatClick: (index: Int, cat: Cat, catPicture: @Composable () -> Unit) -> Unit,
    onBeginTransition: (index: Int, cat: Cat) -> Unit,
    sharedContent: @Composable (catId: String?, @Composable () -> Unit) -> Unit,
) {

    val image = rememberMovableContentOf {
        val currentCat = LocalCurrentCat.current

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (currentCat.cat == cat) Modifier.animateBounds(
                        modifier = Modifier,
                        sizeAnimationSpec = tween(200),
                        positionAnimationSpec = tween(200),
                        debug = true
                    ) else Modifier.clip(RoundedCornerShape(12.dp))
                        .background(DarkGray)
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(cat.url)
                .crossfade(true)
                .build(),
            contentScale = if (currentCat.cat == cat && currentCat.isTransitioning) ContentScale.Fit else ContentScale.FillBounds,
            contentDescription = "Picture of Cat ${cat.name ?: "with ID of ${cat.id}"}",
        )
    }

    val currentCat = LocalCurrentCat.current
    LaunchedEffect(key1 = currentCat) {
        Timber.d("$currentCat")
        if (!currentCat.isTransitioning && currentCat.cat == cat) {
            onCatClick(index, cat, image)
        }
    }

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

    Row(
        modifier = modifier
            .size(124.dp)
            .padding(8.dp)
            .clickable {
                onBeginTransition(index, cat)
            },
    ) {
        sharedContent(cat.id, image)
    }

//    Card(
//        modifier = modifier
//            .size(124.dp)
//            .padding(8.dp)
//            .clickable { onCatClick(index, cat, image) },
//        colors = CardDefaults.cardColors(containerColor = DarkGray)
//    ) {
//        sharedContent(cat.id, image)
//    }
}
