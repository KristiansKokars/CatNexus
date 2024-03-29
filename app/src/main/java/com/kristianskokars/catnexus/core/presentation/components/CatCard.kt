package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.skydoves.orbital.animateBounds
import com.skydoves.orbital.rememberMovableContentOf

@Composable
fun CatCard(
    modifier: Modifier = Modifier,
    cat: Cat,
    index: Int,
    isClicked: Boolean,
    onCatClick: (index: Int, cat: Cat, catPicture: @Composable () -> Unit) -> Unit,
    sharedContent: @Composable (catId: String?, @Composable () -> Unit) -> Unit,
) {
    val image = rememberMovableContentOf {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isClicked) Modifier.animateBounds(
                        modifier = Modifier,
                        sizeAnimationSpec = tween(200),
                        positionAnimationSpec = tween(200),
                        debug = true
                    ) else Modifier
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(cat.url)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Fit,
            contentDescription = "Picture of Cat ${cat.name ?: "with ID of ${cat.id}"}",
        )
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
                onCatClick(index, cat, image)
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
