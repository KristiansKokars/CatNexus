package com.kristianskokars.catnexus.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val swipeDirection: CatSwipeDirection = CatSwipeDirection.HORIZONTAL,
    val showDownloadNotifications: Boolean = true,
)
