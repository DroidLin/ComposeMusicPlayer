package com.music.android.lin.player

import com.music.android.lin.modules.AppIdentifier
import com.music.android.lin.player.repositories.DatabaseService
import com.music.android.lin.player.repositories.MusicDatabaseService
import com.music.android.lin.player.service.controller.BizPlayerControl
import com.music.android.lin.player.service.controller.IPlayerService
import com.music.android.lin.player.service.controller.PlayerControl
import com.music.android.lin.player.service.controller.PlayerServiceHost
import com.music.android.lin.player.service.player.ExoMediaPlayer
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.playlist.MediaList
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
        BizPlayerControl(get(PlayerIdentifier.ExoPlayer3), get(), get(), get(PlayerIdentifier.PlayerLogger))
    }
    single<IPlayerService> {
        PlayerServiceHost(get(), get(), get(AppIdentifier.ApplicationContext))
    }
}