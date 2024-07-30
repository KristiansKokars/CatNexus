package com.kristianskokars.catnexus.feature.cat_detail.presentation

import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.model.CatSwipeDirection

data class CatDetailsState(
    val cats: List<Cat> = emptyList(),
    val pageCount: Int = Int.MAX_VALUE,
    val isCurrentCatDownloading: Boolean = false,
    val swipeDirection: CatSwipeDirection = CatSwipeDirection.HORIZONTAL,
    val isUnfavouritingSavedCatConfirmation: Boolean = false,
)
