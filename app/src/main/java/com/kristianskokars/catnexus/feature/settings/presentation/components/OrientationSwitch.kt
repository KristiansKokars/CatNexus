package com.kristianskokars.catnexus.feature.settings.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.CatSwipeDirection
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Gray

@Composable
fun OrientationSwitch(
    orientation: CatSwipeDirection,
    onCheckedChange: (CatSwipeDirection) -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (orientation == CatSwipeDirection.HORIZONTAL) 1f else 0f,
        label = "Orientation Rotation"
    )

    Switch(
        checked = orientation == CatSwipeDirection.HORIZONTAL,
        onCheckedChange = { if (it) onCheckedChange(CatSwipeDirection.HORIZONTAL) else onCheckedChange(CatSwipeDirection.VERTICAL) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Black,
            checkedTrackColor = Gray,
            uncheckedThumbColor = Black,
            uncheckedTrackColor = Gray,
            uncheckedBorderColor = Gray,
            uncheckedIconColor = Color.White,
            checkedIconColor = Color.White,
        ),
        thumbContent = {
            Icon(
                modifier = Modifier.size(16.dp).rotate(90 * rotation),
                painter = painterResource(id = R.drawable.ic_vertical_orientation),
                contentDescription = stringResource(R.string.cat_swipe_direction)
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    BackgroundSurface {
        Column {
            OrientationSwitch(
                orientation = CatSwipeDirection.HORIZONTAL,
                onCheckedChange = {}
            )
            Spacer(modifier = Modifier.size(8.dp))
            OrientationSwitch(
                orientation = CatSwipeDirection.VERTICAL,
                onCheckedChange = {}
            )
        }
    }
}
