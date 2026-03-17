package com.scrapeverything.app.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

data class OgMetadata(
    val ogTitle: String?,
    val ogDescription: String?,
    val ogImageUrl: String?
)

@Singleton
class OpenGraphFetcher @Inject constructor() {

    suspend fun fetch(url: String): OgMetadata = withContext(Dispatchers.IO) {
        try {
            val normalizedUrl = if (!url.startsWith("http")) "https://$url" else url
            val doc = Jsoup.connect(normalizedUrl)
                .timeout(5000)
                .userAgent("Mozilla/5.0")
                .followRedirects(true)
                .get()

            val ogTitle = doc.selectFirst("meta[property=og:title]")?.attr("content")
                ?.takeIf { it.isNotBlank() }
                ?: doc.title().takeIf { it.isNotBlank() }
            val ogDescription = doc.selectFirst("meta[property=og:description]")?.attr("content")
                ?.takeIf { it.isNotBlank() }
                ?: doc.selectFirst("meta[name=description]")?.attr("content")
                    ?.takeIf { it.isNotBlank() }
            val ogImageUrl = doc.selectFirst("meta[property=og:image]")?.attr("content")
                ?.takeIf { it.isNotBlank() }

            OgMetadata(ogTitle, ogDescription, ogImageUrl)
        } catch (e: Exception) {
            OgMetadata(null, null, null)
        }
    }
}
