package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.feature.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@Composable
fun CatNexusDefaultTopBar(
    hazeState: HazeState,
    isBorderVisible: Boolean,
    navigator: DestinationsNavigator,
) {
    CatNexusTopBarLayout(hazeState = hazeState, isBorderVisible = isBorderVisible) {
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
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier.padding(end = 8.dp),
                onClick = { navigator.navigate(SettingsScreenDestination) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.open_settings),
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatNexusTopBarLayout(
    hazeState: HazeState,
    isBorderVisible: Boolean,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            modifier = Modifier
                .hazeChild(state = hazeState)
                .fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            title = { content() }
        )
        AnimatedVisibility(visible = isBorderVisible) {
            CatNexusDivider()
        }
    }
}
