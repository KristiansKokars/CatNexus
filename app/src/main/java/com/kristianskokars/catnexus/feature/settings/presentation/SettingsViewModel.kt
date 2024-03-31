package com.kristianskokars.catnexus.feature.settings.presentation

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val store: DataStore<UserSettings>
) : ViewModel() {
    val state = store.data
        .map { data ->
            SettingsState(
                swipeDirection = data.swipeDirection,
                showDownloadNotifications = data.showDownloadNotifications
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
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
                SettingsEvent.ResetToDefaultSettings -> store.updateData { UserSettings() }
            }
        }
    }
}
