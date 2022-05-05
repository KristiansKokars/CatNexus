package com.kristianskokars.simplecatapp.presentation.cat_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kristianskokars.simplecatapp.R
import com.kristianskokars.simplecatapp.domain.model.Cat
import com.kristianskokars.simplecatapp.presentation.components.CatCard
import com.ramcosta.composedestinations.annotation.Destination

data class CatDetailsScreenNavArgs(val cat: Cat)

@Destination(navArgsDelegate = CatDetailsScreenNavArgs::class)
@Composable
fun CatDetailsScreen(
    viewModel: CatDetailsViewModel = hiltViewModel(),
) {
    CatDetailsContent(
        cat = viewModel.cat,
        onDownloadClick = { viewModel.saveCat() }
    )
}

@Composable
fun CatDetailsContent(
    cat: Cat?,
    onDownloadClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (cat == null) {
            LinearProgressIndicator()
            return
        }
        CatCard(cat = cat)
        DownloadButton(onDownloadClick = onDownloadClick)
    }
}

@Composable
private fun DownloadButton(onDownloadClick: () -> Unit) {
    Button(onClick = onDownloadClick) {
        Text(text = stringResource(R.string.save_cat))
    }
}
