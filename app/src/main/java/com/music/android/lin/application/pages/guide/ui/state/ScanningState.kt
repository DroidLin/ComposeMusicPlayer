package com.music.android.lin.application.pages.guide.ui.state

import androidx.compose.runtime.Stable
import com.music.android.lin.player.metadata.MediaInfo
import kotlinx.coroutines.Job

@Stable
sealed interface ScanningState {
    data object Init : ScanningState
    data class Loading(val runningJob: Job) : ScanningState
    data class Data(val mediaInfoList: List<MediaInfo>) : ScanningState
    data class Failure(val failureMessage: String) : ScanningState
    data object Complete : ScanningState
}