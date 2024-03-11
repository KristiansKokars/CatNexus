package com.kristianskokars.catnexus.core.data.data_source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kristianskokars.catnexus.core.data.model.PAGED_CAT_TABLE_NAME
import com.kristianskokars.catnexus.core.data.model.PagedCatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PagedCatDao {
    @Query("SELECT * FROM $PAGED_CAT_TABLE_NAME ORDER BY fetchedDateInMillis ASC")
    fun getPagedCats(): Flow<List<PagedCatEntity>>

    @Query("SELECT * FROM $PAGED_CAT_TABLE_NAME WHERE id = :catId")
    fun getCat(catId: String): Flow<PagedCatEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCats(cats: List<PagedCatEntity>)

    @Query("DELETE FROM $PAGED_CAT_TABLE_NAME WHERE id NOT IN (:catIds)")
    suspend fun clearCatsNotIn(catIds: List<String>)

    @Update
    suspend fun updateCat(cat: PagedCatEntity)

    @Transaction
    suspend fun insertNewCats(newCats: List<PagedCatEntity>, clearPrevious: Boolean = false) {
        addCats(newCats)
        if (clearPrevious) clearCatsNotIn(newCats.map { it.id })
    }
}
