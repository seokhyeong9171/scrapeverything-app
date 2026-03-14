package com.scrapeverything.app.data.model.response

data class NoticeListResponse(
    val notices: List<NoticeItem>,
    val nextCursorId: Long?,
    val hasNext: Boolean
)

data class NoticeItem(
    val noticeId: Long,
    val title: String,
    val createdAt: String
)
