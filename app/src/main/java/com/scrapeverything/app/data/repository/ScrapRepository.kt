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

    suspend fun getScrapsByCategory(
        categoryId: Long, lastId: Long? = null, size: Int = 10
    ): ApiResult<ScrapListResponse> {
        return safeApiCall { scrapApi.getScrapsByCategory(categoryId, lastId, size) }
    }

    suspend fun getScrapDetail(scrapId: Long): ApiResult<ScrapDetailResponse> {
        return safeApiCall { scrapApi.getScrapDetail(scrapId) }
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
        return safeApiCall {
            scrapApi.updateScrap(scrapId, ScrapUpdateRequest(categoryId, title, url, description))
        }
    }

    suspend fun deleteScrap(scrapId: Long): ApiResult<Unit> {
        return safeApiCall { scrapApi.deleteScrap(scrapId) }
    }
}
