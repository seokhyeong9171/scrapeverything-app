package com.scrapeverything.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is RegisterEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is RegisterEvent.RegisterSuccess -> {
                    snackbarHostState.showSnackbar("회원가입이 완료되었습니다")
                    onRegisterSuccess()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "회원가입",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.step == RegisterStep.EMAIL) {
                            onNavigateBack()
                        } else {
                            viewModel.goBackToEmail()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 진행 단계 표시
            StepIndicator(currentStep = uiState.step)

            Spacer(modifier = Modifier.height(32.dp))

            when (uiState.step) {
                RegisterStep.EMAIL -> EmailStepContent(
                    email = uiState.email,
                    onEmailChange = viewModel::onEmailChange,
                    onSendCode = viewModel::sendEmailCode,
                    isLoading = uiState.isLoading,
                    error = uiState.error
                )
                RegisterStep.EMAIL_VERIFY -> EmailVerifyStepContent(
                    email = uiState.email,
                    code = uiState.verificationCode,
                    onCodeChange = viewModel::onVerificationCodeChange,
                    onVerify = viewModel::verifyEmailCode,
                    onResend = viewModel::sendEmailCode,
                    isLoading = uiState.isLoading,
                    error = uiState.error
                )
                RegisterStep.INFO -> InfoStepContent(
                    nickname = uiState.nickname,
                    password = uiState.password,
                    passwordConfirm = uiState.passwordConfirm,
                    onNicknameChange = viewModel::onNicknameChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onPasswordConfirmChange = viewModel::onPasswordConfirmChange,
                    onCheckNickname = viewModel::checkNickname,
                    onRegister = viewModel::register,
                    isLoading = uiState.isLoading,
                    isNicknameChecked = uiState.isNicknameChecked,
                    error = uiState.error,
                    nicknameError = uiState.nicknameError
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: RegisterStep) {
    val steps = listOf("이메일", "인증", "정보 입력")
    val currentIndex = currentStep.ordinal

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = if (index <= currentIndex) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${index + 1}",
                            color = if (index <= currentIndex) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index <= currentIndex) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmailStepContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    isLoading: Boolean,
    error: String?
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = "이메일 인증",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "인증코드를 받을 이메일을 입력해주세요",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("이메일") },
        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onSendCode()
            }
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    )

    ErrorText(error)

    Spacer(modifier = Modifier.height(24.dp))

    ActionButton(
        text = "인증코드 전송",
        onClick = onSendCode,
        isLoading = isLoading
    )
}

@Composable
private fun EmailVerifyStepContent(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    isLoading: Boolean,
    error: String?
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = "인증코드 입력",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "$email\n(으)로 전송된 인증코드를 입력해주세요",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = code,
        onValueChange = onCodeChange,
        label = { Text("인증코드 (6자리)") },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onVerify()
            }
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    )

    ErrorText(error)

    Spacer(modifier = Modifier.height(24.dp))

    ActionButton(
        text = "인증 확인",
        onClick = onVerify,
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(12.dp))

    TextButton(onClick = onResend, enabled = !isLoading) {
        Text("인증코드 재전송")
    }
}

@Composable
private fun InfoStepContent(
    nickname: String,
    password: String,
    passwordConfirm: String,
    onNicknameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onCheckNickname: () -> Unit,
    onRegister: () -> Unit,
    isLoading: Boolean,
    isNicknameChecked: Boolean,
    error: String?,
    nicknameError: String?
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }

    Text(
        text = "정보 입력",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "닉네임과 비밀번호를 설정해주세요",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 닉네임 + 중복확인
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            label = { Text("닉네임") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            singleLine = true,
            isError = nicknameError != null,
            supportingText = if (nicknameError != null) {
                { Text(nicknameError, color = MaterialTheme.colorScheme.error) }
            } else if (isNicknameChecked) {
                { Text("사용 가능", color = MaterialTheme.colorScheme.primary) }
            } else null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onCheckNickname,
            modifier = Modifier.padding(top = 8.dp),
            enabled = nickname.isNotBlank() && !isNicknameChecked
        ) {
            Text("확인")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 비밀번호
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("비밀번호") },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                    else Icons.Outlined.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        singleLine = true,
        supportingText = { Text("7~15자, 영문+숫자 필수") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 비밀번호 확인
    OutlinedTextField(
        value = passwordConfirm,
        onValueChange = onPasswordConfirmChange,
        label = { Text("비밀번호 확인") },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { passwordConfirmVisible = !passwordConfirmVisible }) {
                Icon(
                    imageVector = if (passwordConfirmVisible) Icons.Outlined.VisibilityOff
                    else Icons.Outlined.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordConfirmVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        singleLine = true,
        isError = passwordConfirm.isNotEmpty() && password != passwordConfirm,
        supportingText = if (passwordConfirm.isNotEmpty() && password != passwordConfirm) {
            { Text("비밀번호가 일치하지 않습니다", color = MaterialTheme.colorScheme.error) }
        } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onRegister()
            }
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    )

    ErrorText(error)

    Spacer(modifier = Modifier.height(24.dp))

    ActionButton(
        text = "회원가입",
        onClick = onRegister,
        isLoading = isLoading
    )
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = !isLoading,
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ErrorText(error: String?) {
    if (error != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
