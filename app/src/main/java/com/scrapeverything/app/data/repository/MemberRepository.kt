package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.api.MemberApi
import com.scrapeverything.app.data.model.request.MemberInfoUpdateRequest
import com.scrapeverything.app.data.model.response.MemberInfoResponse
import com.scrapeverything.app.data.model.response.NicknameCheckResponse
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val memberApi: MemberApi
) {

    suspend fun checkNickname(nickname: String): ApiResult<NicknameCheckResponse> {
        return safeApiCall { memberApi.checkNickname(nickname) }
    }

    suspend fun getMemberInfo(): ApiResult<MemberInfoResponse> {
        return safeApiCall { memberApi.getMemberInfo() }
    }

    suspend fun updateMemberInfo(nickname: String): ApiResult<Unit> {
        return safeApiCall { memberApi.updateMemberInfo(MemberInfoUpdateRequest(nickname)) }
    }
}
