package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.response.NoticeDetailResponse
import com.scrapeverything.app.data.model.response.NoticeListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeApi {

    @GET("api/v1/notices")
    suspend fun getNotices(
        @Query("lastId") lastId: Long? = null,
        @Query("size") size: Int = 10
    ): Response<NoticeListResponse>

    @GET("api/v1/notices/{noticeId}")
    suspend fun getNoticeDetail(
        @Path("noticeId") noticeId: Long
    ): Response<NoticeDetailResponse>
}
