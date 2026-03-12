package com.scrapeverything.app.network

import com.google.gson.Gson
import com.scrapeverything.app.data.model.response.ErrorResponse
import retrofit2.Response

object ErrorParser {
    private val gson = Gson()

    fun parse(response: Response<*>): ErrorResponse? {
        return try {
            val errorBody = response.errorBody()?.string() ?: return null
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
