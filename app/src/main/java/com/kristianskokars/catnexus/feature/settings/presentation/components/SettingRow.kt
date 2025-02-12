package com.kristianskokars.catnexus.feature.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kristianskokars.catnexus.core.presentation.theme.Gray

@Composable
fun SettingRow(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            SettingTitle(text = title)
            body?.let { SettingBody(text = it) }
        }
        Spacer(modifier = Modifier.weight(1f))
        content(Modifier.weight(1f, false))
    }
}

@Composable
private fun SettingTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(modifier = modifier, text = text, fontWeight = FontWeight.Medium)
}

@Composable
private fun SettingBody(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        color = Gray,
        lineHeight = 16.sp,
        fontSize = 12.sp
    )
}
