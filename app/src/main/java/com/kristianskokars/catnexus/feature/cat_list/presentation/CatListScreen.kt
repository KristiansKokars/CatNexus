package com.kristianskokars.catnexus.feature.cat_list.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.CatNexusTopBar
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.feature.cat_list.presentation.components.CatGrid
import com.kristianskokars.catnexus.feature.destinations.CatDetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

@Destination
@RootNavGraph(start = true)
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
        onRetry = viewModel::retryFetch,
    )
}

@Composable
fun CatListContent(
    state: CatListState,
    onCatClick: (Cat) -> Unit,
    onFetchMoreCats: () -> Unit,
    onRetry: () -> Unit,
) {
    val hazeState = remember { HazeState() }
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            CatNexusTopBar(
                hazeState = hazeState,
                isBorderVisible = lazyGridState.canScrollBackward
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            when (state) {
                is CatListState.Loaded -> CatGrid(
                    modifier = Modifier
                        .haze(
                            state = hazeState,
                            style = HazeStyle(tint = Black.copy(alpha = 0.72f), blurRadius = 24.dp)
                        ),
                    topContentPadding = padding,
                    state = lazyGridState,
                    cats = state.cats,
                    onCatClick = onCatClick,
                    bottomSlot = {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.hasError != null) {
                                ErrorGettingCats(onRetry)
                            } else {
                                LoadingSpinner()
                            }
                        }
                    },
                    onScrolledToBottom = onFetchMoreCats,
                )
                is CatListState.Error -> ErrorGettingCats(onRetry)
                CatListState.Loading -> LoadingCats()
                CatListState.NoCats -> Text(text = stringResource(R.string.no_cats_found))
            }
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
private fun ErrorGettingCats(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null, tint = Red)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.failed_to_fetch_cats), fontSize = 14.sp)
        TextButton(onClick = onRetry, colors = ButtonDefaults.textButtonColors(contentColor = Orange)) {
            Text(text = stringResource(R.string.retry), fontSize = 12.sp)
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
            onRetry = {},
        )
    }
}
