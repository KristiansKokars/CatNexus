package com.kristianskokars.catnexus.core.domain.model

enum class PictureDoubleTapFunctionality {
    ZOOM, FAVORITE;

    fun change(): PictureDoubleTapFunctionality = when (this) {
        ZOOM -> FAVORITE
        FAVORITE -> ZOOM
    }
}