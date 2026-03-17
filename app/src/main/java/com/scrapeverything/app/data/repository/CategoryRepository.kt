package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.local.db.dao.CategoryDao
import com.scrapeverything.app.data.local.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun getAllCategoriesList(): List<CategoryEntity> {
        return categoryDao.getAllCategoriesList()
    }

    suspend fun getCategoryById(id: Long): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun addCategory(name: String): Long {
        return categoryDao.insert(CategoryEntity(name = name))
    }

    suspend fun updateCategory(id: Long, name: String) {
        val existing = categoryDao.getCategoryById(id) ?: return
        categoryDao.update(existing.copy(name = name, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteCategory(id: Long) {
        categoryDao.deleteById(id)
    }

    suspend fun getScrapCount(categoryId: Long): Long {
        return categoryDao.getScrapCount(categoryId)
    }

    suspend fun ensureDefaultCategory() {
        val categories = categoryDao.getAllCategoriesList()
        if (categories.isEmpty()) {
            categoryDao.insert(CategoryEntity(name = "기본"))
        }
    }
}
