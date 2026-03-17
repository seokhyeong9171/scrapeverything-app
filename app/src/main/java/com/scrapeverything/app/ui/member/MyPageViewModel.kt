package com.scrapeverything.app.ui.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.local.ThemeMode
import com.scrapeverything.app.data.local.ThemePreferences
import com.scrapeverything.app.data.local.TokenStorage
import com.scrapeverything.app.data.repository.AuthRepository
import com.scrapeverything.app.data.repository.MemberRepository
import com.scrapeverything.app.network.ApiResult
import com.scrapeverything.app.util.ErrorMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyPageUiState(
    val isLoggedIn: Boolean = false,
    val email: String = "",
    val nickname: String = "",
    val createdAt: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEditNicknameDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showWithdrawDialog: Boolean = false,
    val selectedThemeMode: ThemeMode = ThemeMode.AUTO
)

sealed class MyPageEvent {
    data class ShowSnackbar(val message: String) : MyPageEvent()
    object LogoutSuccess : MyPageEvent()
}

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val memberRepository: MemberRepository,
    private val authRepository: AuthRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MyPageUiState(selectedThemeMode = themePreferences.getThemeMode())
    )
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MyPageEvent>()
    val event: SharedFlow<MyPageEvent> = _event.asSharedFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val loggedIn = tokenStorage.hasTokens()
        _uiState.update { it.copy(isLoggedIn = loggedIn) }
        if (loggedIn) {
            loadMemberInfo()
        }
    }

    fun loadMemberInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = memberRepository.getMemberInfo()) {
                is ApiResult.Success -> {
                    val data = result.data
                    _uiState.update {
                        it.copy(
                            email = data.email,
                            nickname = data.nickname,
                            createdAt = data.createdAt,
                            isLoading = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = ErrorMessages.getMessage(result.code))
                    }
                }
                is ApiResult.NetworkError -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = "네트워크 연결을 확인해주세요")
                    }
                }
            }
        }
    }

    fun updateNickname(newNickname: String) {
        viewModelScope.launch {
            when (val result = memberRepository.updateMemberInfo(newNickname)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(nickname = newNickname, showEditNicknameDialog = false)
                    }
                    _event.emit(MyPageEvent.ShowSnackbar("닉네임이 변경되었습니다"))
                }
                is ApiResult.Error -> {
                    _event.emit(MyPageEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _event.emit(MyPageEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    showLogoutDialog = false,
                    email = "",
                    nickname = "",
                    createdAt = ""
                )
            }
            _event.emit(MyPageEvent.LogoutSuccess)
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            when (val result = authRepository.withdraw()) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoggedIn = false,
                            showWithdrawDialog = false,
                            email = "",
                            nickname = "",
                            createdAt = ""
                        )
                    }
                    _event.emit(MyPageEvent.LogoutSuccess)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(showWithdrawDialog = false) }
                    _event.emit(MyPageEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _uiState.update { it.copy(showWithdrawDialog = false) }
                    _event.emit(MyPageEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        themePreferences.setThemeMode(mode)
        _uiState.update { it.copy(selectedThemeMode = mode) }
    }

    fun showEditNicknameDialog() {
        _uiState.update { it.copy(showEditNicknameDialog = true) }
    }

    fun dismissEditNicknameDialog() {
        _uiState.update { it.copy(showEditNicknameDialog = false) }
    }

    fun showLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun showWithdrawDialog() {
        _uiState.update { it.copy(showWithdrawDialog = true) }
    }

    fun dismissWithdrawDialog() {
        _uiState.update { it.copy(showWithdrawDialog = false) }
    }
}
