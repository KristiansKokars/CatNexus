package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.core.domain.model.Cat

private fun LazyGridState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun CatGrid(
    modifier: Modifier = Modifier,
    cats: List<Cat>,
    onCatClick: (index: Int) -> Unit,
    state: LazyGridState,
    topContentPadding: PaddingValues,
    bottomSlot: @Composable () -> Unit = {},
    onScrolledToBottom: () -> Unit = {}
) {
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
        modifier = modifier.fillMaxSize(),
        state = state,
        columns = GridCells.Fixed(3),
    ) {
        item(span = { GridItemSpan(3)}) {
            Box(modifier = Modifier.padding(topContentPadding))
        }
        itemsIndexed(cats, key = { _, cat -> cat.id }) { index, cat ->
            CatCard(
                modifier = Modifier
                    .size(124.dp)
                    .clickable { onCatClick(index) },
                cat = cat,
            )
        }
        item(span = { GridItemSpan(3) }) {
            bottomSlot()
        }
    }
}
