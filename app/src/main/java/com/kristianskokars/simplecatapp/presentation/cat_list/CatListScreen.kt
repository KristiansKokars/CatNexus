package com.kristianskokars.simplecatapp.presentation.cat_list

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kristianskokars.simplecatapp.R
import com.kristianskokars.simplecatapp.domain.model.Cat
import com.kristianskokars.simplecatapp.presentation.components.BackgroundSurface
import com.kristianskokars.simplecatapp.presentation.components.CatCard
import com.kristianskokars.simplecatapp.presentation.destinations.CatDetailsScreenDestination
import com.kristianskokars.simplecatapp.presentation.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun CatListScreen(
    viewModel: CatListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CatListContent(
        state = state,
        onCatClick = { navigator.navigate(CatDetailsScreenDestination(it)) },
        onFetchMoreCats = viewModel::fetchCats,
    )
}

@Composable
fun CatListContent(
    state: CatListState,
    onCatClick: (Cat) -> Unit,
    onFetchMoreCats: () -> Unit,
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cat_large),
                contentDescription = null,
                tint = Orange,
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.cat_infinity),
                fontSize = 24.sp,
            )
        }
        when (state) {
            is CatListState.Loaded -> CatGrid(
                cats = state.cats,
                onCatClick = onCatClick,
                bottomSlot = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        LoadingSpinner()
                    }
                },
                onScrolledToBottom = onFetchMoreCats,
            )
            is CatListState.Error -> ErrorGettingCats()
            CatListState.Loading -> LoadingCats()
            CatListState.NoCats -> Text(text = "No cats, sad :(")
        }
    }
}

@Composable
private fun LoadingCats() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LoadingSpinner()
    }
}

@Composable
private fun LoadingSpinner(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(48.dp),
        color = Orange,
    )
}

@Composable
private fun ErrorGettingCats() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null, tint = Red)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.failed_to_fetch_cats), fontSize = 14.sp)
        // TODO: needs a retry button
    }
}

private fun LazyGridState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
private fun CatGrid(cats: List<Cat>, onCatClick: (Cat) -> Unit, bottomSlot: @Composable () -> Unit = {}, onScrolledToBottom: () -> Unit = {}) {
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

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CatListContentPreview() {
    BackgroundSurface {
        CatListContent(
            state = CatListState.Loading,
            onFetchMoreCats = {},
            onCatClick = {},
        )
    }
}
