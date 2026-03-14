package com.scrapeverything.app.data.model.response

data class NoticeDetailResponse(
    val noticeId: Long,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)
