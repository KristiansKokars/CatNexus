package com.kristianskokars.catnexus.lib

import androidx.annotation.DrawableRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Toaster @Inject constructor() {
    private val _messages = MutableSharedFlow<ToastMessage>(replay = 0, extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    suspend fun show(message: ToastMessage) {
        _messages.emit(message)
    }

}

data class ToastMessage(val text: UIText, val icon: ToastIcon? = null)

sealed interface ToastIcon {
    data class Resource(@DrawableRes val id: Int) : ToastIcon
}
