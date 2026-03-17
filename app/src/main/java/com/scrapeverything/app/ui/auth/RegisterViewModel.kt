package com.scrapeverything.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

enum class RegisterStep {
    EMAIL,           // 이메일 입력 + 인증코드 전송
    EMAIL_VERIFY,    // 인증코드 입력
    INFO             // 닉네임 + 비밀번호 입력
}

data class RegisterUiState(
    val step: RegisterStep = RegisterStep.EMAIL,
    val email: String = "",
    val verificationCode: String = "",
    val nickname: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val nicknameError: String? = null,
    val isNicknameChecked: Boolean = false,
    val isEmailSent: Boolean = false
)

sealed class RegisterEvent {
    data class ShowSnackbar(val message: String) : RegisterEvent()
    object RegisterSuccess : RegisterEvent()
    object LoginAfterRegisterSuccess : RegisterEvent()
    data class LoginAfterRegisterFailed(val message: String) : RegisterEvent()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<RegisterEvent>()
    val event: SharedFlow<RegisterEvent> = _event.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onVerificationCodeChange(code: String) {
        _uiState.update { it.copy(verificationCode = code, error = null) }
    }

    fun onNicknameChange(nickname: String) {
        _uiState.update {
            it.copy(nickname = nickname, nicknameError = null, isNicknameChecked = false, error = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onPasswordConfirmChange(passwordConfirm: String) {
        _uiState.update { it.copy(passwordConfirm = passwordConfirm, error = null) }
    }

    // Step 1: 이메일 인증코드 전송
    fun sendEmailCode() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "이메일을 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.sendEmailCode(email)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isEmailSent = true, step = RegisterStep.EMAIL_VERIFY)
                    }
                    _event.emit(RegisterEvent.ShowSnackbar("인증코드가 전송되었습니다"))
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

    // Step 2: 인증코드 확인
    fun verifyEmailCode() {
        val code = _uiState.value.verificationCode.trim()
        if (code.isBlank()) {
            _uiState.update { it.copy(error = "인증코드를 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.checkEmailCode(_uiState.value.email, code)) {
                is ApiResult.Success -> {
                    if (result.data.isChecked) {
                        _uiState.update {
                            it.copy(isLoading = false, step = RegisterStep.INFO)
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "인증코드가 일치하지 않습니다")
                        }
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

    // 닉네임 중복 체크
    fun checkNickname() {
        val nickname = _uiState.value.nickname.trim()
        if (nickname.isBlank()) {
            _uiState.update { it.copy(nicknameError = "닉네임을 입력해주세요") }
            return
        }

        viewModelScope.launch {
            when (val result = memberRepository.checkNickname(nickname)) {
                is ApiResult.Success -> {
                    if (result.data.isChecked) {
                        // isChecked == true → 중복 있음
                        _uiState.update {
                            it.copy(nicknameError = "이미 사용 중인 닉네임입니다", isNicknameChecked = false)
                        }
                    } else {
                        _uiState.update {
                            it.copy(nicknameError = null, isNicknameChecked = true)
                        }
                        _event.emit(RegisterEvent.ShowSnackbar("사용 가능한 닉네임입니다"))
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(nicknameError = ErrorMessages.getMessage(result.code))
                    }
                }
                is ApiResult.NetworkError -> {
                    _uiState.update {
                        it.copy(nicknameError = "네트워크 연결을 확인해주세요")
                    }
                }
            }
        }
    }

    // Step 3: 회원가입
    fun register() {
        val state = _uiState.value

        if (state.nickname.isBlank()) {
            _uiState.update { it.copy(error = "닉네임을 입력해주세요") }
            return
        }
        if (!state.isNicknameChecked) {
            _uiState.update { it.copy(error = "닉네임 중복 확인을 해주세요") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "비밀번호를 입력해주세요") }
            return
        }
        if (!isValidPassword(state.password)) {
            _uiState.update { it.copy(error = "비밀번호는 7~15자, 영문+숫자를 포함해야 합니다") }
            return
        }
        if (state.password != state.passwordConfirm) {
            _uiState.update { it.copy(error = "비밀번호가 일치하지 않습니다") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.register(
                email = state.email,
                password = state.password,
                nickname = state.nickname.trim()
            )) {
                is ApiResult.Success -> {
                    // 회원가입 성공 후 자동 로그인
                    when (authRepository.login(state.email, state.password, keepLoggedIn = true)) {
                        is ApiResult.Success -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.emit(RegisterEvent.LoginAfterRegisterSuccess)
                        }
                        else -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.emit(RegisterEvent.LoginAfterRegisterFailed("회원가입은 완료되었으나 자동 로그인에 실패했습니다"))
                        }
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

    fun goBackToEmail() {
        _uiState.update { it.copy(step = RegisterStep.EMAIL, error = null) }
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length !in 7..15) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
}
