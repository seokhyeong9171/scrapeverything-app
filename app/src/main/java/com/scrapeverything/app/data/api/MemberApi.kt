package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.request.MemberInfoUpdateRequest
import com.scrapeverything.app.data.model.response.MemberInfoResponse
import com.scrapeverything.app.data.model.response.NicknameCheckResponse
import retrofit2.Response
import retrofit2.http.*

interface MemberApi {

    @GET("api/v1/members/check-nickname")
    suspend fun checkNickname(
        @Query("nickname") nickname: String
    ): Response<NicknameCheckResponse>

    @GET("api/v1/members/info")
    suspend fun getMemberInfo(): Response<MemberInfoResponse>

    @PATCH("api/v1/members/info")
    suspend fun updateMemberInfo(
        @Body request: MemberInfoUpdateRequest
    ): Response<Unit>
}
