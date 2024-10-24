package com.music.android.lin.player.service

import android.app.Service
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import com.music.android.lin.player.audiofocus.PlayerAudioFocusManager
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaPlayerEvent
import com.music.android.lin.player.metadata.PlayInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.metadata.PlaybackState
import com.music.android.lin.player.metadata.PlayerEvent
import com.music.android.lin.player.notification.PlayNotificationManager
import com.music.android.lin.player.notification.android.PlayMediaSession
import com.music.android.lin.player.service.controller.MediaControllerImpl
import com.music.android.lin.player.service.controller.MusicPlayerListenerWrapper
import com.music.android.lin.player.service.metadata.MediaConfiguration
import com.music.android.lin.player.service.player.MusicPlayer
import com.music.android.lin.player.service.state.IMutablePlayerCenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 所有操作均可放到主线程做，包括io等耗时操作在内
 *
 * @author liuzhongao
 * @since 2023/10/8 8:41 PM
 */
class PlayerService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val musicPlayerListenerWrapper = MusicPlayerListenerWrapper()
    private val servicePlayerInfoMutableFlow = MutableStateFlow(value = PlayInfo())
    private val playInfo: PlayInfo get() = this.servicePlayerInfoMutableFlow.value

    private val servicePlayerEventChannel = Channel<MediaPlayerEvent>()

    private val serviceMutablePlayerCenter = object : IMutablePlayerCenter {
        override val previousPlaybackState: PlaybackState
            get() = this@PlayerService.playInfo.previousPlaybackState
        override val playbackState: PlaybackState
            get() = this@PlayerService.playInfo.playbackState
        override val playList: PlayList?
            get() = this@PlayerService.playInfo.playList
        override val mediaInfo: MediaInfo?
            get() = this@PlayerService.playInfo.mediaInfo
        override val playMode: PlayMode
            get() = this@PlayerService.playInfo.playMode

        override fun updatePlayInfo(update: (PlayInfo) -> PlayInfo) {
            this@PlayerService.servicePlayerInfoMutableFlow.update(update)
        }

        override fun emitPlayEvent(mediaPlayerEvent: MediaPlayerEvent) {
            this@PlayerService.coroutineScope.launch {
                this@PlayerService.servicePlayerEventChannel.send(element = mediaPlayerEvent)
            }
        }
    }

    private val handleNextMediaListener = object : MusicPlayer.Listener {
        override fun onEvent(playerEvent: PlayerEvent) {
            if (playerEvent == MediaPlayerEvent.MediaPlayEnd) {
                musicController.skipToNext(fromUser = false)
            }
        }
    }

    private val musicController by lazy {
        MediaControllerImpl(
            context = this,
            mutablePlayerCenter = this.serviceMutablePlayerCenter,
            playInfoGetter = this::playInfo
        )
    }

    override fun onCreate() {
        super.onCreate()

        this.musicPlayerListenerWrapper.addListener(listener = this.handleNextMediaListener)
        this.coroutineScope.consumeState(
            playInfo = this.servicePlayerInfoMutableFlow,
            playerEventChannel = this.servicePlayerEventChannel,
            musicPlayerListenerWrapper = this.musicPlayerListenerWrapper
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        val mediaListener = intent?.extras?.getBinder(MediaServiceImpl.KEY_GLOBAL_LISTENER) as? MusicPlayer.Listener
        if (mediaListener != null) {
            this.musicPlayerListenerWrapper.removeListener(mediaListener)
            this.musicPlayerListenerWrapper.addListener(mediaListener)
        }
        return this.musicController
    }

    override fun onDestroy() {
        super.onDestroy()
        this.coroutineScope.cancel()
        this.musicController.release()
    }
}

private fun CoroutineScope.consumeState(
    playInfo: StateFlow<PlayInfo>,
    playerEventChannel: Channel<MediaPlayerEvent>,
    musicPlayerListenerWrapper: MusicPlayerListenerWrapper,
) {
    launch {
        playerEventChannel.receiveAsFlow()
            .collect(musicPlayerListenerWrapper::onEvent)
    }
    launch {
        playInfo.map { it.playbackState }
            .distinctUntilChanged()
            .collect(musicPlayerListenerWrapper::onPlaybackStateChanged)
    }
    launch {
        playInfo.map { it.playList }
            .distinctUntilChanged()
            .collect(musicPlayerListenerWrapper::onNewPlayList)
    }
    launch {
        playInfo.map { it.mediaInfo }
            .distinctUntilChanged()
            .collect(musicPlayerListenerWrapper::onNewMusicInfo)
    }
    launch {
        playInfo.map { it.playMode }
            .distinctUntilChanged()
            .collect(musicPlayerListenerWrapper::onPlayModeChanged)
    }
    launch {
        playInfo.map { it.currentProgress to it.currentDuration }
            .distinctUntilChanged()
            .collect { (progress, duration) ->
                musicPlayerListenerWrapper.onSeekProgressChanged(progress, duration)
            }
    }
}