package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.api.BackupApi
import com.scrapeverything.app.data.local.db.dao.CategoryDao
import com.scrapeverything.app.data.local.db.dao.ScrapDao
import com.scrapeverything.app.data.local.db.entity.CategoryEntity
import com.scrapeverything.app.data.local.db.entity.ScrapEntity
import com.scrapeverything.app.data.model.request.BackupCategoryItem
import com.scrapeverything.app.data.model.request.BackupRequest
import com.scrapeverything.app.data.model.request.BackupScrapItem
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    private val backupApi: BackupApi,
    private val categoryDao: CategoryDao,
    private val scrapDao: ScrapDao
) {

    suspend fun backup(): ApiResult<Unit> {
        val categories = categoryDao.getAllCategoriesList()
        val scraps = scrapDao.getAllScraps()

        val request = BackupRequest(
            categories = categories.map { cat ->
                BackupCategoryItem(
                    uuid = cat.uuid,
                    name = cat.name,
                    originalCreatedAt = cat.createdAt,
                    originalUpdatedAt = cat.updatedAt
                )
            },
            scraps = scraps.map { scrap ->
                val category = categories.find { it.id == scrap.categoryId }
                BackupScrapItem(
                    uuid = scrap.uuid,
                    categoryUuid = category?.uuid ?: "",
                    title = scrap.title,
                    url = scrap.url,
                    description = scrap.description,
                    ogTitle = scrap.ogTitle,
                    ogDescription = scrap.ogDescription,
                    ogImageUrl = scrap.ogImageUrl,
                    originalCreatedAt = scrap.createdAt,
                    originalUpdatedAt = scrap.updatedAt
                )
            }
        )

        return safeApiCall { backupApi.backup(request) }
    }

    suspend fun restore(): ApiResult<RestoreResult> {
        return when (val result = safeApiCall { backupApi.restore() }) {
            is ApiResult.Success -> {
                val response = result.data
                val existingCategoryUuids = categoryDao.getAllUuids().toSet()
                val existingScrapUuids = scrapDao.getAllUuids().toSet()

                // 로컬에 없는 카테고리만 추가
                val newCategories = response.categories.filter { it.uuid !in existingCategoryUuids }
                // UUID -> 새 로컬 ID 매핑
                val categoryUuidToLocalId = mutableMapOf<String, Long>()

                // 기존 카테고리의 UUID -> ID 매핑
                val existingCategories = categoryDao.getAllCategoriesList()
                existingCategories.forEach { cat ->
                    categoryUuidToLocalId[cat.uuid] = cat.id
                }

                // 새 카테고리 삽입
                newCategories.forEach { cat ->
                    val entity = CategoryEntity(
                        uuid = cat.uuid,
                        name = cat.name,
                        createdAt = cat.originalCreatedAt,
                        updatedAt = cat.originalUpdatedAt
                    )
                    val newId = categoryDao.insert(entity)
                    categoryUuidToLocalId[cat.uuid] = newId
                }

                // 로컬에 없는 스크랩만 추가
                val newScraps = response.scraps.filter { it.uuid !in existingScrapUuids }
                newScraps.forEach { scrap ->
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
                            createdAt = scrap.originalCreatedAt,
                            updatedAt = scrap.originalUpdatedAt
                        )
                        scrapDao.insert(entity)
                    }
                }

                ApiResult.Success(
                    RestoreResult(
                        addedCategories = newCategories.size,
                        addedScraps = newScraps.size
                    )
                )
            }
            is ApiResult.Error -> ApiResult.Error(result.code, result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.exception)
        }
    }
}

data class RestoreResult(
    val addedCategories: Int,
    val addedScraps: Int
)
