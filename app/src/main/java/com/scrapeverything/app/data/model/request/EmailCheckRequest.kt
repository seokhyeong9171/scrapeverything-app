package com.scrapeverything.app.data.model.request

data class EmailCheckRequest(
    val email: String,
    val code: String
)
