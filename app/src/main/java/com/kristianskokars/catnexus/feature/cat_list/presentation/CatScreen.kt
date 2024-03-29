package com.kristianskokars.catnexus.feature.cat_list.presentation

import androidx.compose.runtime.Composable
import com.kristianskokars.catnexus.core.domain.model.Cat

// TODO: support saving and restoring from this in someway
sealed interface CatScreen {
    data class List(val catId: String? = null, val cat: (@Composable () -> Unit)? = null) : CatScreen
    data class Details(val cat: Cat, val catCard: @Composable () -> Unit) : CatScreen
}
