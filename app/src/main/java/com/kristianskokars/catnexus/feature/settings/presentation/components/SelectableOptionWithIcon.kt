package com.kristianskokars.catnexus.feature.settings.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.theme.Orange

@Composable
fun SelectableOptionWithIcon(
    optionText: @Composable () -> Unit,
    optionIcon: Painter,
    contentDescriptionWhenSelected: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextWithIcon(optionText, optionIcon)
        if (isSelected) {
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                modifier = Modifier.size(16.dp),
                tint = Orange,
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = contentDescriptionWhenSelected,
            )
        }
    }
}
