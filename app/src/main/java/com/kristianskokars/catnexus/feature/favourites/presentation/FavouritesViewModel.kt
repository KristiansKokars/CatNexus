package com.kristianskokars.catnexus.feature.favourites.presentation

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.CarModeListener
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val carModeListener: CarModeListener,
    repository: CatRepository,
    userSettingsStore: DataStore<UserSettings>,
) : ViewModel() {
    val state = repository.getFavouritedCats()
        .map { FavouritesState(cats = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavouritesState())

    val isInCarMode = userSettingsStore.data
        .map { it.isInCarMode }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onCatNexusLogoClick() {
        viewModelScope.launch {
            carModeListener.onCatNexusLogoClick()
        }
    }
}
