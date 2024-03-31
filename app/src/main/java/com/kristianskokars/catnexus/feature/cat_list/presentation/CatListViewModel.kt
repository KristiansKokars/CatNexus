package com.kristianskokars.catnexus.feature.cat_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.CarModeHandler
import com.kristianskokars.catnexus.core.domain.model.ServerError
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatListViewModel @Inject constructor(
    private val repository: CatRepository,
    private val carModeHandler: CarModeHandler,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private val hasServerError = MutableStateFlow<ServerError?>(null)

    val isInCarMode = carModeHandler.isInCarMode

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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatListState.Loading)

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

    fun onCatNexusLogoClick() {
        viewModelScope.launch {
            carModeHandler.onCatNexusLogoClick()
        }
    }
}
