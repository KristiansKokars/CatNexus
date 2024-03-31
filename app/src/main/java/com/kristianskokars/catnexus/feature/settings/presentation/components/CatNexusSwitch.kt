package com.kristianskokars.catnexus.feature.settings.presentation.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Gray600
import com.kristianskokars.catnexus.core.presentation.theme.Orange

@Composable
fun CatNexusSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Switch(
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Black,
            checkedTrackColor = Orange,
            uncheckedThumbColor = Black,
            uncheckedTrackColor = Gray600,
            uncheckedBorderColor = Gray600,
        ),
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}
