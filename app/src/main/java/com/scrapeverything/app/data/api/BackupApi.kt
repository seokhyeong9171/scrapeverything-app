package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.request.BackupRequest
import com.scrapeverything.app.data.model.response.BackupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackupApi {

    @POST("/api/v1/backup")
    suspend fun backup(@Body request: BackupRequest): Response<Unit>

    @GET("/api/v1/backup")
    suspend fun restore(): Response<BackupResponse>
}
