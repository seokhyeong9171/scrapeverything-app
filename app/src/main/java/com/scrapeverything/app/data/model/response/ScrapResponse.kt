package com.scrapeverything.app.data.model.response

data class ScrapListResponse(
    val categoryId: Long,
    val categoryName: String,
    val scraps: List<ScrapItem>,
    val nextCursorId: Long?,
    val hasNext: Boolean
)

data class ScrapItem(
    val scrapId: Long,
    val scrapTitle: String,
    val url: String? = null
)

data class ScrapDetailResponse(
    val categoryId: Long,
    val categoryName: String,
    val scrapTitle: String,
    val url: String,
    val description: String?,
    val ogTitle: String?,
    val ogDescription: String?,
    val ogImageUrl: String?,
    val createdAt: String,
    val updatedAt: String
)

data class ScrapAddResponse(
    val scrapId: Long
)

data class ScrapUpdateResponse(
    val scrapId: Long
)
