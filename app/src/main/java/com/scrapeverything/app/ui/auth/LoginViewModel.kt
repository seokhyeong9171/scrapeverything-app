package com.scrapeverything.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.repository.AuthRepository
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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val keepLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginEvent {
    object LoginSuccess : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<LoginEvent>()
    val event: SharedFlow<LoginEvent> = _event.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onKeepLoggedInChange(keep: Boolean) {
        _uiState.update { it.copy(keepLoggedIn = keep) }
    }

    fun onLoginClick() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.update { it.copy(error = "이메일을 입력해주세요") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "비밀번호를 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.login(
                email = state.email,
                password = state.password,
                keepLoggedIn = state.keepLoggedIn
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _event.emit(LoginEvent.LoginSuccess)
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = ErrorMessages.getMessage(result.code)
                        )
                    }
                }
                is ApiResult.NetworkError -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "네트워크 연결을 확인해주세요"
                        )
                    }
                }
            }
        }
    }
}
