package com.scrapeverything.app.data.local

import android.util.Log
import com.scrapeverything.app.data.api.GroqApi
import com.scrapeverything.app.data.model.groq.GroqChatRequest
import com.scrapeverything.app.data.model.groq.GroqMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiSummarizer @Inject constructor(
    private val groqApi: GroqApi
) {

    suspend fun summarize(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val normalizedUrl = if (!url.startsWith("http")) "https://$url" else url
            val content = extractContent(normalizedUrl)
            if (content == null) {
                Log.e(TAG, "Failed to extract content from: $normalizedUrl")
                return@withContext null
            }

            val messages = buildPromptMessages(normalizedUrl, content)
            val request = GroqChatRequest(messages = messages)
            val response = groqApi.chatCompletion(request)

            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content?.trim()
            } else {
                Log.e(TAG, "Groq API error: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Summarize failed", e)
            null
        }
    }

    companion object {
        private const val TAG = "AiSummarizer"
    }

    private fun extractContent(url: String): ContentResult? {
        return try {
            val doc = Jsoup.connect(url)
                .timeout(5000)
                .userAgent("Mozilla/5.0")
                .followRedirects(true)
                .get()

            when {
                isYouTube(url) -> extractOgContent(doc)
                isInstagram(url) -> extractOgContent(doc)
                else -> extractWebContent(doc)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractWebContent(doc: Document): ContentResult? {
        // 불필요한 요소 제거
        doc.select("script, style, nav, footer, header, aside, iframe, noscript, form").remove()

        val text = (doc.selectFirst("article") ?: doc.selectFirst("main") ?: doc.body())
            ?.text()
            ?.take(6000)

        if (text == null || text.length < 100) {
            // 본문이 너무 짧으면 OG 메타데이터로 폴백
            return extractOgContent(doc)
        }

        return ContentResult.WebContent(text)
    }

    private fun extractOgContent(doc: Document): ContentResult? {
        val ogTitle = doc.selectFirst("meta[property=og:title]")?.attr("content")
            ?.takeIf { it.isNotBlank() }
            ?: doc.title().takeIf { it.isNotBlank() }
        val ogDescription = doc.selectFirst("meta[property=og:description]")?.attr("content")
            ?.takeIf { it.isNotBlank() }
            ?: doc.selectFirst("meta[name=description]")?.attr("content")
                ?.takeIf { it.isNotBlank() }

        if (ogTitle == null && ogDescription == null) return null

        return ContentResult.OgContent(ogTitle, ogDescription)
    }

    private fun buildPromptMessages(url: String, content: ContentResult): List<GroqMessage> {
        val systemMessage = GroqMessage(
            role = "system",
            content = "웹 콘텐츠를 2~3문장으로 간결하게 한국어로 요약해주세요. " +
                    "\"이 글은\", \"이 페이지는\" 같은 서두 없이 핵심 내용만 요약하세요."
        )

        val userContent = when (content) {
            is ContentResult.WebContent -> "다음 콘텐츠를 요약해주세요.\n\nURL: $url\n내용:\n${content.text}"
            is ContentResult.OgContent -> {
                buildString {
                    append("다음 콘텐츠의 제목과 설명을 바탕으로 요약해주세요.\n\nURL: $url")
                    if (content.title != null) append("\n제목: ${content.title}")
                    if (content.description != null) append("\n설명: ${content.description}")
                }
            }
        }

        val userMessage = GroqMessage(role = "user", content = userContent)
        return listOf(systemMessage, userMessage)
    }

    private fun isYouTube(url: String): Boolean {
        return url.contains("youtube.com/watch") || url.contains("youtu.be/")
                || url.contains("youtube.com/shorts")
    }

    private fun isInstagram(url: String): Boolean {
        return url.contains("instagram.com/p/") || url.contains("instagram.com/reel/")
                || url.contains("instagram.com/reels/")
    }

    private sealed class ContentResult {
        data class WebContent(val text: String) : ContentResult()
        data class OgContent(val title: String?, val description: String?) : ContentResult()
    }
}
