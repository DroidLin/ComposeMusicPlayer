package com.music.android.lin.application.common.usecase

import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.metadata.CommonPlayMediaResource
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.MediaResource
import com.music.android.lin.player.metadata.PlayListType
import com.music.android.lin.player.metadata.PositionalPlayMediaResource
import com.music.android.lin.player.service.MediaService

class MediaResourceGeneratorUseCase(
    private val mediaService: MediaService,
    private val mediaRepository: MediaRepository,
) {

    suspend fun startPlayResource(playListId: String, mediaInfoId: String): MediaResource? {
        val currentPlayList = this.mediaService.information.value.mediaInfoPlayList
        return if (
            currentPlayList != null &&
            currentPlayList.id == playListId &&
            currentPlayList.mediaInfoList.indexOfFirst { it.id == mediaInfoId } >= 0
        ) {
            PositionalPlayMediaResource(
                startPosition = currentPlayList.mediaInfoList.indexOfFirst { mediaInfoId == it.id }
                    .coerceIn(0, currentPlayList.mediaInfoList.size - 1)
            )
        } else {
            val playList = mediaRepository.queryPlayList(playListId) ?: return null
            CommonPlayMediaResource(
                mediaInfoPlayList = playList,
                startPosition = playList.mediaInfoList.indexOfFirst { mediaInfoId == it.id }
                    .coerceIn(0, playList.mediaInfoList.size - 1)
            )
        }
    }

    fun startPlayResource(
        uniqueId: String,
        mediaInfoList: List<MediaInfo>,
        musicItem: MusicItem,
    ): MediaResource {
        val currentPlayList = this.mediaService.information.value.mediaInfoPlayList
        return if (
            currentPlayList != null &&
            currentPlayList.id == uniqueId &&
            currentPlayList.mediaInfoList.indexOfFirst { it.id == musicItem.mediaId } >= 0 &&
            currentPlayList.mediaInfoList == mediaInfoList
        ) {
            PositionalPlayMediaResource(
                startPosition = currentPlayList.mediaInfoList.indexOfFirst { it.id == musicItem.mediaId }
            )
        } else {
            CommonPlayMediaResource(
                mediaInfoPlayList = MediaInfoPlayList(
                    id = uniqueId,
                    type = PlayListType.LocalMusic,
                    name = "",
                    description = "",
                    playListCover = "",
                    mediaInfoList = mediaInfoList,
                    extensions = null,
                    updateTimeStamp = System.currentTimeMillis(),
                    countOfPlayable = mediaInfoList.size
                ),
                startPosition = mediaInfoList.indexOfFirst { it.id == musicItem.mediaId }
                    .coerceIn(0, mediaInfoList.size - 1)
            )
        }
    }
}