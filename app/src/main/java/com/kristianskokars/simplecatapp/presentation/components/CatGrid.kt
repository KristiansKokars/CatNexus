package com.kristianskokars.simplecatapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kristianskokars.simplecatapp.domain.model.Cat

private fun LazyGridState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun CatGrid(cats: List<Cat>, onCatClick: (Cat) -> Unit, bottomSlot: @Composable () -> Unit = {}, onScrolledToBottom: () -> Unit = {}) {
    val state = rememberLazyGridState()
    val currentOnScrolledToBottom by rememberUpdatedState(onScrolledToBottom)
    val endOfListReached by remember {
        derivedStateOf {
            state.isScrolledToEnd()
        }
    }

    LaunchedEffect(endOfListReached) {
        if (!endOfListReached) return@LaunchedEffect
        currentOnScrolledToBottom()
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = state,
        columns = GridCells.Fixed(3),
    ) {
        items(cats, key = { it.id }) { cat ->
            CatCard(
                modifier = Modifier
                    .size(124.dp)
                    .clickable { onCatClick(cat) },
                cat = cat,
            )
        }
        item(span = { GridItemSpan(3) }) {
            bottomSlot()
        }
    }
}
