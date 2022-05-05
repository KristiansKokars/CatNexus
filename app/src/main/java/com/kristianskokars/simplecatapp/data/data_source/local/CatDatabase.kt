package com.kristianskokars.simplecatapp.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kristianskokars.simplecatapp.domain.model.Cat

@Database(entities = [Cat::class], version = 1)
abstract class CatDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
}
