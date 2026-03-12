package com.scrapeverything.app.network

import com.scrapeverything.app.data.local.TokenStorage
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val refreshApiProvider: RefreshApiProvider,
    private val sessionManager: SessionManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 이미 retry한 요청이면 중단 (무한 루프 방지)
        if (response.request.header("X-Retry") != null) {
            onRefreshFailed()
            return null
        }

        // refresh 요청 자체가 실패한 경우 중단
        if (response.request.url.encodedPath.contains("auth/refresh")) {
            onRefreshFailed()
            return null
        }

        // refresh token으로 새 JWT 발급 시도
        val refreshToken = tokenStorage.getRefreshToken() ?: run {
            onRefreshFailed()
            return null
        }

        val jwt = tokenStorage.getJwtToken() ?: ""

        return try {
            val refreshRequest = Request.Builder()
                .url(
                    response.request.url.newBuilder()
                        .encodedPath("/api/v1/auth/refresh")
                        .build()
                )
                .post(okhttp3.RequestBody.create(null, ByteArray(0)))
                .header("Authorization", "Bearer $jwt")
                .header("Cookie", "refresh_token=$refreshToken")
                .build()

            val refreshResponse = refreshApiProvider.executeRefresh(refreshRequest)

            if (refreshResponse.isSuccessful) {
                // 새 JWT 저장
                val newJwt = refreshResponse.header("Authorization")
                    ?.removePrefix("Bearer ")
                val newRefresh = refreshResponse.headers("Set-Cookie")
                    .firstOrNull { it.startsWith("refresh_token=") }
                    ?.substringAfter("refresh_token=")
                    ?.substringBefore(";")

                newJwt?.let { tokenStorage.saveJwtToken(it) }
                newRefresh?.let { tokenStorage.saveRefreshToken(it) }

                // 원래 요청을 새 JWT로 재시도
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newJwt ?: jwt}")
                    .header("X-Retry", "true")
                    .build()
            } else {
                onRefreshFailed()
                null
            }
        } catch (e: Exception) {
            onRefreshFailed()
            null
        }
    }

    private fun onRefreshFailed() {
        tokenStorage.clearTokens()
        sessionManager.onSessionExpired()
    }
}
