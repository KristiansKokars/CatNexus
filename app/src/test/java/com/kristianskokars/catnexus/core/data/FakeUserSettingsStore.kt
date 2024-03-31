package com.kristianskokars.catnexus.core.data

import androidx.datastore.core.DataStore
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeUserSettingsStore : DataStore<UserSettings> {
    private val _data = MutableStateFlow(UserSettings())
    override val data = _data

    override suspend fun updateData(transform: suspend (t: UserSettings) -> UserSettings): UserSettings {
        _data.update { transform(it) }
        return _data.value
    }
}
