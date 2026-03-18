package com.scrapeverything.app.data.repository

import com.google.gson.Gson
import com.scrapeverything.app.data.api.BackupApi
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
    val categories: List<BackupCategoryData>,
    val scraps: List<BackupScrapData>
)

private data class BackupCategoryData(
    val uuid: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)

private data class BackupScrapData(
    val uuid: String,
    val categoryUuid: String,
    val title: String,
    val url: String,
    val description: String?,
    val ogTitle: String?,
    val ogDescription: String?,
    val ogImageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Singleton
class BackupRepository @Inject constructor(
    private val backupApi: BackupApi,
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
                val jsonData = result.data.data
                val backupData = gson.fromJson(jsonData, BackupData::class.java)

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

                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> ApiResult.Error(result.code, result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.exception)
        }
    }
}
