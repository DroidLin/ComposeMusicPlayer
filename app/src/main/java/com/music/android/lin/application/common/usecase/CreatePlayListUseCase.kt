package com.music.android.lin.application.common.usecase

import android.os.SystemClock
import com.music.android.lin.application.music.component.CreatePlayListParameter
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayListType

class CreatePlayListUseCase(
    private val mediaRepository: MediaRepository
) {

    suspend fun createPlayList(createPlayListParameter: CreatePlayListParameter) {
        val playList = MediaInfoPlayList(
            id = "user_generated_playlist_${SystemClock.elapsedRealtime()}_${createPlayListParameter.name.hashCode()}",
            type = PlayListType.LocalMusic,
            name = createPlayListParameter.name,
            playListCover = createPlayListParameter.cover,
            countOfPlayable = 0,
            description = "",
            mediaInfoList = emptyList(),
            extensions = null,
            updateTimeStamp = System.currentTimeMillis()
        )
        mediaRepository.insertPlayList(playList)
    }
}