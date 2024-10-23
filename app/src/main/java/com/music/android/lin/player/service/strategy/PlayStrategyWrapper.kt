package com.music.android.lin.player.service.strategy

import com.harvest.musicplayer.service.PlayStrategy
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.state.IMutablePlayerCenter

/**
 * @author liuzhongao
 * @since 2023/10/19 1:25 AM
 */
internal class PlayStrategyWrapper(
    private val mutablePlayerCenter: IMutablePlayerCenter
) : PlayStrategy {
    private var innerPlayStrategy: PlayStrategy = PlayStrategy

    override val playList: PlayList?
        get() = this.mutablePlayerCenter.playList
    override val mediaInfo: MediaInfo?
        get() = this.mutablePlayerCenter.mediaInfo

    val playMode: PlayMode
        get() = this.mutablePlayerCenter.playMode

    @Synchronized
    override fun previousMusicInfo(): MediaInfo? = this.innerPlayStrategy.previousMusicInfo()

    @Synchronized
    override fun nextMusicInfo(): MediaInfo? = this.innerPlayStrategy.nextMusicInfo()

    private fun create(playMode: PlayMode): PlayStrategy {
        return when (playMode) {
            PlayMode.Single -> SinglePlayStrategy()
            PlayMode.SingleLoop -> SingleLoopPlayStrategy()
            PlayMode.PlayListNoLoop -> PlayListNoLoopPlayStrategy()
            PlayMode.PlayListLoop -> PlayListLoopPlayStrategy()
            PlayMode.Shuffle -> ShufflePlayStrategy()
            else -> SinglePlayStrategy()
        }
    }

    fun switchPlayMode(playMode: PlayMode) {
        this.innerPlayStrategy = create(playMode = playMode)
        this.mutablePlayerCenter.updatePlayInfo { it.copy(playMode = playMode) }
    }

    fun setPlayListAndMusicInfo(playList: PlayList?, mediaInfo: MediaInfo?) {
        this.mutablePlayerCenter.updatePlayInfo {
            it.copy(
                playList = playList,
                mediaInfo = mediaInfo
            )
        }
    }

    fun setPlayList(playList: PlayList?) {
        this.mutablePlayerCenter.updatePlayInfo { it.copy(playList = playList) }
    }

    fun setMusicInfo(mediaInfo: MediaInfo?) {
        this.mutablePlayerCenter.updatePlayInfo { it.copy(mediaInfo = mediaInfo) }
    }

    private inner class PlayListLoopPlayStrategy : PlayStrategy {
        override val playList: PlayList?
            get() = this@PlayStrategyWrapper.playList
        override val mediaInfo: MediaInfo?
            get() = this@PlayStrategyWrapper.mediaInfo

        override fun previousMusicInfo(): MediaInfo? {
            val playList = this.playList ?: return null
            val musicInfo = this.mediaInfo ?: return null

            val playListMusicInfoList = playList.mediaInfoList
            val musicCountInPlayList = playListMusicInfoList.size
            val currentPosition = playListMusicInfoList.indexOf(musicInfo)
                .takeIf { it in 0 until musicCountInPlayList } ?: return null
            var nextPosition = currentPosition - 1
            if (nextPosition !in 0 until musicCountInPlayList) {
                nextPosition += musicCountInPlayList
                nextPosition %= musicCountInPlayList
            }

            val nextMusicInfo = playListMusicInfoList[nextPosition]
            this@PlayStrategyWrapper.setPlayList(playList)
            this@PlayStrategyWrapper.setMusicInfo(nextMusicInfo)
            return nextMusicInfo
        }

        override fun nextMusicInfo(): MediaInfo? {
            val playList = this.playList ?: return null
            val musicInfo = this.mediaInfo ?: return null

            val playListMusicInfoList = playList.mediaInfoList
            val musicCountInPlayList = playListMusicInfoList.size
            val currentPosition = playListMusicInfoList.indexOf(musicInfo)
                .takeIf { it in 0 until musicCountInPlayList } ?: return null
            var nextPosition = currentPosition + 1
            if (nextPosition !in 0 until musicCountInPlayList) {
                nextPosition += musicCountInPlayList
                nextPosition %= musicCountInPlayList
            }

            val nextMusicInfo = playListMusicInfoList[nextPosition]
            this@PlayStrategyWrapper.setPlayList(playList)
            this@PlayStrategyWrapper.setMusicInfo(nextMusicInfo)
            return nextMusicInfo
        }
    }

    private inner class PlayListNoLoopPlayStrategy : PlayStrategy {
        override val playList: PlayList?
            get() = this@PlayStrategyWrapper.playList
        override val mediaInfo: MediaInfo?
            get() = this@PlayStrategyWrapper.mediaInfo

        /**
         * 列表内循环，头部向前、尾部向后会返回null
         * 不会取模保持在区间范围内
         */
        override fun previousMusicInfo(): MediaInfo? {
            val playList = this.playList ?: return null
            val musicInfo = this.mediaInfo ?: return null

            val playListMusicInfoList = playList.mediaInfoList
            val musicCountInPlayList = playListMusicInfoList.size
            val currentPosition = playListMusicInfoList.indexOf(musicInfo)
                .takeIf { it in 0 until musicCountInPlayList } ?: return null
            val nextPosition = currentPosition + 1
            if (nextPosition !in 0 until musicCountInPlayList) {
                return null
            }

            val nextMusicInfo = playListMusicInfoList[nextPosition]
            this@PlayStrategyWrapper.setPlayList(playList)
            this@PlayStrategyWrapper.setMusicInfo(nextMusicInfo)
            return nextMusicInfo
        }

        /**
         * 列表内循环，头部向前、尾部向后会返回null
         * 不会取模保持在区间范围内
         */
        override fun nextMusicInfo(): MediaInfo? {
            val playList = this.playList ?: return null
            val musicInfo = this.mediaInfo ?: return null

            val playListMusicInfoList = playList.mediaInfoList
            val musicCountInPlayList = playListMusicInfoList.size
            val currentPosition = playListMusicInfoList.indexOf(musicInfo)
                .takeIf { it in 0 until musicCountInPlayList } ?: return null
            val nextPosition = currentPosition + 1
            if (nextPosition !in 0 until musicCountInPlayList) {
                return null
            }

            val nextMusicInfo = playListMusicInfoList[nextPosition]
            this@PlayStrategyWrapper.setPlayList(playList)
            this@PlayStrategyWrapper.setMusicInfo(nextMusicInfo)
            return nextMusicInfo
        }
    }

    private inner class ShufflePlayStrategy : PlayStrategy {
        override val playList: PlayList?
            get() = this@PlayStrategyWrapper.playList
        override val mediaInfo: MediaInfo?
            get() = this@PlayStrategyWrapper.mediaInfo

        override fun previousMusicInfo(): MediaInfo? = prepareMusicInfo()

        override fun nextMusicInfo(): MediaInfo? = prepareMusicInfo()

        private fun prepareMusicInfo(): MediaInfo? {
            val playList = this.playList ?: return null
            val musicInfo = this.mediaInfo ?: return null

            val playListMusicInfoList = playList.mediaInfoList
            val musicCountInPlayList = playListMusicInfoList.size
            val currentPosition = playListMusicInfoList.indexOf(musicInfo)
                .takeIf { it in 0 until musicCountInPlayList } ?: return null
            var nextPosition = currentPosition + (Math.random() * musicCountInPlayList).toInt()
            if (nextPosition !in 0 until musicCountInPlayList) {
                nextPosition += musicCountInPlayList
                nextPosition %= musicCountInPlayList
            }

            val nextMusicInfo = playListMusicInfoList[nextPosition]
            this@PlayStrategyWrapper.setPlayList(playList)
            this@PlayStrategyWrapper.setMusicInfo(nextMusicInfo)
            return nextMusicInfo
        }
    }

    private inner class SinglePlayStrategy : PlayStrategy {
        override val playList: PlayList?
            get() = this@PlayStrategyWrapper.playList
        override val mediaInfo: MediaInfo?
            get() = this@PlayStrategyWrapper.mediaInfo

        override fun previousMusicInfo(): MediaInfo? = null

        override fun nextMusicInfo(): MediaInfo? = null
    }

    private inner class SingleLoopPlayStrategy : PlayStrategy {
        override val playList: PlayList?
            get() = this@PlayStrategyWrapper.playList
        override val mediaInfo: MediaInfo?
            get() = this@PlayStrategyWrapper.mediaInfo

        override fun previousMusicInfo(): MediaInfo? = mediaInfo

        override fun nextMusicInfo(): MediaInfo? = mediaInfo
    }
}