package com.scrapeverything.app.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authenticator에서 refresh 요청을 위한 별도 OkHttpClient 제공
 * (순환 참조 방지를 위해 Interceptor/Authenticator 없는 기본 클라이언트 사용)
 */
@Singleton
class RefreshApiProvider @Inject constructor() {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private var baseUrl: String = "http://10.0.2.2:8080/"

    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    fun getRefreshApi(): Request {
        return Request.Builder()
            .url("${baseUrl}api/v1/auth/refresh")
            .build()
    }

    fun executeRefresh(request: Request): Response {
        return client.newCall(request).execute()
    }
}
