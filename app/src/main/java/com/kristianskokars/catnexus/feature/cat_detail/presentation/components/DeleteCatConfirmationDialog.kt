package com.kristianskokars.catnexus.feature.cat_detail.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.ElevatedHazeStyle
import com.kristianskokars.catnexus.core.presentation.theme.Gray
import com.kristianskokars.catnexus.feature.cat_detail.presentation.CatDetailsEvent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@Composable
fun DeleteCatConfirmationDialog(
    pictureHazeState: HazeState,
    onEvent: (CatDetailsEvent) -> Unit,
    onConfirmUnfavourite: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onEvent(CatDetailsEvent.DismissDeleteConfirmation) }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 36.dp)
                .border(
                    Dp.Hairline,
                    Gray.copy(alpha = 0.4f),
                    RoundedCornerShape(4.dp)
                )
                .hazeChild(
                    pictureHazeState,
                    shape = RoundedCornerShape(4.dp),
                    style = ElevatedHazeStyle
                )
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.confirm_unfavourite_cat),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(CatDetailsEvent.DismissDeleteConfirmation) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Gray
                    )
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.padding(4.dp))
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(
                        width = Dp.Hairline,
                        color = Gray.copy(alpha = 0.4f),
                    ),
                    onClick = {
                        onEvent(CatDetailsEvent.DismissDeleteConfirmation)
                        onConfirmUnfavourite()
                    }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        }

    }
}
