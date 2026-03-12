package com.scrapeverything.app.network

import com.scrapeverything.app.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // refresh 요청일 때 쿠키로 refresh token 전달
        if (request.url.encodedPath.contains("auth/refresh")) {
            val refreshToken = tokenStorage.getRefreshToken()
            if (refreshToken != null) {
                val jwt = tokenStorage.getJwtToken()
                val newRequest = request.newBuilder()
                    .header("Cookie", "refresh_token=$refreshToken")
                    .apply {
                        if (jwt != null) {
                            header("Authorization", "Bearer $jwt")
                        }
                    }
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return chain.proceed(request)
    }
}
