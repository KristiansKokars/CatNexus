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
import com.kristianskokars.catnexus.core.presentation.theme.SubtitleStyle

@Composable
fun SettingRow(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    content: (@Composable (modifier: Modifier) -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(if (content != null) 0.7f else 1f)
        ) {
            SettingTitle(text = title)
            body?.let { SettingBody(text = it) }
        }
        if (content != null) {
            Spacer(modifier = Modifier.weight(1f))
            content.invoke(Modifier.weight(1f, false))
        }
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
        style = SubtitleStyle,
    )
}
