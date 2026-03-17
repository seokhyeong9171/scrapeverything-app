package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.local.OpenGraphFetcher
import com.scrapeverything.app.data.local.db.dao.ScrapDao
import com.scrapeverything.app.data.local.db.entity.ScrapEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrapRepository @Inject constructor(
    private val scrapDao: ScrapDao,
    private val openGraphFetcher: OpenGraphFetcher
) {

    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    fun getScrapsByCategory(categoryId: Long): Flow<List<ScrapEntity>> {
        return scrapDao.getScrapsByCategory(categoryId)
    }

    suspend fun getScrapDetail(scrapId: Long): ScrapEntity? {
        return scrapDao.getScrapById(scrapId)
    }

    suspend fun addScrap(
        categoryId: Long, title: String, url: String, description: String? = null
    ): Long {
        val scrapId = scrapDao.insert(
            ScrapEntity(
                categoryId = categoryId,
                title = title,
                url = url,
                description = description
            )
        )
        fetchOgMetadataInBackground(scrapId, url)
        return scrapId
    }

    suspend fun updateScrap(
        scrapId: Long, categoryId: Long? = null,
        title: String, url: String, description: String? = null
    ) {
        val existing = scrapDao.getScrapById(scrapId) ?: return
        val urlChanged = existing.url != url
        scrapDao.update(
            existing.copy(
                categoryId = categoryId ?: existing.categoryId,
                title = title,
                url = url,
                description = description,
                updatedAt = System.currentTimeMillis()
            )
        )
        if (urlChanged) {
            fetchOgMetadataInBackground(scrapId, url)
        }
    }

    suspend fun deleteScrap(scrapId: Long) {
        scrapDao.deleteById(scrapId)
    }

    private fun fetchOgMetadataInBackground(scrapId: Long, url: String) {
        backgroundScope.launch {
            val og = openGraphFetcher.fetch(url)
            val scrap = scrapDao.getScrapById(scrapId) ?: return@launch
            scrapDao.update(
                scrap.copy(
                    ogTitle = og.ogTitle,
                    ogDescription = og.ogDescription,
                    ogImageUrl = og.ogImageUrl
                )
            )
        }
    }
}
