package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.api.NoticeApi
import com.scrapeverything.app.data.model.response.NoticeDetailResponse
import com.scrapeverything.app.data.model.response.NoticeListResponse
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepository @Inject constructor(
    private val noticeApi: NoticeApi
) {

    suspend fun getNotices(lastId: Long?, size: Int = 10): ApiResult<NoticeListResponse> {
        return safeApiCall { noticeApi.getNotices(lastId, size) }
    }

    suspend fun getNoticeDetail(noticeId: Long): ApiResult<NoticeDetailResponse> {
        return safeApiCall { noticeApi.getNoticeDetail(noticeId) }
    }
}
