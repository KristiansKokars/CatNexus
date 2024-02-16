package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.presentation.theme.Orange

@Composable
fun ErrorGettingCats(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null, tint = Color.Red)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.failed_to_fetch_cats), fontSize = 14.sp)
        TextButton(onClick = onRetry, colors = ButtonDefaults.textButtonColors(contentColor = Orange)) {
            Text(text = stringResource(R.string.retry), fontSize = 12.sp)
        }
    }
}
