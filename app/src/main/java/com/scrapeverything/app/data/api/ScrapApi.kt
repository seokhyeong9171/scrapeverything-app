package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.request.ScrapAddRequest
import com.scrapeverything.app.data.model.request.ScrapUpdateRequest
import com.scrapeverything.app.data.model.response.ScrapAddResponse
import com.scrapeverything.app.data.model.response.ScrapDetailResponse
import com.scrapeverything.app.data.model.response.ScrapListResponse
import com.scrapeverything.app.data.model.response.ScrapUpdateResponse
import retrofit2.Response
import retrofit2.http.*

interface ScrapApi {

    @GET("api/v1/categories/{categoryId}/scraps")
    suspend fun getScrapsByCategory(
        @Path("categoryId") categoryId: Long,
        @Query("lastId") lastId: Long? = null,
        @Query("size") size: Int = 10
    ): Response<ScrapListResponse>

    @GET("api/v1/scraps/{scrapId}")
    suspend fun getScrapDetail(
        @Path("scrapId") scrapId: Long
    ): Response<ScrapDetailResponse>

    @POST("api/v1/categories/{categoryId}/scraps")
    suspend fun addScrap(
        @Path("categoryId") categoryId: Long,
        @Body request: ScrapAddRequest
    ): Response<ScrapAddResponse>

    @PATCH("api/v1/scraps/{scrapId}")
    suspend fun updateScrap(
        @Path("scrapId") scrapId: Long,
        @Body request: ScrapUpdateRequest
    ): Response<ScrapUpdateResponse>

    @DELETE("api/v1/scraps/{scrapId}")
    suspend fun deleteScrap(
        @Path("scrapId") scrapId: Long
    ): Response<Unit>
}
