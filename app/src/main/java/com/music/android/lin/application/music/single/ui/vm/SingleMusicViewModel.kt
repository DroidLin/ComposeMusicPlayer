package com.music.android.lin.application.music.single.ui.vm

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.withDataLoadState
import com.music.android.lin.application.common.usecase.MediaResourceGeneratorUseCase
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.common.usecase.MusicItemSnapshot
import com.music.android.lin.application.common.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.player.metadata.CommonPlayMediaResource
import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.PlayListType
import com.music.android.lin.player.service.controller.MediaController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Stable
internal class SingleMusicViewModel(
    private val prepareMusicInfoUseCase: PrepareMusicInfoUseCase,
    private val mediaResourceGeneratorUseCase: MediaResourceGeneratorUseCase,
    private val mediaController: MediaController,
) : ViewModel() {

    val musicInfoList = this.prepareMusicInfoUseCase.mediaItemList
        .withDataLoadState { it }
        .stateIn(this.viewModelScope, SharingStarted.Lazily, DataLoadState.Loading)

    fun onMusicItemPressed(snapshot: MusicItemSnapshot, musicItem: MusicItem) {
        val commonResource = CommonPlayMediaResource(
            mediaInfoPlayList = MediaInfoPlayList(
                id = "",
                type = PlayListType.LocalMusic,
                name = "",
                description = "",
                playListCover = "",
                mediaInfoList = snapshot.mediaInfoList,
                extensions = null,
                updateTimeStamp = System.currentTimeMillis(),
                countOfPlayable = snapshot.musicItemList.size
            ),
            startPosition = snapshot.musicItemList.indexOfFirst { it == musicItem }.coerceIn(0, snapshot.musicItemList.size - 1)
        )
        mediaController.playMediaResource(commonResource)
    }
}
