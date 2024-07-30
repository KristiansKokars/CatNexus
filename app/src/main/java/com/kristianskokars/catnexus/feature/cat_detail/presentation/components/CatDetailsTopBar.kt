package com.kristianskokars.catnexus.feature.cat_detail.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.CatSwipeDirection
import com.kristianskokars.catnexus.core.presentation.components.CatNexusTopBarLayout
import com.kristianskokars.catnexus.feature.cat_detail.presentation.CatDetailsEvent
import com.kristianskokars.catnexus.feature.cat_detail.presentation.CatDetailsState
import com.ramcosta.composedestinations.result.ResultBackNavigator
import dev.chrisbanes.haze.HazeState

@Composable
fun CatDetailsTopBar(
    hazeState: HazeState,
    zoomFactor: Float,
    resultNavigator: ResultBackNavigator<Int>,
    pagerState: PagerState,
    state: CatDetailsState,
    onEvent: (CatDetailsEvent) -> Unit,
) {
    CatNexusTopBarLayout(hazeState = hazeState, isBorderVisible = zoomFactor != 1f) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { resultNavigator.navigateBack(pagerState.currentPage) },
                rippleRadius = 24.dp,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.go_back),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { onEvent(CatDetailsEvent.ToggleSwipeDirection) },
                rippleRadius = 24.dp,
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (state.swipeDirection == CatSwipeDirection.HORIZONTAL) 1f else 0f,
                    label = "Orientation Rotation"
                )

                Icon(
                    modifier = Modifier
                        .rotate(90 * rotation),
                    painter = painterResource(id = R.drawable.ic_vertical_orientation),
                    contentDescription = stringResource(R.string.cat_swipe_direction)
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}