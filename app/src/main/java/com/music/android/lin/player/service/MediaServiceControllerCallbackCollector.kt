package com.harvest.musicplayer.service

import android.os.Binder
import android.os.Handler
import android.os.Looper
import com.music.android.lin.player.interfaces.MediaInfo
import com.music.android.lin.player.interfaces.MediaPlayerEvent
import com.music.android.lin.player.interfaces.MusicServicePlaybackState
import com.music.android.lin.player.interfaces.PlayInfo
import com.music.android.lin.player.interfaces.PlayList
import com.music.android.lin.player.interfaces.PlayMode
import com.music.android.lin.player.interfaces.PlaybackState
import com.music.android.lin.player.interfaces.PlayerEvent
import com.music.android.lin.player.service.MusicPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * @author liuzhongao
 * @since 2024/1/5 14:24
 */
internal class MediaServiceControllerCallbackCollector : Binder(), MusicPlayer.Listener {

    private val _internalState = MutableStateFlow(value = MusicServicePlaybackState())
    val flow = this._internalState.asStateFlow()

    private val _internalPlayerEventChannel = Channel<PlayerEvent>(capacity = Channel.Factory.UNLIMITED)
    val playerEvent = this._internalPlayerEventChannel.receiveAsFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val mainHandler = Handler(Looper.getMainLooper())
    private val updateTimer = object : Runnable {
        override fun run() {
            this@MediaServiceControllerCallbackCollector._internalState.update {
                val currentProgress = (it.currentProgress + 1000L).coerceIn(0L, it.currentDuration)
                it.copy(currentProgress = currentProgress)
            }
            this@MediaServiceControllerCallbackCollector.mainHandler.postDelayed(this, 1000L)
        }
    }

    override fun onPlayModeChanged(playMode: PlayMode) {
        this._internalState.update { it.copy(playMode = playMode) }
    }

    override fun onNewPlayList(playList: PlayList?) {
        this._internalState.update { it.copy(playList = playList) }
    }

    override fun onNewMusicInfo(mediaInfo: MediaInfo?) {
        this._internalState.update { it.copy(mediaInfo = mediaInfo) }
    }

    override fun onEvent(playerEvent: PlayerEvent) {
        when (playerEvent) {
//            MediaPlayerEvent.InitializeReady -> this.syncServicePlaybackState()
            MediaPlayerEvent.BufferingStart -> this._internalState.update { it.copy(isBuffering = true) }
            MediaPlayerEvent.BufferingEnd -> this._internalState.update { it.copy(isBuffering = false) }
            else -> {}
        }
        this._internalPlayerEventChannel.trySend(element = playerEvent)
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackState) {
        this._internalState.update { it.copy(playbackState = playbackState) }
        this._internalState.update {
            it.copy(
                playbackState = playbackState,
                isPlaying = playbackState == PlaybackState.Playing,
            )
        }
        if (playbackState == PlaybackState.Playing) {
            this.mainHandler.removeCallbacks(this.updateTimer)
            this.mainHandler.postDelayed(this.updateTimer, 500L)
        } else {
            this.mainHandler.removeCallbacks(this.updateTimer)
        }
    }

    override fun onSeekProgressChanged(progress: Long, duration: Long) {
        this._internalState.update {
            it.copy(
                currentProgress = progress,
                currentDuration = duration
            )
        }
    }

    override fun notifyInfo(code: Int, ext: Map<String, Any?>?) {
        super.notifyInfo(code, ext)
    }

    fun setPlaybackState(playInfo: PlayInfo) {
        this@MediaServiceControllerCallbackCollector._internalState.update {
            it.copy(
                playList = playInfo.playList,
                mediaInfo = playInfo.mediaInfo,
                playbackState = playInfo.playbackState,
                playMode = playInfo.playMode,
                playerType = playInfo.playerType,
                currentProgress = playInfo.currentProgress,
                currentDuration = playInfo.currentDuration.coerceAtLeast(0)
            )
        }
    }
}