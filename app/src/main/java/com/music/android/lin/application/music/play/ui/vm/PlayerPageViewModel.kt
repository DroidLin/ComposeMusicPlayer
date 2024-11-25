package com.music.android.lin.application.music.play.ui.vm

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.music.play.ui.state.PlayerState
import com.music.android.lin.application.music.play.ui.util.pickColorAndTransformToScheme
import com.music.android.lin.player.notification.fetchImageAsBitmap
import com.music.android.lin.player.notification.fetchImageAsPainter
import com.music.android.lin.player.service.MediaService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.MutableSharedFlow
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

    companion object {
        private const val TIMEOUT_INTERVAL = 1000L
    }

    private val sharedInputFlow = MutableSharedFlow<SliderBarInputRecord>(extraBufferCapacity = 1)

    val playerState = flow {
        supervisorScope {
            val channel = produce {
                mediaService.information.collect { send(it) }
            }
            val sliderInputChannel = produce {
                sharedInputFlow.collect { send(it) }
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
            var handleInput: Boolean = false
            var currentCoverUrl: String? = null

            while (isActive && !error) {
                select<Unit> {
                    if (isPlaying && currentProgress <= uiState.currentDuration) {
                        onTimeout(TIMEOUT_INTERVAL) {
                            currentDuration = uiState.currentDuration.coerceAtLeast(0)
                            currentProgress = (currentProgress + TIMEOUT_INTERVAL).coerceIn(0, currentDuration)
                            progress = when {
                                handleInput -> progress
                                currentDuration > 0L -> currentProgress / currentDuration.toFloat()
                                else -> 0f
                            }
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
                                progress = when {
                                    handleInput -> progress
                                    currentDuration > 0L -> currentProgress / currentDuration.toFloat()
                                    else -> 0f
                                }
                                currentMediaId = information.mediaInfo?.id
                                isPlaying = information.playerMetadata.isPlaying

                                val informationBitmap = information.mediaInfo?.coverUri

                                val (playerColorScheme, mediaCoverPainter) = if (currentCoverUrl != informationBitmap) {
                                    val bitmap = fetchImageAsBitmap(informationBitmap)
                                    if (bitmap != null) {
                                        pickColorAndTransformToScheme(bitmap) to BitmapPainter(bitmap.asImageBitmap())
                                    } else PlayerColorScheme() to null
                                } else uiState.colorScheme to uiState.mediaCoverPainter

                                currentCoverUrl = information.mediaInfo?.coverUri
                                uiState = uiState.copy(
                                    progress = progress,
                                    currentProgress = currentProgress,
                                    currentDuration = currentDuration,
                                    mediaCoverPainter = mediaCoverPainter,
                                    colorScheme = playerColorScheme
                                )
                                emit(uiState)
                            }
                            .onFailure { it?.printStackTrace(); error = true }
                    }
                    sliderInputChannel.onReceiveCatching { value ->
                        value
                            .onSuccess { inputRecord ->
                                handleInput = inputRecord.handleInput
                                progress = inputRecord.progress
                                if (progress < 0) {
                                    return@onSuccess
                                }
                                uiState = uiState.copy(progress = progress)
                                emit(uiState)
                            }
                            .onFailure { it?.printStackTrace(); error = true }
                    }
                }
            }
        }
    }
        .stateIn(this.viewModelScope, SharingStarted.Lazily, PlayerState())

    fun handleSliderInput(progress: Float, handleInput: Boolean) {
        while (!sharedInputFlow.tryEmit(SliderBarInputRecord(progress, handleInput))) {
        }
    }

    private data class SliderBarInputRecord(
        val progress: Float,
        val handleInput: Boolean,
    )
}

