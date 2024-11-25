package com.music.android.lin.application.music.play.ui.vm

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.usecase.util.toMusicItem
import com.music.android.lin.application.music.play.ui.state.PlayerUiState
import com.music.android.lin.player.service.MediaService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope

@OptIn(ExperimentalCoroutinesApi::class)
@SuppressLint("StaticFieldLeak")
class PlayerPageViewModel(
    private val context: Context,
    private val mediaService: MediaService,
) : ViewModel() {

    val playerState = flow {
        supervisorScope {
            val languageContext = ContextCompat.getContextForLanguage(context)
            val channel = produce {
                mediaService.information.collect { send(it) }
            }
            var uiState = PlayerUiState()
            var currentProgress = 0L
            var error = false
            while (isActive && !error) {
                select<Unit> {
                    if (uiState.isPlaying && currentProgress <= uiState.currentDuration) {
                        onTimeout(500L) {
                            val currentDuration = uiState.currentDuration.coerceAtLeast(0)
                            currentProgress = (currentProgress + 500L).coerceIn(0, currentDuration)
                            uiState = uiState.copy(currentProgress = currentProgress)
                            emit(uiState)
                        }
                    }
                    channel.onReceiveCatching { value ->
                        value
                            .onSuccess { information ->
                                val currentDuration = information.playerMetadata.contentDuration.coerceAtLeast(0L)
                                currentProgress = if (information.mediaInfo?.id == uiState.musicItem?.mediaId) {
                                    information.playerMetadata.contentProgress.coerceIn(0, currentDuration)
                                } else 0L
                                uiState = uiState.copy(
                                    musicItem = information.mediaInfo?.toMusicItem(languageContext),
                                    playMode = information.playMode,
                                    currentProgress = currentProgress,
                                    currentDuration = currentDuration,
                                    isPlaying = information.playerMetadata.isPlaying
                                )
                                emit(uiState)
                            }
                            .onFailure { it?.printStackTrace(); error = true }
                    }
                }
            }
        }
    }
        .stateIn(this.viewModelScope, SharingStarted.Lazily, PlayerUiState())
}

