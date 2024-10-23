package com.music.android.lin.player.metadata

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/9 14:32
 */
data class PlayInfo(
    val playList: PlayList? = null,
    val mediaInfo: MediaInfo? = null,

    val isPlaying: Boolean = false,
    val currentDuration: Long = 0,
    val currentProgress: Long = 0,

    val previousPlaybackState: PlaybackState = PlaybackState.Initialized,
    val playbackState: PlaybackState = PlaybackState.Initialized,
    val playMode: PlayMode = PlayMode.PlayListLoop,
    val playerType: PlayerType = PlayerType.System,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5453066693866196628L

        val Default = PlayInfo(
            playList = null,
            mediaInfo = null,
            isPlaying = false,
            currentDuration = -1,
            currentProgress = -1,
            playbackState = PlaybackState.Released,
            playMode = PlayMode.Single,
            playerType = PlayerType.System
        )
    }
}
