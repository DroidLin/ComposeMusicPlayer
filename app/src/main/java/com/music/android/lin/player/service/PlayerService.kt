package com.harvest.musicplayer.service

import android.content.Intent
import android.os.IBinder
import com.harvest.common.services.KServiceFacade
import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.MediaPlayerEvent
import com.harvest.musicplayer.PlayInfo
import com.harvest.musicplayer.PlayList
import com.harvest.musicplayer.PlayMode
import com.harvest.musicplayer.PlaybackState
import com.harvest.musicplayer.PlayerEvent
import com.harvest.musicplayer.audiofocus.PlayerAudioFocusManager
import com.harvest.musicplayer.notification.PlayNotificationManager
import com.harvest.musicplayer.notification.android.PlayMediaSession
import com.harvest.musicplayer.service.controller.MediaControllerImpl
import com.harvest.musicplayer.service.controller.MusicPlayerListenerWrapper
import com.harvest.musicplayer.service.metadata.MediaConfiguration
import com.harvest.musicplayer.service.state.IMutablePlayerCenter
import com.harvest.statistic.interfaces.IStatistic
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
class PlayerService : AbstractPlayerService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val musicPlayerListenerWrapper = MusicPlayerListenerWrapper()
    private val servicePlayerInfoMutableFlow = MutableStateFlow(value = PlayInfo())
    private val playInfo: PlayInfo get() = this.servicePlayerInfoMutableFlow.value

    private val servicePlayerEventChannel = Channel<MediaPlayerEvent>()
    private val iStatisticService: IStatistic get() = KServiceFacade[IStatistic::class.java]

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
            this@PlayerService.iStatisticService.logInfo(
                "PlayService",
                "kind", "sendPlayEvent",
                "event", mediaPlayerEvent
            )
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

    private val musicConfigurationHelper by lazy { MusicConfigurationHelper(context = this) }
    private val musicController by lazy {
        MediaControllerImpl(
            context = this,
            mutablePlayerCenter = this.serviceMutablePlayerCenter,
            playInfoGetter = this::playInfo
        )
    }
    private val playNotificationManager by lazy {
        PlayNotificationManager(context = this, mediaController = this.musicController)
    }
    private val playMediaSession by lazy {
        PlayMediaSession(context = this, mediaController = this.musicController)
    }
    private val playAudioFocusManager by lazy {
        PlayerAudioFocusManager(context = this, mediaController = this.musicController)
    }

    override fun onCreate() {
        super.onCreate()
        this.coroutineScope.launch {
            val mediaConfig = this@PlayerService.musicConfigurationHelper.getConfiguration()
            this@PlayerService.musicController.init(mediaConfig)
        }

        this.playNotificationManager.initialize(service = this)
        this.playNotificationManager.setMediaSessionToken(token = this.playMediaSession.mediaSessionToken)
        this.musicPlayerListenerWrapper.addListener(listener = this.handleNextMediaListener)
        this.musicConfigurationHelper.setConfigurationCollector {
            val playInfo = this.playInfo
            MediaConfiguration(
                playList = playInfo.playList,
                mediaInfo = playInfo.mediaInfo,
                playingProgress = playInfo.currentProgress,
                playingDuration = playInfo.currentDuration,
                playMode = playInfo.playMode,
                playerType = playInfo.playerType
            )
        }
        this.coroutineScope.consumeState(
            playInfo = this.servicePlayerInfoMutableFlow,
            playerEventChannel = this.servicePlayerEventChannel,
            playNotificationManager = this.playNotificationManager,
            playMediaSession = this.playMediaSession,
            playAudioFocusManager = this.playAudioFocusManager,
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
        this.playNotificationManager.release()
        this.musicController.release()
        this.playAudioFocusManager.release()
    }
}

private fun CoroutineScope.consumeState(
    playInfo: StateFlow<PlayInfo>,
    playerEventChannel: Channel<MediaPlayerEvent>,
    playNotificationManager: PlayNotificationManager,
    playMediaSession: PlayMediaSession,
    playAudioFocusManager: PlayerAudioFocusManager,
    musicPlayerListenerWrapper: MusicPlayerListenerWrapper,
) {
    launch {
        playInfo.collect { playInfo ->
            playNotificationManager.updateMediaNotification(playInfo = playInfo)
            playMediaSession.updateMediaSessionInfo(playInfo = playInfo)
        }
    }
    launch {
        playInfo.map { it.playbackState == PlaybackState.Playing }
            .distinctUntilChanged()
            .collect(playAudioFocusManager::onPlaybackStateChanged)
    }
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