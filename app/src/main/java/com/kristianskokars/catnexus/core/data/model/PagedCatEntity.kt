package com.kristianskokars.catnexus.core.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kristianskokars.catnexus.core.domain.model.Cat
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

const val PAGED_CAT_TABLE_NAME = "paged_cat"

@Entity(tableName = PAGED_CAT_TABLE_NAME)
@Serializable
@Parcelize
data class PagedCatEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val url: String,
    val name: String?,
    val fetchedDateInMillis: Long,
    val isFavourited: Boolean = false,
) : Parcelable {
    fun toCat() = Cat(
        id = id,
        url = url,
        name = name,
        fetchedDateInMillis = fetchedDateInMillis,
        isFavourited = isFavourited
    )

    fun toCatEntity() = CatEntity(
        id = id,
        url = url,
        name = name,
        fetchedDateInMillis = fetchedDateInMillis,
        isFavourited = isFavourited
    )
}
