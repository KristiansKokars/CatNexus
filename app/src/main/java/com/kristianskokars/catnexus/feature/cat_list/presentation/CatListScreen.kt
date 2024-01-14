package com.kristianskokars.catnexus.feature.cat_list.presentation

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.kristianskokars.catnexus.feature.cat_list.presentation.components.CatGrid
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.feature.destinations.CatDetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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
    Column(modifier = Modifier.padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.size(8.dp))
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