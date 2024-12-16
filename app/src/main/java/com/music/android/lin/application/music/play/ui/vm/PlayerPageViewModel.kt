package com.music.android.lin.application.music.play.ui.vm

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.music.play.ui.state.PlayerState
import com.music.android.lin.application.music.play.ui.util.pickColorAndTransformToScheme
import com.music.android.lin.player.notification.fetchBlurImageAsPainter
import com.music.android.lin.player.notification.fetchImageAsBitmap
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.PlayCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope
import java.util.logging.Logger

@OptIn(ExperimentalCoroutinesApi::class)
@SuppressLint("StaticFieldLeak")
class PlayerPageViewModel(
    private val mediaService: MediaService,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {

    private val sharedInputFlow = MutableSharedFlow<SliderBarInputRecord>(extraBufferCapacity = 1)

    val playerState = flow {
        supervisorScope {
            mediaService.runCommand(PlayCommand.SYNC_PLAY_METADATA) {}
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
            var uiState = PlayerState(progress, currentDuration)
            var handleInput: Boolean = false
            var currentCoverUrl: String? = null

            while (isActive && !error) {
                select<Unit> {
                    if (!handleInput && isPlaying && currentProgress <= uiState.currentDuration) {
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
                            )
                            emit(uiState)
                        }
                    }

                    // while receive play information from remote,
                    channel.onReceiveCatching { value ->
                        value
                            .onSuccess { information ->
                                // recalaulate playing progress.
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

                                val mediaCover = information.mediaInfo?.coverUri
                                var mediaCoverPainter: Painter =
                                    uiState.mediaCoverPainter ?: DefaultColorScheme.coverPainter
                                var mediaBackgroundPainter: Painter = uiState.mediaBackgroundPainter
                                    ?: DefaultColorScheme.backgroundPainter
                                var colorScheme: PlayerColorScheme = uiState.colorScheme
                                // if cover url has changed, we need to generate a new cover image and
                                // relaunch picking color.
                                if (currentCoverUrl != mediaCover) {
                                    val playColorScheme = fetchPlayerCoverAndColorScheme(mediaCover)
                                    mediaCoverPainter = playColorScheme.coverPainter
                                    mediaBackgroundPainter = playColorScheme.backgroundPainter
                                    colorScheme = playColorScheme.colorScheme
                                    currentCoverUrl = mediaCover
                                }

                                uiState = uiState.copy(
                                    progress = progress,
                                    currentDuration = currentDuration,
                                    mediaCoverPainter = mediaCoverPainter,
                                    mediaBackgroundPainter = mediaBackgroundPainter,
                                    colorScheme = colorScheme,
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
        .stateIn(
            ioViewModelScope,
            SharingStarted.Lazily,
            PlayerState()
        )

    fun handleSliderInput(progress: Float, handleInput: Boolean) {
        while (!sharedInputFlow.tryEmit(SliderBarInputRecord(progress, handleInput))) {
            this.logger.info("emit input failure.")
        }
    }

    private suspend fun fetchPlayerCoverAndColorScheme(imageCoverUrl: String?): PlayCoverScheme {
        if (imageCoverUrl.isNullOrEmpty()) return DefaultColorScheme
        return coroutineScope {
            val playCoverBitmap = fetchImageAsBitmap(imageCoverUrl)
            val playBackgroundPainter = fetchBlurImageAsPainter(imageCoverUrl)
            val colorScheme = pickColorAndTransformToScheme(playCoverBitmap)
            PlayCoverScheme(
                coverPainter = if (playCoverBitmap != null) {
                    BitmapPainter(playCoverBitmap.asImageBitmap())
                } else DefaultColorScheme.coverPainter,
                backgroundPainter = playBackgroundPainter ?: DefaultColorScheme.backgroundPainter,
                colorScheme = colorScheme
            )
        }
    }

    private data class PlayCoverScheme(
        val coverPainter: Painter,
        val backgroundPainter: Painter = coverPainter,
        val colorScheme: PlayerColorScheme = PlayerColorScheme(),
    )

    private data class SliderBarInputRecord(
        val progress: Float,
        val handleInput: Boolean,
    )

    companion object {
        private const val TIMEOUT_INTERVAL = 1000L
        private val DefaultColorScheme = PlayCoverScheme(
            coverPainter = ColorPainter(Color.Black),
        )
    }

}

