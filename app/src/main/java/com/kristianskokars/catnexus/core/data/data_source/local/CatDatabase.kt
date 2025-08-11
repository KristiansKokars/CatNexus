package com.kristianskokars.catnexus.core.data.data_source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kristianskokars.catnexus.core.data.model.CatEntity
import com.kristianskokars.catnexus.core.data.model.ContributorEntity
import com.kristianskokars.catnexus.core.data.model.PagedCatEntity

@Database(
    entities = [
        CatEntity::class,
        PagedCatEntity::class,
        ContributorEntity::class
    ],
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
    ],
    version = 4,
    exportSchema = true
)
abstract class CatDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
    abstract fun pagedCatDao(): PagedCatDao
    abstract fun contributorDao(): ContributorDao
}
