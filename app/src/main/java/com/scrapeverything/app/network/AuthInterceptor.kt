package com.scrapeverything.app.network

import com.scrapeverything.app.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    companion object {
        private val NO_AUTH_PATHS = listOf(
            "auth/login",
            "auth/register",
            "auth/check-email",
            "members/check-nickname"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // 인증 불필요한 API는 토큰 없이 요청
        val isNoAuth = NO_AUTH_PATHS.any { original.url.encodedPath.contains(it) }
        if (isNoAuth) {
            return chain.proceed(original)
        }

        // JWT 토큰 첨부
        val token = tokenStorage.getJwtToken()
        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}
