package com.scrapeverything.app.ui.scrap

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scrapeverything.app.BuildConfig
import com.scrapeverything.app.ui.component.ConfirmDialog
import com.scrapeverything.app.ui.component.ErrorView
import com.scrapeverything.app.ui.component.FullScreenLoading
import com.scrapeverything.app.ui.component.OgPreviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapDetailScreen(
    onNavigateBack: () -> Unit,
    onDeleteSuccess: () -> Unit,
    onNavigateToEdit: (scrapId: Long) -> Unit = {},
    viewModel: ScrapDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // 수정 화면에서 돌아왔을 때 데이터 갱신
    LifecycleResumeEffect(Unit) {
        viewModel.loadScrapDetail()
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ScrapDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ScrapDetailEvent.NavigateBack -> {
                    onDeleteSuccess()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.scrapDetail?.title ?: "스크랩 상세",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                actions = {
                    if (uiState.scrapDetail != null) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    Icons.Rounded.MoreVert,
                                    contentDescription = "메뉴"
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("수정") },
                                    onClick = {
                                        showMenu = false
                                        onNavigateToEdit(viewModel.getScrapId())
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Edit, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("공유하기") },
                                    onClick = {
                                        showMenu = false
                                        val detail = uiState.scrapDetail!!
                                        val shareText = buildShareText(
                                            title = detail.title,
                                            url = detail.url,
                                            description = detail.description
                                        )
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(
                                            Intent.createChooser(sendIntent, "공유하기")
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Share, contentDescription = null)
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("삭제", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                FullScreenLoading(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorView(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadScrapDetail() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.scrapDetail != null -> {
                val detail = uiState.scrapDetail!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // 카테고리
                    Text(
                        text = uiState.categoryName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 제목
                    Text(
                        text = detail.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // URL 카드
                    Card(
                        onClick = {
                            try {
                                val urlStr = if (detail.url.startsWith("http://") || detail.url.startsWith("https://")) {
                                    detail.url
                                } else {
                                    "https://${detail.url}"
                                }
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                // 브라우저를 열 수 없는 경우 무시
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = detail.url,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Outlined.OpenInBrowser,
                                contentDescription = "브라우저에서 열기",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // OG 미리보기
                    if (detail.ogTitle != null || detail.ogDescription != null || detail.ogImageUrl != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OgPreviewCard(
                            ogTitle = detail.ogTitle,
                            ogDescription = detail.ogDescription,
                            ogImageUrl = detail.ogImageUrl
                        )
                    }

                    // 설명
                    if (!detail.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "메모",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = detail.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 날짜 정보
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "생성일: ${formatTimestamp(detail.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "수정일: ${formatTimestamp(detail.updatedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        ConfirmDialog(
            title = "스크랩 삭제",
            message = "이 스크랩을 삭제하시겠습니까?",
            confirmText = "삭제",
            onConfirm = {
                viewModel.deleteScrap()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

private fun buildShareText(title: String, url: String, description: String?): String {
    val serverUrl = BuildConfig.SERVER_URL.trimEnd('/')
    val encodedTitle = Uri.encode(title)
    val encodedUrl = Uri.encode(url)
    val encodedDesc = Uri.encode(description ?: "")
    val shareLink = "$serverUrl/share?title=$encodedTitle&url=$encodedUrl&desc=$encodedDesc"

    return buildString {
        appendLine("[조각모음] $title")
        appendLine(url)
        if (!description.isNullOrBlank()) {
            appendLine(description)
        }
        appendLine()
        appendLine("▶ 조각모음에서 바로 저장하기:")
        appendLine(shareLink)
        appendLine()
        appendLine("앱이 없다면 설치하기:")
        append("https://play.google.com/store/apps/details?id=com.scrapeverything.app")
    }
}

private fun formatTimestamp(millis: Long): String {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        sdf.format(java.util.Date(millis))
    } catch (e: Exception) {
        millis.toString()
    }
}
