package com.scrapeverything.app.ui.category

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scrapeverything.app.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scrapeverything.app.ui.component.ConfirmDialog
import com.scrapeverything.app.ui.component.EmptyView
import com.scrapeverything.app.ui.component.ErrorView
import com.scrapeverything.app.ui.component.FullScreenLoading
import com.scrapeverything.app.ui.component.ListBottomLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onNavigateToScrapList: (categoryId: Long, categoryName: String) -> Unit,
    onNavigateToMyPage: () -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // 메인 화면에서 뒤로가기 시 앱 종료
    val activity = LocalContext.current as? Activity
    BackHandler {
        activity?.finish()
    }

    // 이벤트 수신
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CategoryListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is CategoryListEvent.NavigateToLogin -> { /* 향후 구현 */ }
            }
        }
    }

    // 무한스크롤: 마지막 아이템 근처 도달 시 추가 로드
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3 && uiState.hasNext && !uiState.isLoadingMore
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = "앱 로고",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "조각모음",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToMyPage) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "마이페이지"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "카테고리 추가",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 최초 로딩
                uiState.isLoading -> {
                    FullScreenLoading()
                }
                // 에러
                uiState.error != null && uiState.categories.isEmpty() -> {
                    ErrorView(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadCategories() }
                    )
                }
                // 빈 상태
                uiState.categories.isEmpty() -> {
                    EmptyView(
                        icon = Icons.Outlined.FolderOpen,
                        message = "카테고리를 추가해보세요"
                    )
                }
                // 카테고리 리스트
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = uiState.categories,
                            key = { it.categoryId }
                        ) { category ->
                            CategoryItem(
                                category = category,
                                onClick = {
                                    onNavigateToScrapList(
                                        category.categoryId,
                                        category.categoryName
                                    )
                                },
                                onEdit = { viewModel.showEditDialog(category) },
                                onDelete = { viewModel.showDeleteDialog(category) }
                            )
                        }

                        // 추가 로딩 인디케이터
                        if (uiState.isLoadingMore) {
                            item {
                                ListBottomLoading()
                            }
                        }
                    }
                }
            }
        }
    }

    // 카테고리 추가 다이얼로그
    if (uiState.showAddDialog) {
        CategoryInputDialog(
            title = "카테고리 추가",
            confirmText = "추가",
            onConfirm = { name -> viewModel.addCategory(name) },
            onDismiss = { viewModel.dismissAddDialog() }
        )
    }

    // 카테고리 수정 다이얼로그
    if (uiState.showEditDialog && uiState.editingCategory != null) {
        CategoryInputDialog(
            title = "카테고리 수정",
            initialName = uiState.editingCategory!!.categoryName,
            confirmText = "수정",
            onConfirm = { name ->
                viewModel.updateCategory(uiState.editingCategory!!.categoryId, name)
            },
            onDismiss = { viewModel.dismissEditDialog() }
        )
    }

    // 카테고리 삭제 확인 다이얼로그
    if (uiState.showDeleteDialog && uiState.deletingCategory != null) {
        ConfirmDialog(
            title = "카테고리 삭제",
            message = "'${uiState.deletingCategory!!.categoryName}'을(를) 삭제하시겠습니까?",
            confirmText = "삭제",
            onConfirm = {
                viewModel.deleteCategory(uiState.deletingCategory!!.categoryId)
            },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }
}

@Composable
private fun CategoryItem(
    category: CategoryWithCount,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "스크랩 ${category.scrapCount}개",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "더보기",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "삭제",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
