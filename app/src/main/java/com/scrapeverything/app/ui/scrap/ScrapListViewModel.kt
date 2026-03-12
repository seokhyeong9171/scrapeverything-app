package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.model.response.ScrapItem
import com.scrapeverything.app.data.repository.ScrapRepository
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

data class ScrapListUiState(
    val categoryId: Long = 0,
    val categoryName: String = "",
    val scraps: List<ScrapItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasNext: Boolean = false
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

    private var lastId: Long? = null

    init {
        loadScraps()
    }

    fun loadScraps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lastId = null

            when (val result = scrapRepository.getScrapsByCategory(categoryId)) {
                is ApiResult.Success -> {
                    val data = result.data
                    lastId = data.nextCursorId
                    _uiState.update {
                        it.copy(
                            scraps = data.scraps,
                            isLoading = false,
                            hasNext = data.hasNext
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = ErrorMessages.getMessage(result.code))
                    }
                }
                is ApiResult.NetworkError -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = "네트워크 연결을 확인해주세요")
                    }
                }
            }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasNext) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            when (val result = scrapRepository.getScrapsByCategory(categoryId, lastId)) {
                is ApiResult.Success -> {
                    val data = result.data
                    lastId = data.nextCursorId
                    _uiState.update {
                        it.copy(
                            scraps = it.scraps + data.scraps,
                            isLoadingMore = false,
                            hasNext = data.hasNext
                        )
                    }
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

            when (val result = scrapRepository.getScrapsByCategory(categoryId)) {
                is ApiResult.Success -> {
                    val data = result.data
                    lastId = data.nextCursorId
                    _uiState.update {
                        it.copy(
                            scraps = data.scraps,
                            isRefreshing = false,
                            hasNext = data.hasNext,
                            error = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                    _event.emit(ScrapListEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                    _event.emit(ScrapListEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
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
            when (val result = scrapRepository.deleteScrap(scrapId)) {
                is ApiResult.Success -> {
                    _uiState.update { state ->
                        state.copy(scraps = state.scraps.filter { it.scrapId != scrapId })
                    }
                    _event.emit(ScrapListEvent.ShowSnackbar("스크랩이 삭제되었습니다"))
                }
                is ApiResult.Error -> {
                    _event.emit(ScrapListEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _event.emit(ScrapListEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }
}
