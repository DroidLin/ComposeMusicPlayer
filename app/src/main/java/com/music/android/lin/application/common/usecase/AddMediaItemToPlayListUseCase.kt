package com.music.android.lin.application.common.usecase

import com.music.android.lin.application.common.model.PlayListItem
import com.music.android.lin.player.database.MediaRepository

class AddMediaItemToPlayListUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend fun addToPlayList(playListItem: PlayListItem, musicItem: MusicItem) {
        mediaRepository.addMediaInfoToPlayList(
            playListId = playListItem.id,
            mediaInfoId = musicItem.mediaId
        )
    }

    suspend fun addToPlayList(playListId: String, mediaInfoIdList: List<String>) {
        mediaRepository.addMediaInfoToPlayList(
            playListId = playListId,
            mediaInfoIdList = mediaInfoIdList
        )
    }
}