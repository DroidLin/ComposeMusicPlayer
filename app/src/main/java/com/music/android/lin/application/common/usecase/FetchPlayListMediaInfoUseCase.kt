package com.music.android.lin.application.common.usecase

import android.content.Context
import androidx.core.content.ContextCompat
import com.music.android.lin.application.common.usecase.util.toMusicItem
import com.music.android.lin.player.database.MediaRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map

class FetchPlayListMediaInfoUseCase(
    private val context: Context,
    private val mediaRepository: MediaRepository,
    playlistId: String,
) {

    val mediaInfo = this.mediaRepository.observablePlayListMediaInfo(playListId = playlistId)
        .map { mediaInfoList ->
            val languageContext = ContextCompat.getContextForLanguage(this.context)
            val musicItemList = mediaInfoList.map { value ->
                value.toMusicItem(languageContext)
            }
            MusicItemSnapshot(
                musicItemList = musicItemList.toImmutableList(),
                mediaInfoList = mediaInfoList
            )
        }
}