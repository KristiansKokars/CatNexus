package com.kristianskokars.catnexus.feature.cat_detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import com.kristianskokars.catnexus.core.domain.repository.ImageSharer
import com.kristianskokars.catnexus.feature.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val repository: CatRepository,
    private val imageSharer: ImageSharer,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val navArgs = savedStateHandle.navArgs<CatDetailsScreenNavArgs>()
    private val showFavourites = navArgs.showFavourites
    private val startingCatPageIndex = navArgs.catPageIndex
    private val _page = MutableStateFlow(startingCatPageIndex)

    val pageCount = if (showFavourites)
        repository.getFavouritedCats().map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Int.MAX_VALUE)
    else
        repository.cats.map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Int.MAX_VALUE)

    val cats =
        if (showFavourites)
            repository.getFavouritedCats().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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

    fun saveCat() {
        viewModelScope.launch {
            repository.saveCatImage(currentCat())
        }
    }

    fun toggleFavouriteCat() {
        viewModelScope.launch {
            repository.toggleFavouriteForCat(currentCat().id)
        }
    }

    fun shareCat() {
        imageSharer.shareImage(currentCat().url)
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
