package com.scrapeverything.app.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.model.response.NoticeDetailResponse
import com.scrapeverything.app.data.model.response.NoticeItem
import com.scrapeverything.app.data.repository.NoticeRepository
import com.scrapeverything.app.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoticeUiState(
    val notices: List<NoticeItem> = emptyList(),
    val expandedNoticeIds: Set<Long> = emptySet(),
    val noticeDetails: Map<Long, NoticeDetailResponse> = emptyMap(),
    val loadingDetailIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val nextCursorId: Long? = null,
    val hasNext: Boolean = false
)

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoticeUiState())
    val uiState: StateFlow<NoticeUiState> = _uiState.asStateFlow()

    init {
        loadNotices()
    }

    fun loadNotices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = noticeRepository.getNotices(null)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notices = result.data.notices,
                            nextCursorId = result.data.nextCursorId,
                            hasNext = result.data.hasNext,
                            isLoading = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = "공지를 불러올 수 없습니다.")
                    }
                }
                is ApiResult.NetworkError -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = "네트워크 연결을 확인해주세요.")
                    }
                }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.hasNext || state.isLoadingMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            when (val result = noticeRepository.getNotices(state.nextCursorId)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notices = it.notices + result.data.notices,
                            nextCursorId = result.data.nextCursorId,
                            hasNext = result.data.hasNext,
                            isLoadingMore = false
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

    fun toggleNotice(noticeId: Long) {
        val currentExpanded = _uiState.value.expandedNoticeIds
        if (noticeId in currentExpanded) {
            _uiState.update {
                it.copy(expandedNoticeIds = currentExpanded - noticeId)
            }
        } else {
            _uiState.update {
                it.copy(expandedNoticeIds = currentExpanded + noticeId)
            }
            // 상세 내용을 아직 불러오지 않았으면 API 호출
            if (noticeId !in _uiState.value.noticeDetails) {
                loadNoticeDetail(noticeId)
            }
        }
    }

    private fun loadNoticeDetail(noticeId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loadingDetailIds = it.loadingDetailIds + noticeId)
            }
            when (val result = noticeRepository.getNoticeDetail(noticeId)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            noticeDetails = it.noticeDetails + (noticeId to result.data),
                            loadingDetailIds = it.loadingDetailIds - noticeId
                        )
                    }
                }
                is ApiResult.Error, is ApiResult.NetworkError -> {
                    _uiState.update {
                        it.copy(loadingDetailIds = it.loadingDetailIds - noticeId)
                    }
                }
            }
        }
    }
}
