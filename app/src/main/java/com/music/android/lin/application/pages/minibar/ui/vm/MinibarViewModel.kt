package com.music.android.lin.application.pages.minibar.ui.vm

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.pages.minibar.ui.state.MinibarUiState
import com.music.android.lin.player.metadata.MediaType
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class MinibarViewModel(
    private val mediaService: MediaService,
) : ViewModel() {

    val uiState = this.mediaService.information
        .map { information ->
            MinibarUiState(
                mediaType = information.mediaInfo?.mediaType ?: MediaType.Unsupported,
                isPlaying = information.playerMetadata.isPlaying,
                imageUrl = information.mediaInfo?.coverUri,
                minibarTitle = information.mediaInfo?.mediaTitle ?: "",
                minibarDescription = information.mediaInfo?.let { mediaInfo ->
                    mediaInfo.artists.joinToString("/") { it.name } + " - " + mediaInfo.album.albumName
                } ?: ""
            )
        }
        .stateIn(this.ioViewModelScope, SharingStarted.Lazily, MinibarUiState())

}