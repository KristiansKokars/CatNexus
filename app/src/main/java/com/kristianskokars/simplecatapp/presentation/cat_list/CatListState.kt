package com.kristianskokars.simplecatapp.presentation.cat_list

import com.kristianskokars.simplecatapp.domain.model.Cat
import com.kristianskokars.simplecatapp.domain.model.ServerError

sealed class CatListState {
    data object Loading : CatListState()
    data object Error : CatListState()
    data object NoCats : CatListState()
    data class Loaded(val cats: List<Cat>, val hasError: ServerError? = null) : CatListState()
}
