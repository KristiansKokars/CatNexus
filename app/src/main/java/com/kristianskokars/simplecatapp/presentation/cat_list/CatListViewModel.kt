package com.kristianskokars.simplecatapp.presentation.cat_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.simplecatapp.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatListViewModel @Inject constructor(
    private val repository: CatRepository,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)

    val state = combine(
        repository.cats,
        isLoading,
    ) { cats, isLoading ->
        when {
            isLoading && cats.isEmpty() -> CatListState.Loading
            isLoading -> CatListState.Loaded(cats)
            !isLoading && cats.isEmpty() -> CatListState.NoCats
            !isLoading -> CatListState.Loaded(cats)
            else -> throw IllegalStateException("I hope this does not reach")
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CatListState.Loading)

    init {
        fetchCats(clearPreviousCats = true)
    }

    fun fetchCats(clearPreviousCats: Boolean = false) {
        if (isLoading.value) return

        viewModelScope.launch {
            Log.d("CatListViewModel", "Loading cats")
            isLoading.update { true }
            repository.refreshCats(clearPreviousCats)
            isLoading.update { false }
            Log.d("CatListViewModel", "Loaded new cats")
        }
    }
}
