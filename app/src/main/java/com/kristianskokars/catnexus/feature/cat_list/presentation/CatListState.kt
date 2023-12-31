package com.kristianskokars.catnexus.feature.cat_list.presentation

import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.model.ServerError

sealed class CatListState {
    data object Loading : CatListState()
    data object Error : CatListState()
    data object NoCats : CatListState()
    data class Loaded(val cats: List<Cat>, val hasError: ServerError? = null) : CatListState()
}
