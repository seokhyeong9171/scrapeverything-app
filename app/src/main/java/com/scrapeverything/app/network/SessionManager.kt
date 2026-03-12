package com.scrapeverything.app.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 세션 만료 이벤트를 앱 전체에 전파하는 싱글톤
 * TokenAuthenticator에서 refresh 실패 시 이벤트를 발행하고,
 * NavGraph/MainActivity에서 이를 관찰하여 로그인 화면으로 이동
 */
@Singleton
class SessionManager @Inject constructor() {

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    fun onSessionExpired() {
        _sessionExpiredEvent.tryEmit(Unit)
    }
}
