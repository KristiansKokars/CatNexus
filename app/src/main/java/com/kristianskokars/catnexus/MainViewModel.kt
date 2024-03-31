package com.kristianskokars.catnexus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.CarModeHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    carModeHandler: CarModeHandler,
) : ViewModel() {
    // fixes bug where you could see Cat Nexus switch to Car Nexus title when loading in at first, now app awaits until that data is fetched
    val isInitialized = carModeHandler.isInCarMode
        .map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}
