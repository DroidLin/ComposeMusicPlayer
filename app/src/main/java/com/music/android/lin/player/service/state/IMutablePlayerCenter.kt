package com.music.android.lin.player.service.state

import com.music.android.lin.player.interfaces.MediaInfo
import com.music.android.lin.player.interfaces.MediaPlayerEvent
import com.music.android.lin.player.interfaces.PlayInfo
import com.music.android.lin.player.interfaces.PlayList
import com.music.android.lin.player.interfaces.PlayMode
import com.music.android.lin.player.interfaces.PlaybackState

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