package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class ScrapEditUiState(
    val scrapTitle: String = "",
    val url: String = "",
    val description: String = "",
    val categoryId: Long? = null,
    val categoryName: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class ScrapEditEvent {
    data class ShowSnackbar(val message: String) : ScrapEditEvent()
    object SaveSuccess : ScrapEditEvent()
}

@HiltViewModel
class ScrapEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scrapRepository: ScrapRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val scrapId: Long = savedStateHandle["scrapId"] ?: 0L

    private val _uiState = MutableStateFlow(ScrapEditUiState())
    val uiState: StateFlow<ScrapEditUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ScrapEditEvent>()
    val event: SharedFlow<ScrapEditEvent> = _event.asSharedFlow()

    init {
        loadScrapDetail()
    }

    private fun loadScrapDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val scrap = scrapRepository.getScrapDetail(scrapId)
            if (scrap != null) {
                val category = categoryRepository.getCategoryById(scrap.categoryId)
                _uiState.update {
                    it.copy(
                        scrapTitle = scrap.title,
                        url = scrap.url,
                        description = scrap.description ?: "",
                        categoryId = scrap.categoryId,
                        categoryName = category?.name ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = "스크랩을 찾을 수 없습니다")
                }
            }
        }
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
        if (state.scrapTitle.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapEditEvent.ShowSnackbar("제목을 입력해주세요"))
            }
            return
        }
        if (state.url.isBlank()) {
            viewModelScope.launch {
                _event.emit(ScrapEditEvent.ShowSnackbar("URL을 입력해주세요"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            scrapRepository.updateScrap(
                scrapId = scrapId,
                categoryId = state.categoryId,
                title = state.scrapTitle,
                url = state.url,
                description = state.description.ifBlank { null }
            )
            _uiState.update { it.copy(isSaving = false) }
            _event.emit(ScrapEditEvent.SaveSuccess)
        }
    }
}
