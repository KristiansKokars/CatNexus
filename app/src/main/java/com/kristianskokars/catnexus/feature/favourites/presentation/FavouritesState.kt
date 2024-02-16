package com.kristianskokars.catnexus.feature.favourites.presentation

import com.kristianskokars.catnexus.core.domain.model.Cat

data class FavouritesState(val cats: List<Cat>? = null)
