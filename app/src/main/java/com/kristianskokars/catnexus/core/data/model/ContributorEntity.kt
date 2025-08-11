package com.kristianskokars.catnexus.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CONTRIBUTOR_TABLE_NAME = "contributor"

@Entity(tableName = CONTRIBUTOR_TABLE_NAME)
data class ContributorEntity(
    @PrimaryKey val githubAccountId: Int,
    val role: String,
    val name: String,
    val avatarLink: String,
    val githubUrl: String,
    val order: Int,
    val lastSyncedTimeInMillis: Long,
)
