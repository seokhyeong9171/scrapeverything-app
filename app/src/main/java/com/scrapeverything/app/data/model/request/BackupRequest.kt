package com.scrapeverything.app.data.model.request

data class BackupRequest(
    val categories: List<BackupCategoryItem>,
    val scraps: List<BackupScrapItem>
)

data class BackupCategoryItem(
    val uuid: String,
    val name: String,
    val originalCreatedAt: Long,
    val originalUpdatedAt: Long
)

data class BackupScrapItem(
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
