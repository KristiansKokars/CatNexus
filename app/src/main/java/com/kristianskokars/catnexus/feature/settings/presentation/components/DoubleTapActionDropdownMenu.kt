package com.kristianskokars.catnexus.feature.settings.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.PictureDoubleTapAction
import com.kristianskokars.catnexus.core.presentation.theme.White
import com.kristianskokars.catnexus.feature.settings.presentation.SettingsEvent
import com.kristianskokars.catnexus.feature.settings.presentation.SettingsState

@Composable
fun DoubleTapActionDropdownMenu(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
) {
    var isDoubleTapSettingsDropdownExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        TextButton(
            onClick = { isDoubleTapSettingsDropdownExpanded = true },
            colors = ButtonDefaults.textButtonColors(contentColor = White)
        ) {
            when (state.pictureDoubleTapAction) {
                PictureDoubleTapAction.ZOOM -> TextWithIcon(
                    text = { Text(stringResource(R.string.zoom)) },
                    icon = painterResource(id = R.drawable.ic_magnifying_glass)
                )
                PictureDoubleTapAction.FAVOURITE -> TextWithIcon(
                    text = { Text(stringResource(R.string.favourite)) },
                    icon = painterResource(id = R.drawable.ic_favourite)
                )
            }
        }
        DropdownMenu(
            expanded = isDoubleTapSettingsDropdownExpanded,
            onDismissRequest = { isDoubleTapSettingsDropdownExpanded = !isDoubleTapSettingsDropdownExpanded }
        ) {
            DropdownMenuItem(
                text = {
                    SelectableOptionWithIcon(
                        optionText = { Text(stringResource(R.string.zoom)) },
                        optionIcon = painterResource(id = R.drawable.ic_magnifying_glass),
                        contentDescriptionWhenSelected = stringResource(R.string.zoom_selected_double_tap),
                        isSelected = state.pictureDoubleTapAction == PictureDoubleTapAction.ZOOM,
                    )
                },
                onClick = {
                    isDoubleTapSettingsDropdownExpanded = false
                    onEvent(SettingsEvent.ChangeDoubleTapAction(PictureDoubleTapAction.ZOOM))
                }
            )
            DropdownMenuItem(
                text = {
                    SelectableOptionWithIcon(
                        optionText = { Text(stringResource(R.string.favourite)) },
                        optionIcon = painterResource(id = R.drawable.ic_favourite),
                        contentDescriptionWhenSelected = stringResource(R.string.favouriting_selected_double_tap),
                        isSelected = state.pictureDoubleTapAction == PictureDoubleTapAction.FAVOURITE,
                    )
                },
                onClick = {
                    isDoubleTapSettingsDropdownExpanded = false
                    onEvent(SettingsEvent.ChangeDoubleTapAction(PictureDoubleTapAction.FAVOURITE))
                }
            )
        }
    }
}
