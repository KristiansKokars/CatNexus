package com.kristianskokars.simplecatapp.presentation.cat_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.simplecatapp.domain.model.Cat
import com.kristianskokars.simplecatapp.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val repository: CatRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    val cat = savedStateHandle.get<Cat>("cat")!!

    fun saveCat() {
        viewModelScope.launch {
            repository.saveCatImage(cat)
        }
    }
}
