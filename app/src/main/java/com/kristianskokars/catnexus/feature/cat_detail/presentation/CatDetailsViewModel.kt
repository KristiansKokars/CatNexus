package com.kristianskokars.catnexus.feature.cat_detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val repository: CatRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val cat = savedStateHandle.get<Cat>("cat")!!

    fun saveCat() {
        viewModelScope.launch {
            repository.saveCatImage(cat)
        }
    }
}
