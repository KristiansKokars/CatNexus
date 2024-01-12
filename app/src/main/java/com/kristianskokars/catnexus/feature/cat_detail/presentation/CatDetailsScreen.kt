package com.kristianskokars.catnexus.feature.cat_detail.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

data class CatDetailsScreenNavArgs(val cat: Cat)

@Destination(navArgsDelegate = CatDetailsScreenNavArgs::class)
@Composable
fun CatDetailsScreen(
    viewModel: CatDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    imageLoader: ImageLoader,
) {
    CatDetailsContent(
        cat = viewModel.cat,
        navigator = navigator,
        onDownloadClick = viewModel::saveCat,
        imageLoader = imageLoader
    )
}

@Composable
fun CatDetailsContent(
    cat: Cat,
    navigator: DestinationsNavigator,
    imageLoader: ImageLoader,
    onDownloadClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            IconButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = navigator::navigateUp,
                rippleRadius = 24.dp,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.go_back),
                )
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cat.url)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                imageLoader = imageLoader,
            )
        }
        Row(modifier = Modifier.defaultMinSize(minHeight = 48.dp)) {
            DownloadButton(
                onDownloadClick = onDownloadClick,
            )
        }
    }
}

@Composable
private fun DownloadButton(onDownloadClick: () -> Unit) {
    IconButton(
        modifier = Modifier.padding(vertical = 16.dp),
        onClick = onDownloadClick,
        rippleRadius = 40.dp,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_download),
            contentDescription = stringResource(R.string.save_cat),
            tint = Orange,
        )
    }
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    rippleRadius: Dp = 36.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = rippleRadius),
            ),
        contentAlignment = Alignment.Center,
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = content)
    }
}

@Preview
@Composable
private fun CatDetailsScreenPreview() {
    val context = LocalContext.current

    BackgroundSurface {
        CatDetailsContent(
            cat = Cat(id = "cat", url = "cat", name = "cat", fetchedDateInMillis = 0),
            navigator = EmptyDestinationsNavigator,
            imageLoader = ImageLoader.Builder(context).build()
        ) {}
    }
}
