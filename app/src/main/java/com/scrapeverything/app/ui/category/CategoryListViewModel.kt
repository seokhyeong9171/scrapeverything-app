package com.scrapeverything.app.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.repository.CategoryRepository
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
    val error: String? = null,
    // 다이얼로그 상태
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val editingCategory: CategoryWithCount? = null,
    val deletingCategory: CategoryWithCount? = null
)

sealed class CategoryListEvent {
    data class ShowSnackbar(val message: String) : CategoryListEvent()
}

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CategoryListEvent>()
    val event: SharedFlow<CategoryListEvent> = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            categoryRepository.ensureDefaultCategory()
        }
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryRepository.getAllCategories().collect { categories ->
                val categoriesWithCount = categories.map { entity ->
                    CategoryWithCount(
                        categoryId = entity.id,
                        categoryName = entity.name
                    )
                }
                _uiState.update {
                    it.copy(
                        categories = categoriesWithCount,
                        isLoading = false,
                        error = null
                    )
                }
                loadScrapCounts(categoriesWithCount)
            }
        }
    }

    private suspend fun loadScrapCounts(categories: List<CategoryWithCount>) {
        categories.forEach { category ->
            val count = categoryRepository.getScrapCount(category.categoryId)
            _uiState.update { state ->
                state.copy(
                    categories = state.categories.map {
                        if (it.categoryId == category.categoryId) {
                            it.copy(scrapCount = count)
                        } else it
                    }
                )
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
        _uiState.update {
            it.copy(
                showDeleteDialog = false,
                showDeleteConfirmDialog = false,
                deletingCategory = null
            )
        }
    }

    fun showDeleteConfirmDialog() {
        _uiState.update {
            it.copy(showDeleteDialog = false, showDeleteConfirmDialog = true)
        }
    }

    // CRUD 동작
    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryRepository.addCategory(name)
            dismissAddDialog()
            _event.emit(CategoryListEvent.ShowSnackbar("카테고리가 추가되었습니다"))
        }
    }

    fun updateCategory(id: Long, name: String) {
        viewModelScope.launch {
            categoryRepository.updateCategory(id, name)
            dismissEditDialog()
            _event.emit(CategoryListEvent.ShowSnackbar("카테고리가 수정되었습니다"))
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(id)
            dismissDeleteDialog()
            _event.emit(CategoryListEvent.ShowSnackbar("카테고리가 삭제되었습니다"))
        }
    }
}
