package com.scrapeverything.app.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrapeverything.app.data.repository.BackupRepository
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

data class BackupRestoreUiState(
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val showBackupConfirmDialog: Boolean = false,
    val showRestoreConfirmDialog: Boolean = false
)

sealed class BackupRestoreEvent {
    data class ShowSnackbar(val message: String) : BackupRestoreEvent()
}

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupRestoreUiState())
    val uiState: StateFlow<BackupRestoreUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<BackupRestoreEvent>()
    val event: SharedFlow<BackupRestoreEvent> = _event.asSharedFlow()

    fun showBackupConfirmDialog() {
        _uiState.update { it.copy(showBackupConfirmDialog = true) }
    }

    fun dismissBackupConfirmDialog() {
        _uiState.update { it.copy(showBackupConfirmDialog = false) }
    }

    fun showRestoreConfirmDialog() {
        _uiState.update { it.copy(showRestoreConfirmDialog = true) }
    }

    fun dismissRestoreConfirmDialog() {
        _uiState.update { it.copy(showRestoreConfirmDialog = false) }
    }

    fun backup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBackingUp = true, showBackupConfirmDialog = false) }

            when (val result = backupRepository.backup()) {
                is ApiResult.Success -> {
                    _event.emit(BackupRestoreEvent.ShowSnackbar("백업이 완료되었습니다"))
                }
                is ApiResult.Error -> {
                    _event.emit(BackupRestoreEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _event.emit(BackupRestoreEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }

            _uiState.update { it.copy(isBackingUp = false) }
        }
    }

    fun restore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true, showRestoreConfirmDialog = false) }

            when (val result = backupRepository.restore()) {
                is ApiResult.Success -> {
                    val r = result.data
                    if (r.addedCategories == 0 && r.addedScraps == 0) {
                        _event.emit(BackupRestoreEvent.ShowSnackbar("새로 추가된 데이터가 없습니다"))
                    } else {
                        _event.emit(BackupRestoreEvent.ShowSnackbar(
                            "복원 완료: 카테고리 ${r.addedCategories}개, 스크랩 ${r.addedScraps}개 추가"
                        ))
                    }
                }
                is ApiResult.Error -> {
                    _event.emit(BackupRestoreEvent.ShowSnackbar(ErrorMessages.getMessage(result.code)))
                }
                is ApiResult.NetworkError -> {
                    _event.emit(BackupRestoreEvent.ShowSnackbar("네트워크 연결을 확인해주세요"))
                }
            }

            _uiState.update { it.copy(isRestoring = false) }
        }
    }
}
