package com.kristianskokars.catnexus.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.theme.Red
import com.kristianskokars.catnexus.feature.settings.presentation.components.CatNexusSwitch
import com.kristianskokars.catnexus.feature.settings.presentation.components.OrientationSwitch
import com.kristianskokars.catnexus.feature.settings.presentation.components.SettingRow
import com.kristianskokars.catnexus.lib.DefaultTransitions
import com.kristianskokars.catnexus.nav.HomeGraph
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<HomeGraph>(style = DefaultTransitions::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()

    Content(
        state = state,
        onEvent = viewModel::onEvent,
        navigator = navigator
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    navigator: DestinationsNavigator
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                },
                title = { Text(text = stringResource(R.string.settings), fontSize = 24.sp) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = remember {
                    object : Arrangement.Vertical {
                        override fun Density.arrange(
                            totalSize: Int,
                            sizes: IntArray,
                            outPositions: IntArray
                        ) {
                            var currentOffset = 0
                            sizes.forEachIndexed { index, size ->
                                if (index == sizes.lastIndex) {
                                    outPositions[index] = totalSize - size
                                } else {
                                    outPositions[index] = currentOffset
                                    currentOffset += size
                                }
                            }
                        }
                    }
                }
            ) {
                item("settings") {
                    SettingRow(
                        title = stringResource(R.string.swipe_direction),
                        body = stringResource(R.string.swipe_direction_desc),
                    ) {
                        OrientationSwitch(
                            orientation = state.swipeDirection,
                            onCheckedChange = { onEvent(SettingsEvent.ToggleSwipeDirection) }
                        )
                    }
                    Spacer(modifier = Modifier.size(24.dp))
                    SettingRow(
                        title = stringResource(R.string.download_notifications),
                        body = stringResource(R.string.show_download_notifications_desc)
                    ) { modifier ->
                        CatNexusSwitch(
                            modifier = modifier,
                            checked = state.showDownloadNotifications,
                            onCheckedChange = { onEvent(SettingsEvent.ToggleDownloadNotificationsShowing) }
                        )
                    }
                    if (state.isCarModeUnlocked) {
                        Spacer(modifier = Modifier.size(24.dp))
                        SettingRow(
                            title = stringResource(R.string.car_mode),
                            body = stringResource(R.string.car_mode_desc),
                        ) {
                            CatNexusSwitch(
                                modifier = Modifier.weight(1f, false),
                                checked = state.isInCarMode,
                                onCheckedChange = { onEvent(SettingsEvent.ToggleCarMode) }
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(24.dp))
                    SettingRow(
                        title = stringResource(R.string.reset_to_default_settings)
                    ) {
                        IconButton(
                            onClick = { onEvent(SettingsEvent.ResetToDefaultSettings) }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_reset), tint = Red, contentDescription = null)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.made_with_love), fontSize = 12.sp, lineHeight = 2.sp)
                Text(text = stringResource(R.string.by_kristians), fontSize = 12.sp)
            }
        }
    }
}
