package com.kristianskokars.catnexus.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cat(
    val id: String,
    val url: String,
    val name: String?,
    val fetchedDateInMillis: Long,
    val isFavourited: Boolean = false,
) : Parcelable
