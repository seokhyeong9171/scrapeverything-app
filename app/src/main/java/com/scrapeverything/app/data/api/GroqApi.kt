package com.scrapeverything.app.data.api

import com.scrapeverything.app.data.model.groq.GroqChatRequest
import com.scrapeverything.app.data.model.groq.GroqChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GroqApi {

    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: GroqChatRequest): Response<GroqChatResponse>
}
