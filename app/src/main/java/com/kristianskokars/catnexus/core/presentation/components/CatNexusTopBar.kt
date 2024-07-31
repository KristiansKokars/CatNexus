package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CatNexusDefaultTopBar(
    hazeState: HazeState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isBorderVisible: Boolean,
    isInCarMode: Boolean,
    onCatNexusLogoClick: () -> Unit,
    navigator: DestinationsNavigator,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isTransitionActive) Black else Color.Transparent,
        label = "Shared Element Background Color Transition",
        animationSpec = tween(100)
    )

    with(animatedVisibilityScope) {
        CatNexusTopBarLayout(
            modifier = Modifier
                .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                .animateEnterExit(
                    enter = EnterTransition.None,
                    exit = fadeOut()
                )
                .background(color = backgroundColor),
            hazeState = hazeState,
            isBorderVisible = isBorderVisible
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCatNexusLogoClick,
                    ),
                    painter = if (isInCarMode) painterResource(id = R.drawable.ic_car) else painterResource(id = R.drawable.ic_cat_large),
                    contentDescription = null,
                    tint = Orange,
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = if (isInCarMode) stringResource(R.string.car_nexus) else stringResource(R.string.cat_infinity),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatNexusTopBarLayout(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    isBorderVisible: Boolean,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            modifier = modifier
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
