package com.harvest.musicplayer

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
