package com.scrapeverything.app.data.local

import javax.inject.Inject
import javax.inject.Singleton

data class SharedScrapData(
    val url: String,
    val title: String? = null,
    val description: String? = null
)

@Singleton
class SharedUrlHolder @Inject constructor() {
    var url: String? = null
    var title: String? = null
    var description: String? = null

    fun consume(): String? {
        val value = url
        url = null
        title = null
        description = null
        return value
    }

    fun consumeAll(): SharedScrapData? {
        val currentUrl = url ?: return null
        val data = SharedScrapData(
            url = currentUrl,
            title = title,
            description = description
        )
        url = null
        title = null
        description = null
        return data
    }
}
