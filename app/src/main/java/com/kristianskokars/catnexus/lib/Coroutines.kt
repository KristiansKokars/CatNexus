package com.kristianskokars.catnexus.lib

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun LifecycleOwner.launchImmediate(
    block: suspend () -> Unit,
) = repeatOnLifecycle(Lifecycle.State.STARTED) {
    withContext(Dispatchers.Main.immediate) {
        block()
    }
}
