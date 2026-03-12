package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.request.EmailCheckRequest
import com.scrapeverything.app.data.model.request.LoginRequest
import com.scrapeverything.app.data.model.request.RegisterRequest
import com.scrapeverything.app.data.model.response.EmailCheckResponse
import com.scrapeverything.app.data.model.response.EmailSendResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<Unit>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @GET("api/v1/auth/check-email")
    suspend fun sendEmailCode(
        @Query("email") email: String
    ): Response<EmailSendResponse>

    @POST("api/v1/auth/check-email")
    suspend fun checkEmailCode(
        @Body request: EmailCheckRequest
    ): Response<EmailCheckResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(): Response<Unit>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>

    @DELETE("api/v1/auth/withdraw")
    suspend fun withdraw(): Response<Unit>
}
