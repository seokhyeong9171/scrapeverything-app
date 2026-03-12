package com.scrapeverything.app.data.model.response

data class MemberInfoResponse(
    val email: String,
    val nickname: String,
    val createdAt: String
)

data class NicknameCheckResponse(
    val isChecked: Boolean
)
