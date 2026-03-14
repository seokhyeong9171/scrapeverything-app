package com.scrapeverything.app.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.repository.CategoryRepository
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

data class CategoryWithCount(
    val categoryId: Long,
    val categoryName: String,
    val scrapCount: Long = 0
)

data class CategoryListUiState(
    val categories: List<CategoryWithCount> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasNext: Boolean = false,
    // 다이얼로그 상태
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val editingCategory: CategoryWithCount? = null,
    val deletingCategory: CategoryWithCount? = null
)

sealed class CategoryListEvent {
    data class ShowSnackbar(val message: String) : CategoryListEvent()
    object NavigateToLogin : CategoryListEvent()
}

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CategoryListEvent>()
    val event: SharedFlow<CategoryListEvent> = _event.asSharedFlow()

    private var lastId: Long? = null

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lastId = null

            when (val result = categoryRepository.getCategories(null)) {
                is ApiResult.Success -> {
                    val data = result.data
                    lastId = data.nextCursorId
                    val categoriesWithCount = data.categories.map {
                        CategoryWithCount(it.categoryId, it.categoryName)
                    }
                    _uiState.update {
                        it.copy(
                            categories = categoriesWithCount,
                            isLoading = false,
                            hasNext = data.hasNext
                        )
                    }
                    loadScrapCounts(categoriesWithCount)
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

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasNext) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            when (val result = categoryRepository.getCategories(lastId)) {
                is ApiResult.Success -> {
                    val data = result.data
                    lastId = data.nextCursorId
                    val newCategories = data.categories.map {
                        CategoryWithCount(it.categoryId, it.categoryName)
                    }
                    _uiState.update {
                        it.copy(
                            categories = it.categories + newCategories,
                            isLoadingMore = false,
                            hasNext = data.hasNext
                        )
                    }
                    loadScrapCounts(newCategories)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
                is ApiResult.NetworkError -> {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            lastId = null

            when (val result = categoryRepository.getCategories(null)) {
                is ApiResult.Success -> {
                    val data = result.data
                    lastId = data.nextCursorId
                    val categoriesWithCount = data.categories.map {
                        CategoryWithCount(it.categoryId, it.categoryName)
                    }
                    _uiState.update {
                        it.copy(
                            categories = categoriesWithCount,
                            isRefreshing = false,
                            hasNext = data.hasNext,
                            error = null
                        )
                    }
                    loadScrapCounts(categoriesWithCount)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                    _event.emit(CategoryListEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                    _event.emit(CategoryListEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }

    fun refreshScrapCounts() {
        loadScrapCounts(_uiState.value.categories)
    }

    private fun loadScrapCounts(categories: List<CategoryWithCount>) {
        viewModelScope.launch {
            categories.forEach { category ->
                when (val result = categoryRepository.getScrapCount(category.categoryId)) {
                    is ApiResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                categories = state.categories.map {
                                    if (it.categoryId == category.categoryId) {
                                        it.copy(scrapCount = result.data.scrapCount)
                                    } else it
                                }
                            )
                        }
                    }
                    else -> { /* 스크랩 수 로딩 실패는 무시 */ }
                }
            }
        }
    }

    // 다이얼로그 표시/숨김
    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showEditDialog(category: CategoryWithCount) {
        _uiState.update { it.copy(showEditDialog = true, editingCategory = category) }
    }

    fun dismissEditDialog() {
        _uiState.update { it.copy(showEditDialog = false, editingCategory = null) }
    }

    fun showDeleteDialog(category: CategoryWithCount) {
        _uiState.update { it.copy(showDeleteDialog = true, deletingCategory = category) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, deletingCategory = null) }
    }

    // CRUD 동작
    fun addCategory(name: String) {
        viewModelScope.launch {
            when (val result = categoryRepository.addCategory(name)) {
                is ApiResult.Success -> {
                    dismissAddDialog()
                    refresh()
                    _event.emit(CategoryListEvent.ShowSnackbar("카테고리가 추가되었습니다"))
                }
                is ApiResult.Error -> {
                    _event.emit(CategoryListEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _event.emit(CategoryListEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }

    fun updateCategory(id: Long, name: String) {
        viewModelScope.launch {
            when (val result = categoryRepository.updateCategory(id, name)) {
                is ApiResult.Success -> {
                    dismissEditDialog()
                    _uiState.update { state ->
                        state.copy(
                            categories = state.categories.map {
                                if (it.categoryId == id) it.copy(categoryName = name) else it
                            }
                        )
                    }
                    _event.emit(CategoryListEvent.ShowSnackbar("카테고리가 수정되었습니다"))
                }
                is ApiResult.Error -> {
                    _event.emit(CategoryListEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _event.emit(CategoryListEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            when (val result = categoryRepository.deleteCategory(id)) {
                is ApiResult.Success -> {
                    dismissDeleteDialog()
                    _uiState.update { state ->
                        state.copy(
                            categories = state.categories.filter { it.categoryId != id }
                        )
                    }
                    _event.emit(CategoryListEvent.ShowSnackbar("카테고리가 삭제되었습니다"))
                }
                is ApiResult.Error -> {
                    dismissDeleteDialog()
                    _event.emit(CategoryListEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    dismissDeleteDialog()
                    _event.emit(CategoryListEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }
}
