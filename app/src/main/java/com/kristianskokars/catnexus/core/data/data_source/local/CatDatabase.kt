package com.kristianskokars.catnexus.core.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kristianskokars.catnexus.core.data.model.CatEntity
import com.kristianskokars.catnexus.core.data.model.PagedCatEntity

@Database(entities = [CatEntity::class, PagedCatEntity::class], version = 3, exportSchema = false)
abstract class CatDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
    abstract fun pagedCatDao(): PagedCatDao
}
