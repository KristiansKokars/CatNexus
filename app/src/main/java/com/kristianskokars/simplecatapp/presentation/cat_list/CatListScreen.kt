package com.kristianskokars.simplecatapp.presentation.cat_list

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
            LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(3)) {
                items(cats, key = { it.id }) { cat ->
                    CatCard(
                        modifier = Modifier
                            .size(124.dp)
                            .clickable { onCatClick(cat) },
                        cat = cat,
                    )
                }
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
            onRefresh = {},
        )
    }
}
