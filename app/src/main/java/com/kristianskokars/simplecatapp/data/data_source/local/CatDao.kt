package com.kristianskokars.simplecatapp.data.data_source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kristianskokars.simplecatapp.core.CAT_TABLE_NAME
import com.kristianskokars.simplecatapp.domain.model.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
    @Query("SELECT * FROM $CAT_TABLE_NAME")
    fun getCats(): Flow<List<Cat>>

    @Query("SELECT * FROM $CAT_TABLE_NAME WHERE id = :catId")
    fun getCat(catId: String): Flow<Cat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCats(cats: List<Cat>)

    @Query("DELETE FROM $CAT_TABLE_NAME WHERE id NOT IN (:catIds)")
    suspend fun clearCatsNotIn(catIds: List<String>)
}
