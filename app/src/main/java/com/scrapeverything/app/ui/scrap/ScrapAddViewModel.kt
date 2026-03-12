package com.scrapeverything.app.ui.scrap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class ScrapAddUiState(
    val scrapTitle: String = "",
    val url: String = "",
    val description: String = "",
    val isSaving: Boolean = false
)

sealed class ScrapAddEvent {
    data class ShowSnackbar(val message: String) : ScrapAddEvent()
    object SaveSuccess : ScrapAddEvent()
}

@HiltViewModel
class ScrapAddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scrapRepository: ScrapRepository
) : ViewModel() {

    private val categoryId: Long = savedStateHandle["categoryId"] ?: 0L

    private val _uiState = MutableStateFlow(ScrapAddUiState())
    val uiState: StateFlow<ScrapAddUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ScrapAddEvent>()
    val event: SharedFlow<ScrapAddEvent> = _event.asSharedFlow()

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

            when (val result = scrapRepository.addScrap(
                categoryId = categoryId,
                title = state.scrapTitle,
                url = state.url,
                description = state.description.ifBlank { null }
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _event.emit(ScrapAddEvent.SaveSuccess)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _event.emit(ScrapAddEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _event.emit(ScrapAddEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }
        }
    }
}
