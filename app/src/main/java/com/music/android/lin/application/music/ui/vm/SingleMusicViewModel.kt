package com.music.android.lin.application.music.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.state.DataLoadState
import com.music.android.lin.application.usecase.MusicItem
import com.music.android.lin.application.usecase.MusicItemSnapshot
import com.music.android.lin.application.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.player.metadata.CommonMediaResourceParameter
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayListType
import com.music.android.lin.player.service.controller.MediaController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

internal class SingleMusicViewModel(
    private val prepareMusicInfoUseCase: PrepareMusicInfoUseCase,
    private val mediaController: MediaController
) : ViewModel() {

    val musicInfoList = this.prepareMusicInfoUseCase.mediaItemList
        .map { state ->
            DataLoadState.Data(state)
        }
        .catch<DataLoadState> {
            it.printStackTrace()
            emit(DataLoadState.Failure(message = it.message ?: "", -1))
        }
        .onStart {
            emit(DataLoadState.Loading)
        }
        .stateIn(this.viewModelScope, SharingStarted.Lazily, DataLoadState.Loading)

    fun onMusicItemPressed(snapshot: MusicItemSnapshot, musicItem: MusicItem) {
        val commonResource = CommonMediaResourceParameter(
            playList = PlayList(
                id = "",
                type = PlayListType.LocalMusic,
                name = "",
                description = "",
                mediaInfoList = snapshot.mediaInfoList,
                extensions = null,
                updateTimeStamp = System.currentTimeMillis()
            ),
            startPosition = snapshot.musicItemList.indexOfFirst { it == musicItem }.coerceIn(0, snapshot.musicItemList.size - 1)
        )
        mediaController.playMediaResource(commonResource)
    }
}
