package com.kristianskokars.catnexus.core.domain

import androidx.datastore.core.DataStore
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.lib.ToastMessage
import com.kristianskokars.catnexus.lib.Toaster
import com.kristianskokars.catnexus.lib.UIText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarModeHandler @Inject constructor(
    private val store: DataStore<UserSettings>,
    private val toaster: Toaster,
) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private var timeoutJob: Job? = null
    private var clicks = 0

    val isInCarMode = store.data.map { it.isInCarMode }.stateIn(scope, SharingStarted.WhileSubscribed(), false)

    suspend fun onCatNexusLogoClick() {
        if (store.data.first().isCarModeUnlocked) return

        timeoutJob?.cancel()
        timeoutJob = scope.launch {
            clicks++
            if (clicks == 5) {
                unlockCarMode()
                return@launch
            }
            delay(2000)
            clicks = 0
        }
    }

    private suspend fun unlockCarMode() {
        store.updateData { data ->
            data.copy(isCarModeUnlocked = true)
        }
        toaster.show(ToastMessage(UIText.StringResource(R.string.unlocked_car_mode)))
    }
}
