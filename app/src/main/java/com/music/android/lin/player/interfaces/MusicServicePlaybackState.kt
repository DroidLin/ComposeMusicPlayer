package com.music.android.lin.player.interfaces

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.metadata.PlaybackState
import com.music.android.lin.player.metadata.PlayerType

/**
 * @author liuzhongao
 * @since 2023/10/11 15:10
 */
data class MusicServicePlaybackState(
    val playList: PlayList? = null,
    val mediaInfo: MediaInfo? = null,
    val playbackState: PlaybackState = PlaybackState.Initialized,
    val isPlaying: Boolean = playbackState == PlaybackState.Playing,
    val isBuffering: Boolean = false,
    val playMode: PlayMode = PlayMode.PlayListLoop,
    val playerType: PlayerType = PlayerType.System,

    val currentProgress: Long = 0L,
    val currentDuration: Long = 0L
)
