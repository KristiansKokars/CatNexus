package com.kristianskokars.catnexus.core.data.data_source.local

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.lib.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.io.InputStream
import java.io.OutputStream

private object UserSettingsSerializer : Serializer<UserSettings> {
    override val defaultValue: UserSettings
        get() = UserSettings()

    override suspend fun readFrom(input: InputStream): UserSettings {
        return try {
            json.decodeFromString(
                deserializer = UserSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                json.encodeToString(
                    serializer = UserSettings.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}

val Context.userSettingsStore by dataStore(
    fileName = "user-settings.json",
    serializer = UserSettingsSerializer
)
