package com.kristianskokars.simplecatapp.data.data_source.remote

import com.kristianskokars.simplecatapp.domain.model.Cat
import kotlinx.serialization.Serializable

@Serializable
data class CatAPIModel(
    val id: String,
    val url: String,
    val name: String? = null,
) {
    fun toCat() = Cat(id, url, name)
}
