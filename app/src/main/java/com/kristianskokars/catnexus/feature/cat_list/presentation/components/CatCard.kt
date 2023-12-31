package com.kristianskokars.catnexus.feature.cat_list.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
            model = cat.url,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}
