package com.kristianskokars.catnexus.lib

import android.content.Context
import androidx.annotation.StringRes

sealed class UIText {
    class StringResource(@StringRes val id: Int, val argument: String = "") : UIText()

    fun get(context: Context) = when (this) {
        is StringResource -> context.getString(id, argument)
    }
}
