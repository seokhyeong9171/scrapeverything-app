package com.scrapeverything.app.di

import android.content.Context
import androidx.room.Room
import com.scrapeverything.app.data.local.db.AppDatabase
import com.scrapeverything.app.data.local.db.dao.CategoryDao
import com.scrapeverything.app.data.local.db.dao.ScrapDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "scrap_everything.db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideScrapDao(database: AppDatabase): ScrapDao {
        return database.scrapDao()
    }
}
