package com.kristianskokars.catnexus.feature.favourites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.CarModeHandler
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import com.kristianskokars.catnexus.lib.asStateFlow
import com.kristianskokars.catnexus.lib.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val carModeHandler: CarModeHandler,
    repository: CatRepository,
) : ViewModel() {
    val state = repository.getFavouritedCats()
        .map { FavouritesState(cats = it) }
        .asStateFlow(viewModelScope, FavouritesState())

    val isInCarMode = carModeHandler.isInCarMode

    fun onCatNexusLogoClick() {
        launch {
            carModeHandler.onCatNexusLogoClick()
        }
    }
}
