package com.scrapeverything.app.ui.backup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scrapeverything.app.data.model.response.BackupItem
import com.scrapeverything.app.ui.component.ConfirmDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    onNavigateBack: () -> Unit,
    viewModel: BackupRestoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BackupRestoreEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "백업 / 복원",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 백업 카드
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
                        Icons.Outlined.CloudUpload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "백업",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "현재 기기의 모든 데이터를 서버에 저장합니다.\n최근 5건의 백업이 유지됩니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.showBackupConfirmDialog() },
                        enabled = !uiState.isBackingUp && !uiState.isRestoring && !uiState.isLoadingList,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isBackingUp) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (uiState.isBackingUp) "백업 중..." else "백업하기")
                    }
                }
            }

            // 복원 카드
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
                        Icons.Outlined.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "복원",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "서버에 저장된 백업을 선택하여 복원합니다.\n현재 기기의 모든 데이터가 덮어씌워집니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.loadBackupListAndShowDialog() },
                        enabled = !uiState.isBackingUp && !uiState.isRestoring && !uiState.isLoadingList,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isRestoring || uiState.isLoadingList) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            when {
                                uiState.isLoadingList -> "불러오는 중..."
                                uiState.isRestoring -> "복원 중..."
                                else -> "복원하기"
                            }
                        )
                    }
                }
            }
        }
    }

    // 백업 확인 다이얼로그
    if (uiState.showBackupConfirmDialog) {
        ConfirmDialog(
            title = "백업",
            message = "현재 기기의 데이터를 서버에 백업합니다. 하시겠습니까?",
            confirmText = "백업",
            onConfirm = { viewModel.backup() },
            onDismiss = { viewModel.dismissBackupConfirmDialog() }
        )
    }

    // 백업 목록 선택 다이얼로그
    if (uiState.showRestoreSelectDialog) {
        BackupSelectDialog(
            backups = uiState.backupList,
            onSelect = { viewModel.selectBackupForRestore(it) },
            onDismiss = { viewModel.dismissRestoreSelectDialog() }
        )
    }

    // 복원 확인 다이얼로그
    if (uiState.showRestoreConfirmDialog) {
        ConfirmDialog(
            title = "복원",
            message = "현재 기기의 모든 데이터가 삭제되고 선택한 백업 데이터로 대체됩니다. 하시겠습니까?",
            confirmText = "복원",
            onConfirm = { viewModel.restore() },
            onDismiss = { viewModel.dismissRestoreConfirmDialog() }
        )
    }
}

@Composable
private fun BackupSelectDialog(
    backups: List<BackupItem>,
    onSelect: (BackupItem) -> Unit,
    onDismiss: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "복원할 백업 선택",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                backups.forEachIndexed { index, backup ->
                    val displayDate = try {
                        LocalDateTime.parse(backup.createdAt).format(formatter)
                    } catch (e: Exception) {
                        backup.createdAt
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(backup) },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displayDate,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (index == 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "최신",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (index < backups.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
