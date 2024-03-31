package com.kristianskokars.catnexus.core.data.data_source.remote

import com.kristianskokars.catnexus.core.data.model.PagedCatEntity
import kotlinx.serialization.Serializable

@Serializable
data class CatAPIModel(
    val id: String,
    val url: String,
    val name: String? = null,
) {
    fun toPagedCat() = PagedCatEntity(
        id = id,
        url = url,
        name = name,
        fetchedDateInMillis = System.currentTimeMillis(),
        isFavourited = false
    )
}
