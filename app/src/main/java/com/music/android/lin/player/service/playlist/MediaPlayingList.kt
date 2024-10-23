package com.music.android.lin.player.service.playlist

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode

/**
 * @author: liuzhongao
 * @since: 2024/10/23 21:34
 */
internal class MediaPlayingList : MediaList {

    private var mediaList: MediaList = ListLoopMediaList()
    private var indexOfCurrentPosition: Int = -1

    val currentPosition: Int get() = this.indexOfCurrentPosition

    override var playList: PlayList? = null

    override val mediaInfo: MediaInfo?
        get() {
            val playList = this.playList ?: return null
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
        this.mediaList = when (playMode) {
            PlayMode.PlayListLoop -> ListLoopMediaList()
            PlayMode.SingleLoop -> LoopMediaList()
            PlayMode.Single -> SingleMediaList()
            else -> ListLoopMediaList()
        }
    }

    fun setResource(playList: PlayList, indexOfCurrentPosition: Int) {
        this.playList = playList
        this.indexOfCurrentPosition = indexOfCurrentPosition
    }

    private inner class ListLoopMediaList : MediaList by this {
        override val prevMediaInfo: MediaInfo?
            get() {
                val playList = this.playList ?: return null
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
                return playList.mediaInfoList[newPosition]
            }
        override val nextMediaInfo: MediaInfo?
            get() {
                val playList = this.playList ?: return null
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
                return playList.mediaInfoList[newPosition]
            }
    }

    private inner class LoopMediaList : MediaList by this {
        override val prevMediaInfo: MediaInfo? get() = this.mediaInfo
        override val nextMediaInfo: MediaInfo? get() = this.mediaInfo
    }

    private inner class SingleMediaList : MediaList by this {
        override val nextMediaInfo: MediaInfo? = null
        override val prevMediaInfo: MediaInfo? = null
    }
}
