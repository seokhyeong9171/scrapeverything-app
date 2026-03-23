package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.local.AiSummarizer
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

data class ScrapAddUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategory: CategoryEntity? = null,
    val scrapTitle: String = "",
    val url: String = "",
    val summary: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val isLoadingCategories: Boolean = true,
    val isGeneratingSummary: Boolean = false,
    val isGeneratingDescription: Boolean = false
)

sealed class ScrapAddEvent {
    data class ShowSnackbar(val message: String) : ScrapAddEvent()
    object SaveSuccess : ScrapAddEvent()
}

@HiltViewModel
class ScrapAddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val scrapRepository: ScrapRepository,
    private val aiSummarizer: AiSummarizer
) : ViewModel() {

    private val initialCategoryId: Long = savedStateHandle["categoryId"] ?: 0L

    private val _uiState = MutableStateFlow(ScrapAddUiState())
    val uiState: StateFlow<ScrapAddUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ScrapAddEvent>()
    val event: SharedFlow<ScrapAddEvent> = _event.asSharedFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCategories = true) }
            val categories = categoryRepository.getAllCategoriesList()
            val selected = categories.find { it.id == initialCategoryId }
                ?: categories.firstOrNull()
            _uiState.update {
                it.copy(
                    categories = categories,
                    selectedCategory = selected,
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

    fun onSummaryChanged(summary: String) {
        _uiState.update { it.copy(summary = summary) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun generateDescription() {
        val url = _uiState.value.url
        if (url.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapAddEvent.ShowSnackbar("URL을 입력해주세요"))
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingDescription = true) }
            val oneLiner = aiSummarizer.generateOneLiner(url)
            if (oneLiner != null) {
                _uiState.update { it.copy(summary = oneLiner, isGeneratingDescription = false) }
            } else {
                _uiState.update { it.copy(isGeneratingDescription = false) }
                _event.emit(ScrapAddEvent.ShowSnackbar("설명 생성에 실패했습니다"))
            }
        }
    }

    fun generateSummary() {
        val url = _uiState.value.url
        if (url.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapAddEvent.ShowSnackbar("URL을 입력해주세요"))
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingSummary = true) }
            val summary = aiSummarizer.summarize(url)
            if (summary != null) {
                _uiState.update { it.copy(description = summary, isGeneratingSummary = false) }
            } else {
                _uiState.update { it.copy(isGeneratingSummary = false) }
                _event.emit(ScrapAddEvent.ShowSnackbar("요약 생성에 실패했습니다"))
            }
        }
    }

    fun saveScrap() {
        val state = _uiState.value
        if (state.selectedCategory == null) {
            viewModelScope.launch {
                _event.emit(ScrapAddEvent.ShowSnackbar("카테고리를 선택해주세요"))
            }
            return
        }
        if (state.scrapTitle.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapAddEvent.ShowSnackbar("제목을 입력해주세요"))
            }
            return
        }
        if (state.url.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapAddEvent.ShowSnackbar("URL을 입력해주세요"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            scrapRepository.addScrap(
                categoryId = state.selectedCategory.id,
                title = state.scrapTitle,
                url = state.url,
                summary = state.summary.ifBlank { null },
                description = state.description.ifBlank { null }
            )
            _uiState.update { it.copy(isSaving = false) }
            _event.emit(ScrapAddEvent.SaveSuccess)
        }
    }
}
