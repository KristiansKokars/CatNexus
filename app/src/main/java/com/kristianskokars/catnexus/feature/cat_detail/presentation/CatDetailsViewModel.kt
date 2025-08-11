package com.kristianskokars.catnexus.feature.cat_detail.presentation

import androidx.datastore.core.DataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.model.CatSwipeDirection
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import com.kristianskokars.catnexus.core.domain.repository.ImageSharer
import com.kristianskokars.catnexus.lib.Navigator
import com.kristianskokars.catnexus.lib.ToastIcon
import com.kristianskokars.catnexus.lib.ToastMessage
import com.kristianskokars.catnexus.lib.Toaster
import com.kristianskokars.catnexus.lib.UIText
import com.kristianskokars.catnexus.lib.asStateFlow
import com.kristianskokars.catnexus.lib.launch
import com.ramcosta.composedestinations.generated.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val repository: CatRepository,
    private val imageSharer: ImageSharer,
    private val navigator: Navigator,
    private val userSettingsStore: DataStore<UserSettings>,
    private val toaster: Toaster,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val navArgs = savedStateHandle.navArgs<CatDetailsScreenNavArgs>()
    private val showFavourites = navArgs.showFavourites
    private val startingCatPageIndex = navArgs.catPageIndex
    private val _page = MutableStateFlow(startingCatPageIndex)
    private val _isUnfavouritingSavedCatConfirmation = MutableStateFlow(false)

    private val pageCount = if (showFavourites)
        repository.getFavouritedCats().map { it.size }.asStateFlow(viewModelScope, Int.MAX_VALUE)
    else
        repository.catCount

    private val cats =
        if (showFavourites)
            repository
                .getFavouritedCats()
                .onEach { cats -> if (cats.isEmpty()) navigator.navigateUp() }
                .asStateFlow(viewModelScope, emptyList())
        else
            repository.cats

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isCatDownloading = combine(
        repository.cats,
        _page
    ) { cats, page ->
        cats[page].id
    }
        .flatMapLatest { repository.isCatDownloading(it) }
        .asStateFlow(viewModelScope, false)

    private val userSettings = userSettingsStore.data.asStateFlow(viewModelScope, UserSettings())

    private val isUnfavouritingSavedCatConfirmation = _isUnfavouritingSavedCatConfirmation.asStateFlow()

    val state = combine(
        cats, pageCount, isCatDownloading, userSettings, isUnfavouritingSavedCatConfirmation
    ) { cats, pageCount, isCatDownloading, userSettings, isUnfavouritingSavedCatConfirmation ->
        CatDetailsState(cats, pageCount, isCatDownloading, userSettings.swipeDirection, isUnfavouritingSavedCatConfirmation, userSettings.pictureDoubleTapAction)
    }.asStateFlow(viewModelScope, CatDetailsState())

    fun onEvent(event: CatDetailsEvent) {
        when (event) {
            CatDetailsEvent.ConfirmUnfavourite -> confirmUnfavourite()
            CatDetailsEvent.DismissDeleteConfirmation -> dismissDeleteConfirmation()
            is CatDetailsEvent.OnPageSelected -> onPageSelected(event.page)
            CatDetailsEvent.SaveCat -> saveCat()
            CatDetailsEvent.ShareCat -> shareCat()
            CatDetailsEvent.ToggleFavouriteCat -> toggleFavouriteCat()
            CatDetailsEvent.ToggleSwipeDirection -> onToggleSwipeDirection()
        }
    }

    private fun saveCat() {
        launch {
            repository.saveCatImage(currentCat())
        }
    }

    private fun toggleFavouriteCat() {
        launch {
            if (showFavourites && currentCat().isFavourited) {
                _isUnfavouritingSavedCatConfirmation.update { true }
            } else {
                repository.toggleFavouriteForCat(currentCat().id)
            }
        }
    }

    private fun shareCat() {
        imageSharer.shareImage(currentCat().url)
    }

    private fun dismissDeleteConfirmation() {
        _isUnfavouritingSavedCatConfirmation.update { false }
    }

    private fun confirmUnfavourite() {
        launch {
            _isUnfavouritingSavedCatConfirmation.update { false }
            repository.toggleFavouriteForCat(currentCat().id)
        }
    }

    private fun onPageSelected(page: Int) {
        if (page > cats.value.size - 1) return

        // we add more cats one page before the final one to have it preload earlier for better UX
        if (page == cats.value.size - 2 && !showFavourites) {
            launch {
                repository.refreshCats()
            }
        }

        _page.update { page }
    }

    private fun onToggleSwipeDirection() {
        launch {
            userSettingsStore.updateData {
                val newDirection = it.swipeDirection.flip()
                // TODO: make string resource out of this
                val text = when (newDirection) {
                    CatSwipeDirection.VERTICAL -> "Vertical"
                    CatSwipeDirection.HORIZONTAL -> "Horizontal"
                }
                launch {
                    toaster.show(
                        ToastMessage(
                            UIText.StringResource(
                                R.string.flipped_direction_notification,
                                text
                            ),
                            ToastIcon.Resource(
                                R.drawable.ic_vertical_orientation
                            )
                        )
                    )
                }
                it.copy(swipeDirection = newDirection)
            }
        }
    }

    private fun currentCat(): Cat {
        return cats.value[_page.value]
    }
}
