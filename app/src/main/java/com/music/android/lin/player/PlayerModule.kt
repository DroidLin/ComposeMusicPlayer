package com.music.android.lin.player

import android.content.Context
import android.os.Handler
import com.music.android.lin.modules.AppIdentifier
import com.music.android.lin.player.service.controller.MediaController
import com.music.android.lin.player.service.controller.PlayEventLoop
import com.music.android.lin.player.service.controller.PlayEventLoopHost
import com.music.android.lin.player.service.controller.PlayInfo
import com.music.android.lin.player.service.controller.ProxyMediaController
import com.music.android.lin.player.service.player.ExoMediaPlayer
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.playlist.MediaPlayingList
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Property
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single
import org.koin.dsl.module
import java.util.logging.Logger

@Module
class PlayModule {

    @Single
    @Qualifier(name = PlayerIdentifier.playerLogger)
    fun logger(): Logger = Logger.getLogger("PlayerLogger")

    @Single
    @Qualifier(name = PlayerIdentifier.exoPlayer3)
    fun exoPlayer(
        @Named(PlayerIdentifier.playServiceHandlerThread)
        handler: Handler,
        logger: Logger
    ): Player = ExoMediaPlayer(handler, logger)

    @Single
    @Qualifier(name = PlayerIdentifier.proxyMediaController)
    fun proxyMediaController(playEventLoop: PlayEventLoop): MediaController =
        ProxyMediaController(playEventLoop::dispatchMessage)

    @Single
    internal fun playEventLoopHost(
        playInfo: PlayInfo,
        mediaController: MediaController,
        @Named(AppIdentifier.applicationContext)
        context: Context,
        @Named(PlayerIdentifier.playServiceHandlerThread)
        handler: Handler
    ): PlayEventLoopHost = PlayEventLoopHost(playInfo, mediaController, context, handler)

    @Factory
    internal fun playEventLoop(host: PlayEventLoopHost): PlayEventLoop = host
}

internal val PlayerModule = module {
//    single<Logger>(PlayerIdentifier.PlayerLogger) {
//        Logger.getLogger("PlayerLogger")
//    }
//    single<Player>(PlayerIdentifier.ExoPlayer3) {
//        ExoMediaPlayer(
//            handler = get(PlayerIdentifier.PlayServiceHandlerThread),
//            logger = get(PlayerIdentifier.PlayerLogger)
//        )
//    }
    single<MediaPlayingList> {
        MediaPlayingList()
    }
//    single<PlayInfo> {
//        BizPlayInfo(
//            player = get(PlayerIdentifier.ExoPlayer3),
//            mediaList = get(),
//        )
//    }
//    single<MediaController>(PlayerIdentifier.BizMediaController) {
//        BizMediaController(
//            player = get(PlayerIdentifier.ExoPlayer3),
//            mediaList = get(),
//            dataSourceFactory = get() { it },
//            logger = get(PlayerIdentifier.PlayerLogger),
//        )
//    }
//    single<MediaController>(PlayerIdentifier.ProxyMediaController) {
//        ProxyMediaController(
//            dispatcher = get<PlayEventLoop>()::dispatchMessage,
//        )
//    }
    single<PlayEventLoop> {
        get<PlayEventLoopHost>()
    }
//    single {
//        PlayEventLoopHost(
//            playInfo = get { it },
//            mediaController = get(PlayerIdentifier.BizMediaController) { it },
//            context = get(AppIdentifier.ApplicationContext),
//            handler = get(PlayerIdentifier.PlayServiceHandlerThread) { it }
//        )
//    }
//    single {
//        PlayerAudioFocusManager(
//            context = get(AppIdentifier.ApplicationContext),
//            playInfo = get(),
//            mediaController = get(PlayerIdentifier.ProxyMediaController),
//            coroutineScope = get()
//        )
//    }
//    single {
//        PlayNotificationManager(
//            service = get(PlayerIdentifier.PlayService),
//            playInfo = get(),
//            mediaController = get(PlayerIdentifier.ProxyMediaController),
//            coroutineScope = get()
//        )
//    }
//    single<PlayMediaSession> {
//        PlayMediaSession(
//            context = get(AppIdentifier.ApplicationContext),
//            playInfo = get(),
//            mediaController = get(PlayerIdentifier.ProxyMediaController),
//            coroutineScope = get()
//        )
//    }
//    single {
//        RemoteMediaServiceProxy(
//            handler = get(PlayerIdentifier.PlayServiceHandlerThread),
//            playInfo = get(),
//            coroutineScope = get()
//        )
//    }
}