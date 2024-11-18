package com.music.android.lin.player

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
import com.music.android.lin.player.service.playlist.MediaPlayingList
import kotlinx.coroutines.coroutineScope
import org.koin.dsl.module
import java.util.logging.Logger

internal val PlayerModule = module {
    single<Logger>(PlayerIdentifier.PlayerLogger) {
        Logger.getLogger("PlayerLogger")
    }
    single<Player>(PlayerIdentifier.ExoPlayer3) {
        ExoMediaPlayer(
            handler = get(PlayerIdentifier.PlayServiceHandlerThread),
            logger = get(PlayerIdentifier.PlayerLogger)
        )
    }
    single<MediaPlayingList> {
        MediaPlayingList()
    }
    single<PlayInfo> {
        BizPlayInfo(
            player = get(PlayerIdentifier.ExoPlayer3),
            mediaList = get(),
        )
    }
    single<MediaController>(PlayerIdentifier.BizMediaController) {
        BizMediaController(
            player = get(PlayerIdentifier.ExoPlayer3),
            mediaList = get(),
            dataSourceFactory = get() { it },
            logger = get(PlayerIdentifier.PlayerLogger),
        )
    }
    single<MediaController>(PlayerIdentifier.ProxyMediaController) {
        ProxyMediaController(
            dispatcher = get<PlayEventLoop>()::dispatchMessage,
        )
    }
    single<PlayEventLoop> {
        get<PlayEventLoopHost>()
    }
    single {
        PlayEventLoopHost(
            playInfo = get { it },
            mediaController = get(PlayerIdentifier.BizMediaController) { it },
            coroutineScope = get { it },
            context = get(AppIdentifier.ApplicationContext),
            handler = get(PlayerIdentifier.PlayServiceHandlerThread) { it }
        )
    }
    single {
        PlayerAudioFocusManager(
            context = get(AppIdentifier.ApplicationContext),
            playInfo = get(),
            mediaController = get(PlayerIdentifier.ProxyMediaController),
            coroutineScope = get()
        )
    }
    single {
        PlayNotificationManager(
            service = get(PlayerIdentifier.PlayService),
            playInfo = get(),
            mediaController = get(PlayerIdentifier.ProxyMediaController),
            coroutineScope = get()
        )
    }
    single<PlayMediaSession> {
        PlayMediaSession(
            context = get(AppIdentifier.ApplicationContext),
            playInfo = get(),
            mediaController = get(PlayerIdentifier.ProxyMediaController),
            coroutineScope = get()
        )
    }
    single {
        RemoteMediaServiceProxy(
            handler = get(PlayerIdentifier.PlayServiceHandlerThread),
            playInfo = get(),
            coroutineScope = get()
        )
    }
}