package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.api.CategoryApi
import com.scrapeverything.app.data.model.request.CategoryAddRequest
import com.scrapeverything.app.data.model.request.CategoryUpdateRequest
import com.scrapeverything.app.data.model.response.CategoryItem
import com.scrapeverything.app.data.model.response.CategoryListResponse
import com.scrapeverything.app.data.model.response.ScrapCountResponse
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryApi: CategoryApi
) {

    suspend fun getCategories(lastId: Long?, size: Int = 10): ApiResult<CategoryListResponse> {
        return safeApiCall { categoryApi.getCategories(lastId, size) }
    }

    suspend fun getAllCategories(): ApiResult<List<CategoryItem>> {
        val allCategories = mutableListOf<CategoryItem>()
        var lastId: Long? = null
        while (true) {
            when (val result = getCategories(lastId, 100)) {
                is ApiResult.Success -> {
                    allCategories.addAll(result.data.categories)
                    if (!result.data.hasNext) break
                    lastId = result.data.nextCursorId
                }
                is ApiResult.Error -> return ApiResult.Error(result.code, result.message)
                is ApiResult.NetworkError -> return ApiResult.NetworkError(result.exception)
            }
        }
        return ApiResult.Success(allCategories)
    }

    suspend fun addCategory(name: String): ApiResult<Unit> {
        return safeApiCall { categoryApi.addCategory(CategoryAddRequest(name)) }
    }

    suspend fun updateCategory(id: Long, name: String): ApiResult<Unit> {
        return safeApiCall { categoryApi.updateCategory(id, CategoryUpdateRequest(name)) }
    }

    suspend fun deleteCategory(id: Long): ApiResult<Unit> {
        return safeApiCall { categoryApi.deleteCategory(id) }
    }

    suspend fun getScrapCount(categoryId: Long): ApiResult<ScrapCountResponse> {
        return safeApiCall { categoryApi.getScrapCount(categoryId) }
    }
}
