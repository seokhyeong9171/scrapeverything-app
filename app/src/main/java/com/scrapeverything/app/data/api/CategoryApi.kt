package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.request.CategoryAddRequest
import com.scrapeverything.app.data.model.request.CategoryUpdateRequest
import com.scrapeverything.app.data.model.response.CategoryListResponse
import com.scrapeverything.app.data.model.response.ScrapCountResponse
import retrofit2.Response
import retrofit2.http.*

interface CategoryApi {

    @GET("api/v1/categories")
    suspend fun getCategories(
        @Query("lastId") lastId: Long? = null,
        @Query("size") size: Int = 10
    ): Response<CategoryListResponse>

    @POST("api/v1/categories")
    suspend fun addCategory(
        @Body request: CategoryAddRequest
    ): Response<Unit>

    @PATCH("api/v1/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: Long,
        @Body request: CategoryUpdateRequest
    ): Response<Unit>

    @DELETE("api/v1/categories/{categoryId}")
    suspend fun deleteCategory(
        @Path("categoryId") categoryId: Long
    ): Response<Unit>

    @GET("api/v1/categories/{categoryId}/count")
    suspend fun getScrapCount(
        @Path("categoryId") categoryId: Long
    ): Response<ScrapCountResponse>
}
