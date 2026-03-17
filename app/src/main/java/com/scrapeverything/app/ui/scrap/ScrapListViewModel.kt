package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.local.db.entity.ScrapEntity
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

data class ScrapListUiState(
    val categoryId: Long = 0,
    val categoryName: String = "",
    val scraps: List<ScrapEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ScrapListEvent {
    data class ShowSnackbar(val message: String) : ScrapListEvent()
}

@HiltViewModel
class ScrapListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scrapRepository: ScrapRepository
) : ViewModel() {

    private val categoryId: Long = savedStateHandle["categoryId"] ?: 0L
    private val categoryName: String = savedStateHandle["categoryName"] ?: ""

    private val _uiState = MutableStateFlow(
        ScrapListUiState(categoryId = categoryId, categoryName = categoryName)
    )
    val uiState: StateFlow<ScrapListUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ScrapListEvent>()
    val event: SharedFlow<ScrapListEvent> = _event.asSharedFlow()

    init {
        observeScraps()
    }

    private fun observeScraps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            scrapRepository.getScrapsByCategory(categoryId).collect { scraps ->
                _uiState.update {
                    it.copy(
                        scraps = scraps,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun showMessage(message: String) {
        viewModelScope.launch {
            _event.emit(ScrapListEvent.ShowSnackbar(message))
        }
    }

    fun deleteScrap(scrapId: Long) {
        viewModelScope.launch {
            scrapRepository.deleteScrap(scrapId)
            _event.emit(ScrapListEvent.ShowSnackbar("스크랩이 삭제되었습니다"))
        }
    }
}
