package com.music.android.lin.player.service.player

import android.net.Uri
import android.view.Surface
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.metadata.PlaybackState
import com.music.android.lin.player.metadata.PlayerEvent

/**
 * @author liuzhongao
 * @since 2023/10/8 10:15 PM
 */
internal interface MusicPlayer {

    val currentMusicInfoDuration: Long

    val currentProgress: Long

    fun setSurface(surface: Surface?)

    fun setDataSource(uri: Uri)

    /**
     * 准备资源 + 自动播放
     */
    fun prepareWithAutoStart()

    /**
     * 准备资源，不会自动播放
     */
    fun prepareWithoutStart(onPrepared: () -> Unit = {})

    fun play()

    fun pause()

    fun stop()

    fun seekToPosition(position: Long)

    fun reset()

    fun release()

    interface Listener {

        fun onPlayModeChanged(playMode: PlayMode) {}

        fun onNewPlayList(playList: PlayList?) {}

        fun onNewMusicInfo(mediaInfo: MediaInfo?) {}

        fun onEvent(playerEvent: PlayerEvent) {}

        fun onPlaybackStateChanged(playbackState: PlaybackState) {}

        fun onSeekProgressChanged(progress: Long, duration: Long) {}

        fun notifyInfo(code: Int, ext: Map<String, Any?>? = null) {}
    }
}