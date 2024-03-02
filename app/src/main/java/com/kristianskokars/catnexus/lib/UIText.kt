package com.kristianskokars.catnexus.lib

import android.content.Context
import androidx.annotation.StringRes

sealed class UIText {
    data class StringResource(@StringRes val id: Int) : UIText()

    fun get(context: Context) = when (this) {
        is StringResource -> context.getString(id)
    }
}
