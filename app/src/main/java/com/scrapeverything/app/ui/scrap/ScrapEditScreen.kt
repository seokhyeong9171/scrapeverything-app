package com.scrapeverything.app.ui.scrap

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scrapeverything.app.ui.component.ErrorView
import com.scrapeverything.app.ui.component.FullScreenLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapEditScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit = onNavigateBack,
    viewModel: ScrapEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ScrapEditEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ScrapEditEvent.SaveSuccess -> {
                    onSaveSuccess()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "스크랩 수정",
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

        when {
            uiState.isLoading -> {
                FullScreenLoading(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorView(
                    message = uiState.error!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // 카테고리 표시
                    Text(
                        text = "카테고리: ${uiState.categoryName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 제목
                    OutlinedTextField(
                        value = uiState.scrapTitle,
                        onValueChange = { viewModel.onTitleChanged(it) },
                        label = { Text("제목") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // URL
                    OutlinedTextField(
                        value = uiState.url,
                        onValueChange = { viewModel.onUrlChanged(it) },
                        label = { Text("URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI 요약 생성 버튼
                    OutlinedButton(
                        onClick = { viewModel.generateSummary() },
                        enabled = !uiState.isGeneratingSummary && !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isGeneratingSummary) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("요약 생성 중...")
                        } else {
                            Icon(
                                Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI 요약 생성")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 상세정보
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.onDescriptionChanged(it) },
                        label = { Text("상세정보 (선택)") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 저장 버튼
                    Button(
                        onClick = { viewModel.saveScrap() },
                        enabled = !uiState.isSaving && !uiState.isGeneratingSummary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("저장")
                    }
                }
            }
        }
    }
}
