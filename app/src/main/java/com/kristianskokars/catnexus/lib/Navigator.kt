package com.kristianskokars.catnexus.lib

import com.ramcosta.composedestinations.spec.Direction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {
    private val _navigationActions = MutableSharedFlow<Action>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val navigationActions = _navigationActions.asSharedFlow()

    suspend fun navigateUp() {
        _navigationActions.emit(Action.GoBack)
    }

    sealed class Action {
        data object GoBack : Action()
        data class Navigate(val direction: Direction) : Action()
    }
}
