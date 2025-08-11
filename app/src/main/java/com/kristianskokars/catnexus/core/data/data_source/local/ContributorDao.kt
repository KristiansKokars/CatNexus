package com.kristianskokars.catnexus.core.data.data_source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kristianskokars.catnexus.core.data.model.CONTRIBUTOR_TABLE_NAME
import com.kristianskokars.catnexus.core.data.model.ContributorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributorDao {
    @Query("SELECT * FROM $CONTRIBUTOR_TABLE_NAME ORDER BY `order`")
    fun getContributors(): Flow<List<ContributorEntity>>

    @Query("SELECT MAX(lastSyncedTimeInMillis) FROM $CONTRIBUTOR_TABLE_NAME")
    fun getLastSyncedTimeInMillis(): Flow<Long>

    @Insert
    suspend fun addContributor(contributor: ContributorEntity)

    @Insert
    suspend fun addContributors(contributors: List<ContributorEntity>)
}
