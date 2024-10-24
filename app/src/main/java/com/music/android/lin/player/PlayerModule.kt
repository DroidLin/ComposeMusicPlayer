package com.music.android.lin.player

import com.music.android.lin.modules.AppIdentifier
import com.music.android.lin.player.audiofocus.PlayerAudioFocusManager
import com.music.android.lin.player.notification.PlayNotificationManager
import com.music.android.lin.player.notification.android.PlayMediaSession
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
    single<DatabaseService> {
        MusicDatabaseService(
            applicationContext = get(AppIdentifier.ApplicationContext),
            accessToken = get(PlayerIdentifier.PlayerDatabaseAccessToken)
        )
    }
    single<Logger>(PlayerIdentifier.PlayerLogger) {
        Logger.getLogger("PlayerLogger")
    }
    single<Player>(PlayerIdentifier.ExoPlayer3) {
        ExoMediaPlayer(get(), get(PlayerIdentifier.PlayerLogger))
    }
    single<MediaPlayingList> {
        MediaPlayingList()
    }
    single<PlayerControl> {
        MediaPlayerProxy(get(), get())
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
            notificationManager = get(),
            playMediaSession = get(),
            audioFocusManager = get(),
            handler = get()
        )
    }
    single {
        PlayerAudioFocusManager(get(AppIdentifier.ApplicationContext), get(), get())
    }
    single {
        PlayNotificationManager(get(), get())
    }
    single<PlayMediaSession> {
        PlayMediaSession(get(), get(), get())
    }
}