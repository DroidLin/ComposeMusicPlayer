package com.harvest.musicplayer.service

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList

/**
 * @author liuzhongao
 * @since 2023/10/9 14:30
 */
internal interface PlayStrategy {

    /**
     * 当前正在播放的播放列表信息
     */
    val playList: PlayList?

    /**
     * 当前正在播放的歌曲信息
     */
    val mediaInfo: MediaInfo?

    fun previousMusicInfo(): MediaInfo?

    fun nextMusicInfo(): MediaInfo?

    companion object Null : PlayStrategy {
        override val playList: PlayList? = null
        override val mediaInfo: MediaInfo? = null
        override fun previousMusicInfo(): MediaInfo? = null
        override fun nextMusicInfo(): MediaInfo? = null
    }
}