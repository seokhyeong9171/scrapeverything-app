package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.request.BackupRequest
import com.scrapeverything.app.data.model.response.BackupListResponse
import com.scrapeverything.app.data.model.response.BackupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BackupApi {

    @POST("/api/v1/backup")
    suspend fun backup(@Body request: BackupRequest): Response<Unit>

    @GET("/api/v1/backup")
    suspend fun getBackupList(): Response<BackupListResponse>

    @GET("/api/v1/backup/{backupId}")
    suspend fun restore(@Path("backupId") backupId: Long): Response<BackupResponse>
}
