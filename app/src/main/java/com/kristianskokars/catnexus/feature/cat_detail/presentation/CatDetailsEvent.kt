package com.kristianskokars.catnexus.feature.cat_detail.presentation

sealed interface CatDetailsEvent {
    data object SaveCat : CatDetailsEvent
    data object ToggleFavouriteCat : CatDetailsEvent
    data object ShareCat : CatDetailsEvent
    data object DismissDeleteConfirmation : CatDetailsEvent
    data object ConfirmUnfavourite : CatDetailsEvent
    data class OnPageSelected(val page: Int) : CatDetailsEvent
    data object ToggleSwipeDirection : CatDetailsEvent
}
