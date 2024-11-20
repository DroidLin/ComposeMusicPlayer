package com.music.android.lin.player

import android.app.Service
import android.content.Context
import android.os.Handler
import com.music.android.lin.modules.AppIdentifier
import com.music.android.lin.player.audiofocus.PlayerAudioFocusManager
import com.music.android.lin.player.notification.PlayMediaSession
import com.music.android.lin.player.notification.PlayNotificationManager
import com.music.android.lin.player.service.RemoteMediaServiceProxy
import com.music.android.lin.player.service.controller.BizMediaController
import com.music.android.lin.player.service.controller.BizPlayInfo
import com.music.android.lin.player.service.controller.MediaController
import com.music.android.lin.player.service.controller.PlayEventLoop
import com.music.android.lin.player.service.controller.PlayEventLoopHost
import com.music.android.lin.player.service.controller.PlayInfo
import com.music.android.lin.player.service.controller.ProxyMediaController
import com.music.android.lin.player.service.player.ExoMediaPlayer
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.player.datasource.DataSource
import com.music.android.lin.player.service.playlist.MediaPlayingList
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single
import java.util.logging.Logger

@Module
class PlayModule {

    @Single
    @Qualifier(name = PlayerIdentifier.playerLogger)
    fun logger(): Logger = Logger.getLogger("PlayerLogger")

    @Single
    @Qualifier(name = PlayerIdentifier.exoPlayer3)
    fun exoPlayer(
        @Qualifier(name = PlayerIdentifier.playServiceHandlerThread)
        handler: Handler,
        @Qualifier(name = PlayerIdentifier.playerLogger)
        logger: Logger,
    ): Player = ExoMediaPlayer(handler, logger)

    @Single
    @Qualifier(name = PlayerIdentifier.proxyMediaController)
    fun proxyMediaController(playEventLoop: PlayEventLoop): MediaController =
        ProxyMediaController(playEventLoop::dispatchMessage)

    @Single
    @Qualifier(name = PlayerIdentifier.bizMediaController)
    internal fun bizMediaController(
        @Qualifier(name = PlayerIdentifier.exoPlayer3)
        player: Player,
        mediaPlayingList: MediaPlayingList,
        dataSourceFactory: DataSource.Factory,
        @Qualifier(name = PlayerIdentifier.playerLogger)
        logger: Logger
    ): MediaController = BizMediaController(player, mediaPlayingList, dataSourceFactory, logger)

    @Single
    internal fun playInfo(
        @Qualifier(name = PlayerIdentifier.exoPlayer3)
        player: Player,
        mediaPlayingList: MediaPlayingList
    ): PlayInfo = BizPlayInfo(player, mediaPlayingList)

    @Single
    internal fun playEventLoopHost(
        playInfo: PlayInfo,
        @Qualifier(name = PlayerIdentifier.bizMediaController)
        mediaController: MediaController,
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        @Qualifier(name = PlayerIdentifier.playServiceHandlerThread)
        handler: Handler,
        @Qualifier(name = PlayerIdentifier.exoPlayer3)
        player: Player,
    ): PlayEventLoopHost = PlayEventLoopHost(playInfo, mediaController, context, handler, player)

    @Single
    internal fun mediaPlayingList(): MediaPlayingList = MediaPlayingList()

    @Factory
    internal fun playEventLoop(host: PlayEventLoopHost): PlayEventLoop = host

    @Single
    internal fun playerAudioFocusManager(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        @Qualifier(name = PlayerIdentifier.proxyMediaController)
        mediaController: MediaController,
        playInfo: PlayInfo,
        coroutineScope: CoroutineScope
    ): PlayerAudioFocusManager =
        PlayerAudioFocusManager(context, mediaController, playInfo, coroutineScope)

    @Single
    internal fun playMediaSession(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        playInfo: PlayInfo,
        @Qualifier(name = PlayerIdentifier.proxyMediaController)
        mediaController: MediaController,
        coroutineScope: CoroutineScope
    ): PlayMediaSession = PlayMediaSession(context, mediaController, playInfo, coroutineScope)

    @Single
    internal fun remoteMediaServiceProxy(
        @Qualifier(name = PlayerIdentifier.playServiceHandlerThread)
        handler: Handler,
        playInfo: PlayInfo,
        coroutineScope: CoroutineScope
    ): RemoteMediaServiceProxy = RemoteMediaServiceProxy(handler, playInfo, coroutineScope)

    @Single
    internal fun playNotificationManager(
        @Qualifier(name = PlayerIdentifier.playService)
        service: Service,
        playInfo: PlayInfo,
        @Qualifier(name = PlayerIdentifier.proxyMediaController)
        mediaController: MediaController,
        coroutineScope: CoroutineScope
    ): PlayNotificationManager =
        PlayNotificationManager(service, mediaController, playInfo, coroutineScope)
}