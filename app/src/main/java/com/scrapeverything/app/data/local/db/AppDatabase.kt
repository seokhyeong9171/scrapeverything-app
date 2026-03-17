package com.scrapeverything.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scrapeverything.app.data.local.db.dao.CategoryDao
import com.scrapeverything.app.data.local.db.dao.ScrapDao
import com.scrapeverything.app.data.local.db.entity.CategoryEntity
import com.scrapeverything.app.data.local.db.entity.ScrapEntity

@Database(
    entities = [CategoryEntity::class, ScrapEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun scrapDao(): ScrapDao
}
