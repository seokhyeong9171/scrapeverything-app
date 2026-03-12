package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.request.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<Unit>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(): Response<Unit>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>

    @DELETE("api/v1/auth/withdraw")
    suspend fun withdraw(): Response<Unit>
}
