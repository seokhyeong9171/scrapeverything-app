package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.model.response.ScrapDetailResponse
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

data class ScrapDetailUiState(
    val scrapDetail: ScrapDetailResponse? = null,
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
    private val scrapRepository: ScrapRepository
) : ViewModel() {

    private val scrapId: Long = savedStateHandle["scrapId"] ?: 0L

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

            when (val result = scrapRepository.getScrapDetail(scrapId)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(scrapDetail = result.data, isLoading = false)
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

    fun deleteScrap() {
        viewModelScope.launch {
            when (val result = scrapRepository.deleteScrap(scrapId)) {
                is ApiResult.Success -> {
                    _event.emit(ScrapDetailEvent.ShowSnackbar("스크랩이 삭제되었습니다"))
                    _event.emit(ScrapDetailEvent.NavigateBack)
                }
                is ApiResult.Error -> {
                    _event.emit(ScrapDetailEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _event.emit(ScrapDetailEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }
}
