package com.kristianskokars.catnexus.feature.cat_list.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.core.domain.model.Cat

@Composable
fun CatCard(
    modifier: Modifier = Modifier,
    cat: Cat,
) {
    Card(
        modifier = modifier.padding(8.dp),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cat.url)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = "Picture of Cat ${cat.name ?: "with ID of ${cat.id}"}",
        )
    }
}
