package com.kristianskokars.catnexus.feature.settings.presentation

import com.kristianskokars.catnexus.core.domain.model.CatSwipeDirection

data class SettingsState(
    val swipeDirection: CatSwipeDirection = CatSwipeDirection.HORIZONTAL,
    val showDownloadNotifications: Boolean = true,
    val isCarModeUnlocked: Boolean = false,
    val isInCarMode: Boolean = false,
)
