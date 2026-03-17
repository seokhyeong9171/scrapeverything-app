package com.scrapeverything.app.ui.member

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scrapeverything.app.BuildConfig
import com.scrapeverything.app.data.local.ThemeMode
import com.scrapeverything.app.ui.component.ConfirmDialog
import com.scrapeverything.app.ui.component.ErrorView
import com.scrapeverything.app.ui.component.FullScreenLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToBackupRestore: () -> Unit = {},
    onNavigateToNotice: () -> Unit = {},
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshLoginStatus()
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is MyPageEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is MyPageEvent.LogoutSuccess -> {
                    snackbarHostState.showSnackbar("로그아웃되었습니다")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "마이페이지",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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

        if (uiState.isLoggedIn) {
            // 로그인 상태
            when {
                uiState.isLoading -> {
                    FullScreenLoading(modifier = Modifier.padding(paddingValues))
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadMemberInfo() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                else -> {
                    LoggedInContent(
                        uiState = uiState,
                        paddingValues = paddingValues,
                        onNavigateToBackupRestore = onNavigateToBackupRestore,
                        onNavigateToNotice = onNavigateToNotice,
                        viewModel = viewModel
                    )
                }
            }
        } else {
            // 비로그인 상태
            LoggedOutContent(
                uiState = uiState,
                paddingValues = paddingValues,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToNotice = onNavigateToNotice,
                viewModel = viewModel
            )
        }
    }

    // 닉네임 수정 다이얼로그
    if (uiState.showEditNicknameDialog) {
        NicknameEditDialog(
            currentNickname = uiState.nickname,
            onConfirm = { newNickname -> viewModel.updateNickname(newNickname) },
            onDismiss = { viewModel.dismissEditNicknameDialog() }
        )
    }

    // 로그아웃 확인 다이얼로그
    if (uiState.showLogoutDialog) {
        ConfirmDialog(
            title = "로그아웃",
            message = "로그아웃 하시겠습니까?",
            confirmText = "로그아웃",
            onConfirm = { viewModel.logout() },
            onDismiss = { viewModel.dismissLogoutDialog() }
        )
    }

    // 회원탈퇴 확인 다이얼로그
    if (uiState.showWithdrawDialog) {
        ConfirmDialog(
            title = "회원탈퇴",
            message = "정말로 탈퇴하시겠습니까?\n탈퇴 후에는 서버의 백업 데이터가 삭제됩니다.",
            confirmText = "탈퇴",
            onConfirm = { viewModel.withdraw() },
            onDismiss = { viewModel.dismissWithdrawDialog() }
        )
    }
}

@Composable
private fun LoggedInContent(
    uiState: MyPageUiState,
    paddingValues: PaddingValues,
    onNavigateToBackupRestore: () -> Unit,
    onNavigateToNotice: () -> Unit,
    viewModel: MyPageViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 프로필 섹션
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // 닉네임
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.nickname,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.showEditNicknameDialog() }) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "닉네임 수정",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 이메일
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 가입일
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "가입일: ${formatDate(uiState.createdAt)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 백업/복원 버튼
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToBackupRestore),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Backup,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "백업 / 복원",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 공지사항 버튼
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToNotice),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Campaign,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "공지사항",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 화면 모드 설정
        Text(
            text = "화면 모드",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        ThemeModeSelector(
            selectedMode = uiState.selectedThemeMode,
            onModeSelected = { viewModel.setThemeMode(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 로그아웃 버튼
        OutlinedButton(
            onClick = { viewModel.showLogoutDialog() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("로그아웃")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 회원탈퇴 버튼
        TextButton(
            onClick = { viewModel.showWithdrawDialog() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "회원탈퇴",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ContactFooter()
    }
}

@Composable
private fun LoggedOutContent(
    uiState: MyPageUiState,
    paddingValues: PaddingValues,
    onNavigateToLogin: () -> Unit,
    onNavigateToNotice: () -> Unit,
    viewModel: MyPageViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 로그인 유도 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "로그인하면 데이터를 백업할 수 있어요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateToLogin) {
                    Icon(
                        Icons.AutoMirrored.Filled.Login,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("로그인")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 공지사항 버튼
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToNotice),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Campaign,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "공지사항",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 화면 모드 설정
        Text(
            text = "화면 모드",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        ThemeModeSelector(
            selectedMode = uiState.selectedThemeMode,
            onModeSelected = { viewModel.setThemeMode(it) }
        )

        Spacer(modifier = Modifier.weight(1f))

        ContactFooter()
    }
}

@Composable
private fun ContactFooter() {
    val context = LocalContext.current
    val contactEmail = "scrapeverything1234@gmail.com"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "문의 : $contactEmail",
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$contactEmail")
                        putExtra(Intent.EXTRA_SUBJECT, "[조각모음] 문의")
                    }
                    context.startActivity(intent)
                }
                .padding(vertical = 16.dp)
        )

        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun NicknameEditDialog(
    currentNickname: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nickname by remember { mutableStateOf(currentNickname) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("닉네임 수정") },
        text = {
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(nickname) },
                enabled = nickname.isNotBlank() && nickname != currentNickname
            ) {
                Text("수정")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun ThemeModeSelector(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    val options = listOf(
        ThemeMode.LIGHT to "라이트",
        ThemeMode.DARK to "다크",
        ThemeMode.AUTO to "자동"
    )

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (mode, label) ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) }
            ) {
                Text(label)
            }
        }
    }
}

private fun formatDate(dateTimeStr: String): String {
    return try {
        dateTimeStr.replace("T", " ").take(10)
    } catch (e: Exception) {
        dateTimeStr
    }
}
