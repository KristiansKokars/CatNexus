package com.kristianskokars.catnexus.lib

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

suspend fun LifecycleOwner.launchImmediate(
    block: suspend () -> Unit,
) = repeatOnLifecycle(Lifecycle.State.STARTED) {
    withContext(Dispatchers.Main.immediate) {
        block()
    }
}

fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(
    context = CoroutineExceptionHandler { _, e ->
        Timber.e(e, "Coroutine failed")
    },
    block = block
)

fun <T> Flow<T>.asStateFlow(
    coroutineScope: CoroutineScope,
    initialValue: T,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5000)
) = stateIn(coroutineScope, sharingStarted, initialValue)
