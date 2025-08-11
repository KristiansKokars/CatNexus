package com.kristianskokars.catnexus.feature.settings.presentation

import com.kristianskokars.catnexus.core.domain.model.PictureDoubleTapAction

sealed interface SettingsEvent {
    data object ToggleSwipeDirection : SettingsEvent
    data object ToggleDownloadNotificationsShowing : SettingsEvent
    data object ToggleCarMode : SettingsEvent
    data class ChangeDoubleTapAction(val action: PictureDoubleTapAction): SettingsEvent
    data object ResetToDefaultSettings : SettingsEvent
}
