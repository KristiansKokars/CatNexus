package com.kristianskokars.catnexus.feature.settings.presentation

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.lib.asStateFlow
import com.kristianskokars.catnexus.lib.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val store: DataStore<UserSettings>
) : ViewModel() {
    val state = store.data
        .map { data ->
            SettingsState(
                swipeDirection = data.swipeDirection,
                showDownloadNotifications = data.showDownloadNotifications,
                isCarModeUnlocked = data.isCarModeUnlocked,
                isInCarMode = data.isInCarMode,
                pictureDoubleTapAction = data.pictureDoubleTapAction
            )
        }
        .asStateFlow(viewModelScope, SettingsState())

    fun onEvent(event: SettingsEvent) {
        launch {
            when (event) {
                SettingsEvent.ToggleDownloadNotificationsShowing -> {
                    store.updateData { data ->
                        data.copy(showDownloadNotifications = !data.showDownloadNotifications)
                    }
                }

                SettingsEvent.ToggleSwipeDirection -> {
                    store.updateData { data ->
                        data.copy(swipeDirection = data.swipeDirection.flip())
                    }
                }

                SettingsEvent.ToggleCarMode -> {
                    store.updateData { data ->
                        data.copy(isInCarMode = !data.isInCarMode)
                    }
                }

                is SettingsEvent.ChangeDoubleTapAction -> {
                    store.updateData { data ->
                        data.copy(pictureDoubleTapAction = event.action)
                    }
                }

                SettingsEvent.ResetToDefaultSettings -> store.updateData { UserSettings() }
            }
        }
    }
}
