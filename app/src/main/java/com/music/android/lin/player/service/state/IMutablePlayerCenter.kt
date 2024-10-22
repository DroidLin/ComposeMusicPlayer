package com.music.android.lin.player.service.state

import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.MediaPlayerEvent
import com.harvest.musicplayer.PlayInfo
import com.harvest.musicplayer.PlayList
import com.harvest.musicplayer.PlayMode
import com.harvest.musicplayer.PlaybackState

/**
 * @author liuzhongao
 * @since 2023/10/20 1:12 AM
 */
interface IMutablePlayerCenter {

    val previousPlaybackState: PlaybackState

    val playbackState: PlaybackState

    val playList: PlayList?

    val mediaInfo: MediaInfo?

    val playMode: PlayMode

    fun updatePlayInfo(update: (PlayInfo) -> PlayInfo)

    fun emitPlayEvent(mediaPlayerEvent: MediaPlayerEvent)
}