package com.kristianskokars.catnexus.feature.favourites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.CarModeHandler
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val carModeHandler: CarModeHandler,
    repository: CatRepository,
) : ViewModel() {
    val state = repository.getFavouritedCats()
        .map { FavouritesState(cats = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavouritesState())

    val isInCarMode = carModeHandler.isInCarMode

    fun onCatNexusLogoClick() {
        viewModelScope.launch {
            carModeHandler.onCatNexusLogoClick()
        }
    }
}
