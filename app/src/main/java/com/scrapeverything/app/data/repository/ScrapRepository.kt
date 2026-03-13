package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.api.ScrapApi
import com.scrapeverything.app.data.model.request.ScrapAddRequest
import com.scrapeverything.app.data.model.request.ScrapUpdateRequest
import com.scrapeverything.app.data.model.response.ScrapAddResponse
import com.scrapeverything.app.data.model.response.ScrapDetailResponse
import com.scrapeverything.app.data.model.response.ScrapListResponse
import com.scrapeverything.app.data.model.response.ScrapUpdateResponse
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrapRepository @Inject constructor(
    private val scrapApi: ScrapApi
) {

    private val detailCache = mutableMapOf<Long, ScrapDetailResponse>()

    suspend fun getScrapsByCategory(
        categoryId: Long, lastId: Long? = null, size: Int = 10
    ): ApiResult<ScrapListResponse> {
        return safeApiCall { scrapApi.getScrapsByCategory(categoryId, lastId, size) }
    }

    suspend fun getScrapDetail(scrapId: Long): ApiResult<ScrapDetailResponse> {
        detailCache[scrapId]?.let { return ApiResult.Success(it) }

        val result = safeApiCall { scrapApi.getScrapDetail(scrapId) }
        if (result is ApiResult.Success) {
            detailCache[scrapId] = result.data
        }
        return result
    }

    suspend fun addScrap(
        categoryId: Long, title: String, url: String, description: String? = null
    ): ApiResult<ScrapAddResponse> {
        return safeApiCall {
            scrapApi.addScrap(categoryId, ScrapAddRequest(title, url, description))
        }
    }

    suspend fun updateScrap(
        scrapId: Long, categoryId: Long? = null,
        title: String, url: String, description: String? = null
    ): ApiResult<ScrapUpdateResponse> {
        val result = safeApiCall {
            scrapApi.updateScrap(scrapId, ScrapUpdateRequest(categoryId, title, url, description))
        }
        if (result is ApiResult.Success) {
            detailCache.remove(scrapId)
        }
        return result
    }

    suspend fun deleteScrap(scrapId: Long): ApiResult<Unit> {
        val result = safeApiCall { scrapApi.deleteScrap(scrapId) }
        if (result is ApiResult.Success) {
            detailCache.remove(scrapId)
        }
        return result
    }
}
