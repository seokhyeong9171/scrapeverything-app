package com.scrapeverything.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.scrapeverything.app.data.local.db.entity.ScrapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapDao {

    @Query("SELECT * FROM scrap WHERE categoryId = :categoryId ORDER BY id ASC")
    fun getScrapsByCategory(categoryId: Long): Flow<List<ScrapEntity>>

    @Query("SELECT * FROM scrap WHERE id = :id")
    suspend fun getScrapById(id: Long): ScrapEntity?

    @Query("SELECT * FROM scrap ORDER BY id ASC")
    suspend fun getAllScraps(): List<ScrapEntity>

    @Insert
    suspend fun insert(scrap: ScrapEntity): Long

    @Insert
    suspend fun insertAll(scraps: List<ScrapEntity>): List<Long>

    @Update
    suspend fun update(scrap: ScrapEntity)

    @Query("DELETE FROM scrap WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scrap")
    suspend fun deleteAll()

    @Query("SELECT uuid FROM scrap")
    suspend fun getAllUuids(): List<String>
}
