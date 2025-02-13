package com.kristianskokars.catnexus.core.domain.model

enum class PictureDoubleTapFunctionality {
    ZOOM, FAVOURITE;

    fun change(): PictureDoubleTapFunctionality = when (this) {
        ZOOM -> FAVOURITE
        FAVOURITE -> ZOOM
    }
}