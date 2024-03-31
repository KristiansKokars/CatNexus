package com.kristianskokars.catnexus.feature.cat_detail.presentation

import androidx.datastore.core.DataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.model.CatSwipeDirection
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import com.kristianskokars.catnexus.core.domain.repository.ImageSharer
import com.kristianskokars.catnexus.feature.navArgs
import com.kristianskokars.catnexus.lib.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val repository: CatRepository,
    private val imageSharer: ImageSharer,
    private val navigator: Navigator,
    userSettingsStore: DataStore<UserSettings>,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val navArgs = savedStateHandle.navArgs<CatDetailsScreenNavArgs>()
    private val showFavourites = navArgs.showFavourites
    private val startingCatPageIndex = navArgs.catPageIndex
    private val _page = MutableStateFlow(startingCatPageIndex)
    private val _isUnfavouritingSavedCatConfirmation = MutableStateFlow(false)

    val pageCount = if (showFavourites)
        repository.getFavouritedCats().map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Int.MAX_VALUE)
    else
        repository.cats.map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Int.MAX_VALUE)

    val cats =
        if (showFavourites)
            repository
                .getFavouritedCats()
                .onEach { cats -> if (cats.isEmpty()) navigator.navigateUp() }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        else
            repository.cats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val isCatDownloading = combine(
        repository.cats,
        _page
    ) { cats, page ->
        cats[page].id
    }
        .flatMapLatest { repository.isCatDownloading(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val swipeDirection = userSettingsStore.data
        .map { it.swipeDirection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatSwipeDirection.HORIZONTAL)

    val isUnfavouritingSavedCatConfirmation = _isUnfavouritingSavedCatConfirmation.asStateFlow()

    fun saveCat() {
        viewModelScope.launch {
            repository.saveCatImage(currentCat())
        }
    }

    fun toggleFavouriteCat() {
        viewModelScope.launch {
            if (showFavourites && currentCat().isFavourited) {
                _isUnfavouritingSavedCatConfirmation.update { true }
            } else {
                repository.toggleFavouriteForCat(currentCat().id)
            }
        }
    }

    fun shareCat() {
        imageSharer.shareImage(currentCat().url)
    }

    fun dismissDeleteConfirmation() {
        _isUnfavouritingSavedCatConfirmation.update { false }
    }

    fun confirmUnfavourite() {
        viewModelScope.launch {
            _isUnfavouritingSavedCatConfirmation.update { false }
            repository.toggleFavouriteForCat(currentCat().id)
        }
    }

    fun onPageSelected(page: Int) {
        if (page > cats.value.size - 1) return

        // we add more cats one page before the final one to have it preload earlier for better UX
        if (page == cats.value.size - 2 && !showFavourites) {
            viewModelScope.launch {
                repository.refreshCats()
            }
        }

        _page.update { page }
    }

    private fun currentCat(): Cat {
        return cats.value[_page.value]
    }
}
