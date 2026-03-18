package com.scrapeverything.app.data.model.response

data class BackupListResponse(
    val backups: List<BackupItem>
)

data class BackupItem(
    val backupId: Long,
    val createdAt: String
)
