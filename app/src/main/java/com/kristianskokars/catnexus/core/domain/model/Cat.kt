package com.kristianskokars.catnexus.core.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kristianskokars.catnexus.core.CAT_TABLE_NAME
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// in a larger app, we would separate the entity model from the domain one, but here it does not make sense to do so
@Entity(tableName = CAT_TABLE_NAME)
@Serializable
@Parcelize
data class Cat(
    @PrimaryKey(autoGenerate = false) val id: String,
    val url: String,
    val name: String?,
    val fetchedDateInMillis: Long,
) : Parcelable
