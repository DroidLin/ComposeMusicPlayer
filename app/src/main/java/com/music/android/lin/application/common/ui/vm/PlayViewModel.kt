package com.music.android.lin.application.common.ui.vm

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.usecase.MediaResourceGeneratorUseCase
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.common.usecase.util.toMusicItem
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
@Stable
class PlayViewModel(
    private val context: Context,
    private val mediaResourceGeneratorUseCase: MediaResourceGeneratorUseCase,
    private val mediaService: MediaService,
    private val mediaController: MediaController,
) : ViewModel() {

    private val languageContext by lazy { ContextCompat.getContextForLanguage(context) }

    val playState = mediaService.information
        .map { information ->
            PlayState(
                musicItem = information.mediaInfo?.toMusicItem(languageContext),
                playMode = information.playMode,
                isPlaying = information.playerMetadata.isPlaying,
            )
        }
        .stateIn(this.viewModelScope, SharingStarted.Lazily, PlayState())

    fun seekToPosition(position: Long) {
        mediaController.seekToPosition(position)
    }

    fun skipToPrev() {

    }

    fun skipToNext() {

    }

    fun playButtonPressed() {
        val isPlaying = this.mediaService.information.value.playerMetadata.isPlaying
        if (isPlaying) {
            this.mediaController.pause()
        } else {
            this.mediaController.playOrResume()
        }
    }

    fun startResource(playListId: String, musicItem: MusicItem) {
        viewModelScope.launch {
            val mediaResource =
                mediaResourceGeneratorUseCase.startPlayResource(playListId, musicItem.mediaId)
                    ?: return@launch
            mediaController.playMediaResource(mediaResource)
        }
    }

    fun startResource(uniqueId: String, mediaInfoList: List<MediaInfo>, musicItem: MusicItem) {
        val mediaResource =
            mediaResourceGeneratorUseCase.startPlayResource(uniqueId, mediaInfoList, musicItem)
        mediaController.playMediaResource(mediaResource)
    }

}