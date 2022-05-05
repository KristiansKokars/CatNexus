package com.kristianskokars.simplecatapp.presentation.cat_list

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kristianskokars.simplecatapp.domain.model.Cat
import com.kristianskokars.simplecatapp.presentation.components.BackgroundSurface
import com.kristianskokars.simplecatapp.presentation.components.CatCard
import com.kristianskokars.simplecatapp.presentation.destinations.CatDetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

// https://placekitten.com/500/500

@Destination(start = true)
@Composable
fun CatListScreen(
    viewModel: CatListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val cats by viewModel.cats.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val refreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

   CatListContent(
       cats = cats,
       refreshState = refreshState,
       onCatClick = { navigator.navigate(CatDetailsScreenDestination(it)) },
       onRefresh = { viewModel.refreshCats() },
   )
}

@Composable
fun CatListContent(
    cats: List<Cat>,
    refreshState: SwipeRefreshState,
    onCatClick: (Cat) -> Unit,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(state = refreshState, onRefresh = onRefresh) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(cats) { cat ->
                CatCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCatClick(cat) },
                    cat = cat,
                )
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CatListContentPreview() {
    BackgroundSurface {
        CatListContent(
            cats = emptyList(),
            refreshState = SwipeRefreshState(false),
            onCatClick = {},
            onRefresh = {}
        )
    }
}
