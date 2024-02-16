package com.kristianskokars.catnexus.feature.cat_detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import com.kristianskokars.catnexus.core.domain.repository.ImageSharer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val repository: CatRepository,
    private val imageSharer: ImageSharer,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val navArgCat = savedStateHandle.get<Cat>("cat")!!
    val cat = repository.getCat(navArgCat.id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), navArgCat)

    fun saveCat() {
        viewModelScope.launch {
            repository.saveCatImage(cat.value)
        }
    }

    fun toggleFavouriteCat() {
        viewModelScope.launch {
            repository.toggleFavouriteForCat(cat.value.id)
        }
    }

    fun shareCat() {
        imageSharer.shareImage(cat.value.url)
    }
}
