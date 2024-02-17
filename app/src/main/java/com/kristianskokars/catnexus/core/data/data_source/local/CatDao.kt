package com.kristianskokars.catnexus.core.data.data_source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kristianskokars.catnexus.core.CAT_TABLE_NAME
import com.kristianskokars.catnexus.core.domain.model.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
    @Query("SELECT * FROM $CAT_TABLE_NAME WHERE isFavourited = 0 ORDER BY fetchedDateInMillis ASC")
    fun getCats(): Flow<List<Cat>>

    @Query("SELECT * FROM $CAT_TABLE_NAME WHERE isFavourited = 1 ORDER BY fetchedDateInMillis DESC")
    fun getFavouritedCats(): Flow<List<Cat>>

    @Query("SELECT * FROM $CAT_TABLE_NAME WHERE id = :catId")
    fun getCat(catId: String): Flow<Cat>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCats(cats: List<Cat>)

    @Query("DELETE FROM $CAT_TABLE_NAME WHERE id NOT IN (:catIds) AND isFavourited = 0")
    suspend fun clearCatsNotIn(catIds: List<String>)

    @Update
    suspend fun updateCat(cat: Cat)

    @Transaction
    suspend fun insertNewCats(newCats: List<Cat>, clearPrevious: Boolean = false) {
        addCats(newCats)
        if (clearPrevious) clearCatsNotIn(newCats.map { it.id })
    }
}
