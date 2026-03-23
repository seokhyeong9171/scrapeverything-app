package com.scrapeverything.app.data.repository

import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.scrapeverything.app.data.api.BackupApi
import com.scrapeverything.app.data.local.db.AppDatabase
import com.scrapeverything.app.data.local.db.dao.CategoryDao
import com.scrapeverything.app.data.local.db.dao.ScrapDao
import com.scrapeverything.app.data.local.db.entity.CategoryEntity
import com.scrapeverything.app.data.local.db.entity.ScrapEntity
import com.scrapeverything.app.data.model.request.BackupRequest
import com.scrapeverything.app.data.model.response.BackupItem
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

private data class BackupData(
    @SerializedName("categories") val categories: List<BackupCategoryData>,
    @SerializedName("scraps") val scraps: List<BackupScrapData>
)

private data class BackupCategoryData(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long
)

private data class BackupScrapData(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("categoryUuid") val categoryUuid: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("summary") val summary: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("ogTitle") val ogTitle: String?,
    @SerializedName("ogDescription") val ogDescription: String?,
    @SerializedName("ogImageUrl") val ogImageUrl: String?,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long
)

@Singleton
class BackupRepository @Inject constructor(
    private val backupApi: BackupApi,
    private val database: AppDatabase,
    private val categoryDao: CategoryDao,
    private val scrapDao: ScrapDao
) {
    private val gson = Gson()

    suspend fun backup(): ApiResult<Unit> {
        val categories = categoryDao.getAllCategoriesList()
        val scraps = scrapDao.getAllScraps()

        val backupData = BackupData(
            categories = categories.map { cat ->
                BackupCategoryData(
                    uuid = cat.uuid,
                    name = cat.name,
                    createdAt = cat.createdAt,
                    updatedAt = cat.updatedAt
                )
            },
            scraps = scraps.map { scrap ->
                val category = categories.find { it.id == scrap.categoryId }
                BackupScrapData(
                    uuid = scrap.uuid,
                    categoryUuid = category?.uuid ?: "",
                    title = scrap.title,
                    url = scrap.url,
                    summary = scrap.summary,
                    description = scrap.description,
                    ogTitle = scrap.ogTitle,
                    ogDescription = scrap.ogDescription,
                    ogImageUrl = scrap.ogImageUrl,
                    createdAt = scrap.createdAt,
                    updatedAt = scrap.updatedAt
                )
            }
        )

        val jsonData = gson.toJson(backupData)
        val request = BackupRequest(data = jsonData)

        return safeApiCall { backupApi.backup(request) }
    }

    suspend fun getBackupList(): ApiResult<List<BackupItem>> {
        return when (val result = safeApiCall { backupApi.getBackupList() }) {
            is ApiResult.Success -> ApiResult.Success(result.data.backups)
            is ApiResult.Error -> ApiResult.Error(result.code, result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.exception)
        }
    }

    suspend fun restore(backupId: Long): ApiResult<Unit> {
        return when (val result = safeApiCall { backupApi.restore(backupId) }) {
            is ApiResult.Success -> {
                try {
                    val jsonData = result.data.data
                    val backupData = gson.fromJson(jsonData, BackupData::class.java)

                    database.withTransaction {
                        // 로컬 데이터 전부 삭제 (scrap 먼저 - FK 제약)
                        scrapDao.deleteAll()
                        categoryDao.deleteAll()

                        // 카테고리 삽입 및 UUID -> 새 로컬 ID 매핑
                        val categoryUuidToLocalId = mutableMapOf<String, Long>()
                        backupData.categories.forEach { cat ->
                            val entity = CategoryEntity(
                                uuid = cat.uuid,
                                name = cat.name,
                                createdAt = cat.createdAt,
                                updatedAt = cat.updatedAt
                            )
                            val newId = categoryDao.insert(entity)
                            categoryUuidToLocalId[cat.uuid] = newId
                        }

                        // 스크랩 삽입
                        backupData.scraps.forEach { scrap ->
                            val categoryId = categoryUuidToLocalId[scrap.categoryUuid]
                            if (categoryId != null) {
                                val entity = ScrapEntity(
                                    uuid = scrap.uuid,
                                    categoryId = categoryId,
                                    title = scrap.title,
                                    url = scrap.url,
                                    summary = scrap.summary,
                                    description = scrap.description,
                                    ogTitle = scrap.ogTitle,
                                    ogDescription = scrap.ogDescription,
                                    ogImageUrl = scrap.ogImageUrl,
                                    createdAt = scrap.createdAt,
                                    updatedAt = scrap.updatedAt
                                )
                                scrapDao.insert(entity)
                            }
                        }
                    }

                    ApiResult.Success(Unit)
                } catch (e: Exception) {
                    ApiResult.Error("RESTORE_FAILED", "데이터 복원에 실패했습니다")
                }
            }
            is ApiResult.Error -> ApiResult.Error(result.code, result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.exception)
        }
    }
}
