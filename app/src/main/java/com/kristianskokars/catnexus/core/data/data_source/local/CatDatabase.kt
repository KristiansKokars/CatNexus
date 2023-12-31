package com.kristianskokars.catnexus.core.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kristianskokars.catnexus.core.domain.model.Cat

@Database(entities = [Cat::class], version = 1, exportSchema = false)
abstract class CatDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
}
