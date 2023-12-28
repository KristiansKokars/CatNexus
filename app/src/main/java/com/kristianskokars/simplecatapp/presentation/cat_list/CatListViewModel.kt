package com.kristianskokars.simplecatapp.presentation.cat_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.simplecatapp.domain.model.ServerError
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
    private val hasServerError = MutableStateFlow<ServerError?>(null)

    val state = combine(
        repository.cats,
        isLoading,
        hasServerError,
    ) { cats, isLoading, hasServerError ->
        when {
            hasServerError != null -> when {
                cats.isEmpty() -> CatListState.Error
                else -> CatListState.Loaded(cats, hasServerError)
            }
            else -> when {
                isLoading && cats.isEmpty() -> CatListState.Loading
                !isLoading && cats.isEmpty() -> CatListState.NoCats
                else -> CatListState.Loaded(cats)
            }
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CatListState.Loading)

    init {
        fetchCats(clearPreviousCats = true)
    }

    fun fetchCats(clearPreviousCats: Boolean = false) {
        if (isLoading.value || hasServerError.value != null) return

        viewModelScope.launch {
            isLoading.update { true }
            repository.refreshCats(clearPreviousCats).handle(
                onSuccess = { hasServerError.update { null } },
                onError = { hasServerError.update { ServerError } },
            )
            isLoading.update { false }
        }
    }

    fun retryFetch() {
        hasServerError.update { null }
        fetchCats()
    }
}
