package com.scrapeverything.app.data.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String
)
