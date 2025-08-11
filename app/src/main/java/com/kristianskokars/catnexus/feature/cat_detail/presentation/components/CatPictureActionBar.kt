package com.kristianskokars.catnexus.feature.cat_detail.presentation.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.presentation.ElevatedHazeStyle
import com.kristianskokars.catnexus.core.presentation.components.LoadingSpinner
import com.kristianskokars.catnexus.core.presentation.theme.Gray
import com.kristianskokars.catnexus.core.presentation.theme.Orange
import com.kristianskokars.catnexus.core.presentation.theme.Red
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect

@Composable
fun BoxScope.CatPictureActionBar(
    cat: Cat,
    isCatDownloading: Boolean,
    isDownloadPermissionGranted: Boolean?,
    pictureHazeState: HazeState,
    onFavouriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onShareCat: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 36.dp)
            .border(Dp.Hairline, Gray.copy(alpha = 0.4f), CircleShape)
            .clip(CircleShape)
            .hazeEffect(pictureHazeState, style = ElevatedHazeStyle)
            .padding(8.dp)
            .align(Alignment.BottomCenter),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onFavouriteClick) {
            if (cat.isFavourited) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favourite_filled),
                    tint = Orange,
                    contentDescription = stringResource(R.string.unfavourite_cat),
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favourite),
                    contentDescription = stringResource(R.string.favourite_cat)
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        if (isCatDownloading) {
            LoadingSpinner(
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp)
            )
        } else {
            IconButton(
                onClick = {
                    if (isDownloadPermissionGranted == false) {
                        Toast.makeText(
                            context,
                            R.string.ask_for_storage_permission,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onDownloadClick()
                    }
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = stringResource(R.string.save_cat),
                    tint = if (isDownloadPermissionGranted == false) Red else Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        IconButton(
            onClick = onShareCat,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = stringResource(R.string.share_cat),
                tint = Color.White,
            )
        }
    }
}
