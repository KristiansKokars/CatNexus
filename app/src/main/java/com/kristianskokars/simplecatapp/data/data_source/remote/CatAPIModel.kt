package com.kristianskokars.simplecatapp.data.data_source.remote

import com.kristianskokars.simplecatapp.domain.model.Cat
import kotlinx.serialization.Serializable

@Serializable
data class CatAPIModel(
    val id: String,
    val url: String,
    val name: String? = null,
) {
    // TODO: extract the logic out of here for time
    // TODO: may need a bit more logic to sort properly
    fun toCat() = Cat(id, url, name, fetchedDateInMillis = System.currentTimeMillis())
}
