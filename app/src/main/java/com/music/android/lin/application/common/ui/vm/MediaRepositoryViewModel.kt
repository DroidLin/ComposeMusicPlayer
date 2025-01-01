package com.music.android.lin.application.common.ui.vm

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.R
import com.music.android.lin.application.common.model.PlayListItem
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.withDataLoadState
import com.music.android.lin.application.common.usecase.AddMediaItemToPlayListUseCase
import com.music.android.lin.application.common.usecase.CreatePlayListUseCase
import com.music.android.lin.application.common.usecase.DeleteMediaInfoUseCase
import com.music.android.lin.application.common.usecase.FetchMyPlayListUseCase
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.pages.music.component.CreatePlayListParameter
import com.music.android.lin.player.metadata.MediaInfo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Stable
@SuppressLint("StaticFieldLeak")
class MediaRepositoryViewModel(
    private val context: Context,
    private val deleteMediaInfoUseCase: DeleteMediaInfoUseCase,
    private val fetchMyPlayListUseCase: FetchMyPlayListUseCase,
    private val addMediaItemToPlayListUseCase: AddMediaItemToPlayListUseCase,
    private val createPlayListUseCase: CreatePlayListUseCase,
) : ViewModel() {

    val playList = this.fetchMyPlayListUseCase.mediaInfoPlayListFlow
        .withDataLoadState { list ->
            val languageContext = ContextCompat.getContextForLanguage(this.context)
            list.map { playList ->
                PlayListItem(
                    id = playList.id,
                    name = playList.name,
                    description = languageContext.getString(
                        R.string.string_playlist_description,
                        playList.countOfPlayable.toString()
                    ),
                    playListCover = playList.playListCover ?: ""
                )
            }.toImmutableList()
        }
        .stateIn(this.ioViewModelScope, SharingStarted.Lazily, DataLoadState.Loading)

    suspend fun deleteMediaInfo(mediaInfoId: String): Boolean {
        return this.deleteMediaInfoUseCase.deleteMediaInfo(mediaInfoId)
    }

    suspend fun deleteMediaInfo(mediaInfo: MediaInfo): Boolean {
        return this.deleteMediaInfoUseCase.deleteMediaInfo(mediaInfo)
    }

    suspend fun doAddToPlayList(playListItem: PlayListItem, musicItem: MusicItem) {
        this.addMediaItemToPlayListUseCase.addToPlayList(playListItem, musicItem)
    }

    suspend fun doAddToPlayList(playListId: String, mediaInfoIdList: List<String>): Boolean {
        return kotlin.runCatching {
            addMediaItemToPlayListUseCase.addToPlayList(
                playListId = playListId,
                mediaInfoIdList = mediaInfoIdList
            )
        }.isSuccess
    }

    suspend fun createPlayList(createPlayListParameter: CreatePlayListParameter) {
        this.createPlayListUseCase.createPlayList(createPlayListParameter)
    }
}