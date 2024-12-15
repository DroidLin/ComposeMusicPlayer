package com.music.android.lin.player.service.playlist

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.metadata.MediaListMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @author: liuzhongao
 * @since: 2024/10/23 21:34
 */
class MediaPlayingList internal constructor() : MediaList {

    private val mutableMetadata = MutableStateFlow(MediaListMetadata())
    val metadata = this.mutableMetadata.asStateFlow()

    private var mediaList: MediaList = ListLoopMediaList()
    private var indexOfCurrentPosition: Int
        set(value) {
            this.mutableMetadata.update {
                it.copy(indexOfCurrentMediaInfo = value)
            }
        }
        get() = this.mutableMetadata.value.indexOfCurrentMediaInfo

    override var mediaInfoPlayList: MediaInfoPlayList? = null

    override val mediaInfo: MediaInfo?
        get() {
            val playList = this.mediaInfoPlayList ?: return null
            val currentPosition = this@MediaPlayingList.indexOfCurrentPosition
            if (currentPosition !in playList.mediaInfoList.indices) {
                return null
            }
            return playList.mediaInfoList[currentPosition]
        }

    override val prevMediaInfo: MediaInfo?
        get() = this.mediaList.prevMediaInfo
    override val nextMediaInfo: MediaInfo?
        get() = this.mediaList.nextMediaInfo

    fun setPlayMode(playMode: PlayMode) {
        this.mutableMetadata.update { it.copy(playMode = playMode) }
        this.mediaList = when (playMode) {
            PlayMode.PlayListLoop -> ListLoopMediaList()
            PlayMode.SingleLoop -> LoopMediaList()
            PlayMode.Single -> SingleMediaList()
            PlayMode.Shuffle -> SingleMediaList()
            else -> ListLoopMediaList()
        }
    }

    fun setResource(mediaInfoPlayList: MediaInfoPlayList, indexOfCurrentPosition: Int) {
        this.mediaInfoPlayList = mediaInfoPlayList
        this.indexOfCurrentPosition = indexOfCurrentPosition
        this.syncCurrentMediaInfo()
        this.mutableMetadata.update { it.copy(mediaInfoPlayList = mediaInfoPlayList) }
    }

    private fun syncCurrentMediaInfo() {
        this.mutableMetadata.update { it.copy(mediaInfo = this.mediaInfo) }
    }

    private inner class ListLoopMediaList : MediaListParent(this) {
        override val prevMediaInfo: MediaInfo?
            get() {
                val playList = this.mediaInfoPlayList ?: return null
                val currentPosition = this@MediaPlayingList.indexOfCurrentPosition
                if (currentPosition !in playList.mediaInfoList.indices) {
                    return null
                }
                var newPosition = currentPosition - 1
                if (newPosition !in playList.mediaInfoList.indices) {
                    newPosition += playList.mediaInfoList.size
                }
                newPosition %= playList.mediaInfoList.size
                this@MediaPlayingList.indexOfCurrentPosition = newPosition
                this@MediaPlayingList.syncCurrentMediaInfo()
                return playList.mediaInfoList[newPosition]
            }
        override val nextMediaInfo: MediaInfo?
            get() {
                val playList = this.mediaInfoPlayList ?: return null
                val currentPosition = this@MediaPlayingList.indexOfCurrentPosition
                if (currentPosition !in playList.mediaInfoList.indices) {
                    return null
                }
                var newPosition = currentPosition + 1
                if (newPosition !in playList.mediaInfoList.indices) {
                    newPosition += playList.mediaInfoList.size
                }
                newPosition %= playList.mediaInfoList.size
                this@MediaPlayingList.indexOfCurrentPosition = newPosition
                this@MediaPlayingList.syncCurrentMediaInfo()
                return playList.mediaInfoList[newPosition]
            }
    }

    private inner class LoopMediaList : MediaListParent(this) {
        override val prevMediaInfo: MediaInfo? get() = this.mediaInfo
        override val nextMediaInfo: MediaInfo? get() = this.mediaInfo
    }

    private inner class SingleMediaList : MediaListParent(this) {
        override val nextMediaInfo: MediaInfo? = null
        override val prevMediaInfo: MediaInfo? = null
    }

    internal abstract class MediaListParent(private val rawMediaList: MediaList) : MediaList {
        final override val mediaInfoPlayList: MediaInfoPlayList? get() = this.rawMediaList.mediaInfoPlayList
        override val mediaInfo: MediaInfo? get() = this.rawMediaList.mediaInfo
    }
}
