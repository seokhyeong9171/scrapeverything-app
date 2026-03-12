package com.scrapeverything.app.data.model.response

data class ErrorResponse(
    val code: String,
    val message: String,
    val validation: Map<String, String>? = null
)
