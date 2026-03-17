package com.scrapeverything.app.data.model.response

data class BackupResponse(
    val categories: List<BackupCategoryResponse>,
    val scraps: List<BackupScrapResponse>
)

data class BackupCategoryResponse(
    val uuid: String,
    val name: String,
    val originalCreatedAt: Long,
    val originalUpdatedAt: Long
)

data class BackupScrapResponse(
    val uuid: String,
    val categoryUuid: String,
    val title: String,
    val url: String,
    val description: String?,
    val ogTitle: String?,
    val ogDescription: String?,
    val ogImageUrl: String?,
    val originalCreatedAt: Long,
    val originalUpdatedAt: Long
)
