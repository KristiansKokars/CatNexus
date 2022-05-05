package com.kristianskokars.simplecatapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kristianskokars.simplecatapp.core.CAT_TABLE_NAME
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = CAT_TABLE_NAME)
@Serializable
@Parcelize
data class Cat(
    @PrimaryKey(autoGenerate = false) val id: String,
    val url: String,
    val name: String?,
) : Parcelable
