package com.music.android.lin.application.music.play.ui.vm

import androidx.lifecycle.ViewModel
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.MediaService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlayerListViewModel(
    private val mediaService: MediaService
) : ViewModel() {

    val uiState = this.mediaService.information
        .mapLatest { information ->
            PlayerListUiState(
                playList = information.mediaInfoPlayList,
                playMode = information.playMode
            )
        }
        .distinctUntilChanged()
        .stateIn(ioViewModelScope, SharingStarted.Lazily, PlayerListUiState())

}

internal data class PlayerListUiState(
    val playList: PlayList? = null,
    val playMode: PlayMode = PlayMode.PlayListLoop
)