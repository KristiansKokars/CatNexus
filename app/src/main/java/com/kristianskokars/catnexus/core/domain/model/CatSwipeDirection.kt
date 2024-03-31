package com.kristianskokars.catnexus.core.domain.model

enum class CatSwipeDirection {
    VERTICAL, HORIZONTAL;

    fun flip(): CatSwipeDirection = when (this) {
        VERTICAL -> HORIZONTAL
        HORIZONTAL -> VERTICAL
    }
}
