package com.music.android.lin.application.music.play.ui.vm

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.music.play.ui.state.PlayerState
import com.music.android.lin.player.service.MediaService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope

@OptIn(ExperimentalCoroutinesApi::class)
@SuppressLint("StaticFieldLeak")
class PlayerPageViewModel(
    private val mediaService: MediaService,
) : ViewModel() {

    val playerState = flow {
        supervisorScope {
            val channel = produce {
                mediaService.information.collect { send(it) }
            }
            var currentProgress = mediaService.information.value.playerMetadata.contentProgress
            var currentDuration = mediaService.information.value.playerMetadata.contentDuration
            var isPlaying = mediaService.information.value.playerMetadata.isPlaying
            var currentMediaId: String? = mediaService.information.value.mediaInfo?.id
            var progress = if (currentDuration > 0L) {
                currentProgress / currentDuration.toFloat()
            } else 0f
            var error = false
            var uiState = PlayerState(progress, currentProgress, currentDuration)
            while (isActive && !error) {
                select<Unit> {
                    if (isPlaying && currentProgress <= uiState.currentDuration) {
                        onTimeout(500L) {
                            currentDuration = uiState.currentDuration.coerceAtLeast(0)
                            currentProgress = (currentProgress + 500L).coerceIn(0, currentDuration)
                            progress = if (currentDuration > 0L) {
                                currentProgress / currentDuration.toFloat()
                            } else 0f
                            uiState = uiState.copy(
                                progress = progress,
                                currentProgress = currentProgress
                            )
                            emit(uiState)
                        }
                    }
                    channel.onReceiveCatching { value ->
                        value
                            .onSuccess { information ->
                                currentDuration = information.playerMetadata.contentDuration.coerceAtLeast(0L)
                                currentProgress = if (information.mediaInfo?.id == currentMediaId) {
                                    information.playerMetadata.contentProgress.coerceIn(0, currentDuration)
                                } else 0L
                                progress = if (currentDuration > 0L) {
                                    currentProgress / currentDuration.toFloat()
                                } else 0f
                                currentMediaId = information.mediaInfo?.id
                                isPlaying = information.playerMetadata.isPlaying
                                uiState = uiState.copy(
                                    progress = progress,
                                    currentProgress = currentProgress,
                                    currentDuration = currentDuration
                                )
                                emit(uiState)
                            }
                            .onFailure { it?.printStackTrace(); error = true }
                    }
                }
            }
        }
    }
        .stateIn(this.viewModelScope, SharingStarted.Lazily, PlayerState())
}

