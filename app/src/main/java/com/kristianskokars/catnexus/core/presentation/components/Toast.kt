package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.theme.Black
import com.kristianskokars.catnexus.core.presentation.theme.Inter

@Composable
fun BelowTopBarDownloadToast(
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState
) {
    ToastHost(hostState = hostState) { data ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(top = 36.dp)
        ) {
            Toast(
                modifier = Modifier.align(Alignment.TopCenter),
                data = data,
            )
        }
    }
}

@Composable
private fun Toast(
    modifier: Modifier = Modifier,
    data: SnackbarData,
) {
    Row(
        modifier = modifier
            .padding(vertical = 24.dp, horizontal = 36.dp)
            .background(Black, CircleShape)
            .border(
                Dp.Hairline,
                Color.Gray.copy(alpha = 0.75f),
                CircleShape
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.ic_download),
            contentDescription = null,
            tint = Color.White,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            fontFamily = Inter,
            fontSize = 12.sp,
            text = data.visuals.message
        )
    }
}
