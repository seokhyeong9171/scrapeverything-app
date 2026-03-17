package com.scrapeverything.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.scrapeverything.app.data.local.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

data class CategoryWithScrapCount(
    val id: Long,
    val uuid: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val scrapCount: Long
)

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category ORDER BY id ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category ORDER BY id ASC")
    suspend fun getAllCategoriesList(): List<CategoryEntity>

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Query("SELECT COUNT(*) FROM scrap WHERE categoryId = :categoryId")
    suspend fun getScrapCount(categoryId: Long): Long

    @Query("""
        SELECT c.id, c.uuid, c.name, c.createdAt, c.updatedAt,
               COUNT(s.id) AS scrapCount
        FROM category c
        LEFT JOIN scrap s ON c.id = s.categoryId
        GROUP BY c.id
        ORDER BY c.id ASC
    """)
    fun getAllCategoriesWithScrapCount(): Flow<List<CategoryWithScrapCount>>

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM category WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM category")
    suspend fun deleteAll()

    @Query("SELECT uuid FROM category")
    suspend fun getAllUuids(): List<String>
}
