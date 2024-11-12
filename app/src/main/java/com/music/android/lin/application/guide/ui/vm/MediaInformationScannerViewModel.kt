package com.music.android.lin.application.guide.ui.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.guide.ui.state.ScannerUiState
import com.music.android.lin.application.guide.ui.state.ScanningState
import com.music.android.lin.application.settings.usecase.SaveMediaInfoUseCase
import com.music.android.lin.application.settings.usecase.ScanAndroidContentUseCase
import com.music.android.lin.application.settings.usecase.scanner.MediaContentScanner
import com.music.android.lin.player.metadata.MediaInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaInformationScannerViewModel(
    private val scanAndroidContentUseCase: ScanAndroidContentUseCase,
    private val saveMediaInfoUseCase: SaveMediaInfoUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState = this._uiState.asStateFlow()

    fun switchUseAndroidScanner(useAndroidScanner: Boolean) {
        this._uiState.update { it.copy(useAndroidScanner = useAndroidScanner) }
    }

    fun scannerLauncher() {
        val scanningJob = this.viewModelScope.launch {
            proceedScanner(scanAndroidContentUseCase::scanContentProvider)
        }
        this._uiState.update {
            val loadingState = ScanningState.Loading(scanningJob)
            it.copy(scanningState = loadingState)
        }
    }

    fun cancelScanning() {
        val scanningState = this._uiState.value.scanningState
        if (scanningState is ScanningState.Loading) {
            scanningState.runningJob.cancel()
        }
        this._uiState.update { it.copy(scanningState = ScanningState.Init) }
    }

    fun confirmSave(scanningState: ScanningState.Data) {
        viewModelScope.launch {
            saveMediaInfoUseCase.saveMediaInfo(scanningState.mediaInfoList)
        }.invokeOnCompletion {
            this._uiState.update { it.copy(scanningState = ScanningState.Complete) }
        }
    }

    private suspend fun proceedScanner(handle: suspend () -> List<MediaInfo>) {
        val dataResult = kotlin.runCatching { handle() }
        if (dataResult.isFailure) {
            this._uiState.update {
                it.copy(
                    scanningState = ScanningState.Failure(
                        failureMessage = dataResult.exceptionOrNull()?.message ?: ""
                    )
                )
            }
            return
        }
        this._uiState.update {
            it.copy(
                scanningState = ScanningState.Data(
                    dataResult.getOrNull() ?: emptyList()
                )
            )
        }
    }
}
