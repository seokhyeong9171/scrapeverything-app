package com.scrapeverything.app.data.model.request

data class ScrapAddRequest(
    val scrapTitle: String,
    val url: String,
    val description: String? = null
)

data class ScrapUpdateRequest(
    val categoryId: Long? = null,
    val scrapTitle: String,
    val url: String,
    val description: String? = null
)
