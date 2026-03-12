package com.scrapeverything.app.data.repository

import com.scrapeverything.app.data.api.AuthApi
import com.scrapeverything.app.data.local.TokenStorage
import com.scrapeverything.app.data.model.request.LoginRequest
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.network.ErrorParser
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {

    suspend fun login(email: String, password: String, keepLoggedIn: Boolean): ApiResult<Unit> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                // JWT 추출 (Authorization 헤더)
                val jwt = response.headers()["Authorization"]?.removePrefix("Bearer ")

                // Refresh Token 추출 (Set-Cookie 헤더)
                val refreshToken = response.headers().values("Set-Cookie")
                    .firstOrNull { it.startsWith("refresh_token=") }
                    ?.substringAfter("refresh_token=")
                    ?.substringBefore(";")

                if (jwt != null) tokenStorage.saveJwtToken(jwt)
                if (refreshToken != null) tokenStorage.saveRefreshToken(refreshToken)
                tokenStorage.setKeepLoggedIn(keepLoggedIn)

                ApiResult.Success(Unit)
            } else {
                val error = ErrorParser.parse(response)
                ApiResult.Error(
                    code = error?.code ?: response.code().toString(),
                    message = error?.message ?: "Login failed"
                )
            }
        } catch (e: IOException) {
            ApiResult.NetworkError(e)
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    suspend fun logout(): ApiResult<Unit> {
        return try {
            val response = authApi.logout()
            tokenStorage.clearTokens()
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                // 로그아웃은 로컬 토큰 삭제가 중요하므로 에러여도 성공 처리
                ApiResult.Success(Unit)
            }
        } catch (e: Exception) {
            // 네트워크 에러여도 로컬 토큰은 삭제
            tokenStorage.clearTokens()
            ApiResult.Success(Unit)
        }
    }

    fun isLoggedIn(): Boolean {
        return tokenStorage.isKeepLoggedIn() && tokenStorage.hasTokens()
    }

    fun clearSession() {
        tokenStorage.clearTokens()
    }
}
