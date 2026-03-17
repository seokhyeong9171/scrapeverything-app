package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.local.db.entity.ScrapEntity
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

data class ScrapDetailUiState(
    val scrapDetail: ScrapEntity? = null,
    val categoryName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ScrapDetailEvent {
    data class ShowSnackbar(val message: String) : ScrapDetailEvent()
    object NavigateBack : ScrapDetailEvent()
}

@HiltViewModel
class ScrapDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scrapRepository: ScrapRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val scrapId: Long = savedStateHandle["scrapId"] ?: 0L

    fun getScrapId(): Long = scrapId

    private val _uiState = MutableStateFlow(ScrapDetailUiState())
    val uiState: StateFlow<ScrapDetailUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ScrapDetailEvent>()
    val event: SharedFlow<ScrapDetailEvent> = _event.asSharedFlow()

    init {
        loadScrapDetail()
    }

    fun loadScrapDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val scrap = scrapRepository.getScrapDetail(scrapId)
            if (scrap != null) {
                val category = categoryRepository.getCategoryById(scrap.categoryId)
                _uiState.update {
                    it.copy(
                        scrapDetail = scrap,
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

    fun deleteScrap() {
        viewModelScope.launch {
            scrapRepository.deleteScrap(scrapId)
            _event.emit(ScrapDetailEvent.NavigateBack)
        }
    }
}
