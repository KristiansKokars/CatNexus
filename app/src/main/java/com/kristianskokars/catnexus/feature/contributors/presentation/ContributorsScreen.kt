package com.kristianskokars.catnexus.feature.contributors.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.CatNexusMediumTopAppBar
import com.kristianskokars.catnexus.core.presentation.components.LoadingScreen
import com.kristianskokars.catnexus.core.presentation.theme.SubtitleStyle
import com.kristianskokars.catnexus.feature.contributors.domain.Contributor
import com.kristianskokars.catnexus.nav.HomeGraph
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Composable
@Destination<HomeGraph>
fun ContributorsScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: ContributorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(
        modifier = modifier,
        navigator = navigator,
        state = state,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    state: ContributorsState,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CatNexusMediumTopAppBar(
                title = { Text(text = stringResource(R.string.contributors), fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        when (state) {
            is ContributorsState.Loaded -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(state.contributors, key = { _, contributor -> contributor.githubAccountId}) { index, contributor ->
                        ContributorRow(
                            padding = PaddingValues(vertical = 12.dp),
                            contributor = contributor
                        )
                    }
                }
            }
            ContributorsState.Loading -> LoadingScreen()
        }
    }
}

@Composable
private fun ContributorRow(
    modifier: Modifier = Modifier,
    contributor: Contributor,
    padding: PaddingValues,
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
            .clickable(
                onClick = { uriHandler.openUri(contributor.githubUrl) },
                onClickLabel = stringResource(R.string.go_to_github, contributor.name)
            )
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            model = contributor.avatarLink,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_user),
            error = painterResource(R.drawable.ic_user)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(contributor.name, fontWeight = FontWeight.Medium)
            Text(contributor.role, style = SubtitleStyle)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    BackgroundSurface {
        Content(
            navigator = EmptyDestinationsNavigator,
            state = ContributorsState.Loading
        )
    }
}
