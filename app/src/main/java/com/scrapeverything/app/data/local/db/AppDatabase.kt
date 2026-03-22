package com.scrapeverything.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.scrapeverything.app.data.local.db.dao.CategoryDao
import com.scrapeverything.app.data.local.db.dao.ScrapDao
import com.scrapeverything.app.data.local.db.entity.CategoryEntity
import com.scrapeverything.app.data.local.db.entity.ScrapEntity

@Database(
    entities = [CategoryEntity::class, ScrapEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun scrapDao(): ScrapDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE scrap ADD COLUMN summary TEXT DEFAULT NULL")
            }
        }
    }
}
