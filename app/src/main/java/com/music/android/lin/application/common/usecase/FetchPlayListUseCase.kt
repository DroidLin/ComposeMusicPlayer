package com.music.android.lin.application.common.usecase

import android.content.Context
import androidx.core.content.ContextCompat
import com.music.android.lin.R
import com.music.android.lin.application.common.model.MediaInfoPlayListItem
import com.music.android.lin.application.common.usecase.util.toMusicItem
import com.music.android.lin.player.database.MediaRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map

class FetchPlayListUseCase(
    private val context: Context,
    private val mediaRepository: MediaRepository,
    playListId: String
) {

    val playList = this.mediaRepository.observableMediaInfoPlayList(playListId)
        .map { playList ->
            if (playList != null) {
                val languageContext = ContextCompat.getContextForLanguage(this.context)
                MediaInfoPlayListItem(
                    id = playList.id,
                    name = playList.name,
                    description = languageContext.getString(
                        R.string.string_playlist_description,
                        playList.countOfPlayable.toString()
                    ),
                    playListCover = playList.playListCover ?: "",
                    musicInfoList = playList.mediaInfoList.map { mediaInfo ->
                        mediaInfo.toMusicItem(languageContext)
                    }.toImmutableList()
                )
            } else null
        }
}