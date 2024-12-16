package com.music.android.lin.player.service.playlist

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.metadata.MediaListMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MediaPlayingList {

    private val mutableMetadata = MutableStateFlow(MediaListMetadata())
    val metadata = this.mutableMetadata.asStateFlow()

    private var mediaList: MediaList = ListLoop(this)
    var indexOfCurrentPosition: Int
        set(value) {
            this.mutableMetadata.update {
                it.copy(indexOfCurrentMediaInfo = value)
            }
        }
        get() = this.mutableMetadata.value.indexOfCurrentMediaInfo

    var mediaInfoPlayList: MediaInfoPlayList? = null

    val mediaInfo: MediaInfo?
        get() {
            val playList = this.mediaInfoPlayList ?: return null
            val currentPosition = this@MediaPlayingList.indexOfCurrentPosition
            if (currentPosition !in playList.mediaInfoList.indices) {
                return null
            }
            return playList.mediaInfoList[currentPosition]
        }

    fun tryGetPreviousMediaInfo(fromUser: Boolean): MediaInfo? {
        return this.mediaList.tryGetPreviousMediaInfo(fromUser)
    }

    fun tryGetNextMediaInfo(fromUser: Boolean): MediaInfo? {
        return this.mediaList.tryGetNextMediaInfo(fromUser)
    }

    fun setPlayMode(playMode: PlayMode) {
        this.mutableMetadata.update { it.copy(playMode = playMode) }
        this.mediaList = when (playMode) {
            PlayMode.PlayListLoop -> ListLoop(this)
            PlayMode.SingleLoop -> SingleLoop(this)
            PlayMode.Single -> Single(this)
            PlayMode.Shuffle -> Shuffle(this)
            else -> ListLoop(this)
        }
    }

    fun setResource(mediaInfoPlayList: MediaInfoPlayList, indexOfCurrentPosition: Int) {
        this.mediaInfoPlayList = mediaInfoPlayList
        this.indexOfCurrentPosition = indexOfCurrentPosition
        this.mutableMetadata.update {
            it.copy(
                mediaInfoPlayList = mediaInfoPlayList,
                mediaInfo = this.mediaInfo
            )
        }
    }

    internal fun syncCurrentMediaInfo() {
        this.mutableMetadata.update { it.copy(mediaInfo = this.mediaInfo) }
    }
}