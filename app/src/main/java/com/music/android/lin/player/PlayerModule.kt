package com.music.android.lin.player

import com.music.android.lin.modules.AppIdentifier
import com.music.android.lin.player.audiofocus.PlayerAudioFocusManager
import com.music.android.lin.player.notification.PlayNotificationManager
import com.music.android.lin.player.notification.PlayMediaSession
import com.music.android.lin.player.repositories.DatabaseService
import com.music.android.lin.player.repositories.MusicDatabaseService
import com.music.android.lin.player.service.controller.BizPlayerControl
import com.music.android.lin.player.service.controller.IPlayerService
import com.music.android.lin.player.service.controller.PlayerControl
import com.music.android.lin.player.service.controller.PlayerServiceHost
import com.music.android.lin.player.service.player.ExoMediaPlayer
import com.music.android.lin.player.service.player.MediaPlayerProxy
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.playlist.MediaPlayingList
import org.koin.dsl.module
import java.util.logging.Logger

internal val PlayerModule = module {
    single<Logger>(PlayerIdentifier.PlayerLogger) {
        Logger.getLogger("PlayerLogger")
    }
    single<Player>(PlayerIdentifier.ExoPlayer3) {
        ExoMediaPlayer(
            handler = get(),
            logger = get(PlayerIdentifier.PlayerLogger)
        )
    }
    single<MediaPlayingList> {
        MediaPlayingList()
    }
    single<PlayerControl> {
        MediaPlayerProxy(
            playerService = get(),
            playerControl = get(PlayerIdentifier.PlayerControl)
        )
    }
    single<PlayerControl>(PlayerIdentifier.PlayerControl) {
        BizPlayerControl(
            player = get(PlayerIdentifier.ExoPlayer3),
            mediaList = get(),
            dataSourceFactory = get(),
            logger = get(PlayerIdentifier.PlayerLogger),
        )
    }
    single<IPlayerService> {
        PlayerServiceHost(
            playerControl = get(PlayerIdentifier.PlayerControl),
            coroutineScope = get(),
            context = get(AppIdentifier.ApplicationContext),
            handler = get()
        )
    }
    single {
        PlayerAudioFocusManager(
            context = get(AppIdentifier.ApplicationContext),
            playerControl = get(),
            coroutineScope = get()
        )
    }
    single {
        PlayNotificationManager(
            service = get(PlayerIdentifier.PlayService),
            playerControl = get(),
            coroutineScope = get()
        )
    }
    single<PlayMediaSession> {
        PlayMediaSession(
            context = get(AppIdentifier.ApplicationContext),
            playerControl = get(),
            coroutineScope = get()
        )
    }
}