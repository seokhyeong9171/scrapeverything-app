package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.local.SharedUrlHolder
import com.scrapeverything.app.data.local.db.entity.CategoryEntity
import com.scrapeverything.app.data.repository.CategoryRepository
import com.scrapeverything.app.data.repository.ScrapRepository
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

data class ScrapAddFromShareUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategory: CategoryEntity? = null,
    val scrapTitle: String = "",
    val url: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val isLoadingCategories: Boolean = true,
    val error: String? = null
)

sealed class ScrapAddFromShareEvent {
    data class ShowSnackbar(val message: String) : ScrapAddFromShareEvent()
    data class SaveSuccess(val categoryId: Long, val categoryName: String) : ScrapAddFromShareEvent()
}

@HiltViewModel
class ScrapAddFromShareViewModel @Inject constructor(
    private val sharedUrlHolder: SharedUrlHolder,
    private val categoryRepository: CategoryRepository,
    private val scrapRepository: ScrapRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ScrapAddFromShareUiState(url = sharedUrlHolder.consume() ?: "")
    )
    val uiState: StateFlow<ScrapAddFromShareUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ScrapAddFromShareEvent>()
    val event: SharedFlow<ScrapAddFromShareEvent> = _event.asSharedFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCategories = true, error = null) }
            val categories = categoryRepository.getAllCategoriesList()
            _uiState.update {
                it.copy(
                    categories = categories,
                    selectedCategory = categories.firstOrNull(),
                    isLoadingCategories = false
                )
            }
        }
    }

    fun onCategorySelected(category: CategoryEntity) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(scrapTitle = title) }
    }

    fun onUrlChanged(url: String) {
        _uiState.update { it.copy(url = url) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun saveScrap() {
        val state = _uiState.value
        if (state.selectedCategory == null) {
            viewModelScope.launch {
                _event.emit(ScrapAddFromShareEvent.ShowSnackbar("카테고리를 선택해주세요"))
            }
            return
        }
        if (state.scrapTitle.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapAddFromShareEvent.ShowSnackbar("제목을 입력해주세요"))
            }
            return
        }
        if (state.url.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapAddFromShareEvent.ShowSnackbar("URL을 입력해주세요"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            scrapRepository.addScrap(
                categoryId = state.selectedCategory.id,
                title = state.scrapTitle,
                url = state.url,
                description = state.description.ifBlank { null }
            )
            _uiState.update { it.copy(isSaving = false) }
            _event.emit(
                ScrapAddFromShareEvent.SaveSuccess(
                    categoryId = state.selectedCategory.id,
                    categoryName = state.selectedCategory.name
                )
            )
        }
    }
}
