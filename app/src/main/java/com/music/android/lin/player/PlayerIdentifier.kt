package com.music.android.lin.player

import org.koin.core.qualifier.named

/**
 * defines a set of qualifiers about player module.
 *
 * @author: liuzhongao
 * @since: 2024/10/22 22:56
 */
object PlayerIdentifier {

    const val playerDatabaseAccessToken = "PlayerAccessToken"
    /**
     * define a database access token qualifier in dependencies injection.
     */
    @JvmField
    val PlayerDatabaseAccessToken = named(playerDatabaseAccessToken)

    const val playerLogger = "player_logger"
    @JvmField
    val PlayerLogger= named(playerLogger)

    const val exoPlayer3 = "exo_player3"
    @JvmField
    val ExoPlayer3 = named(exoPlayer3)

    const val playService = "play_service"
    @JvmField
    val PlayService = named(playService)

    const val playerControl = "player_control"
    @JvmField
    val PlayerControl = named(playerControl)

    const val bizMediaController = "biz_media_controller"
    @JvmField
    val BizMediaController = named(bizMediaController)

    const val proxyMediaController = "proxy_media_controller"
    @JvmField
    val ProxyMediaController = named(proxyMediaController)

    const val playServiceHandlerThread = "play_service_handler_thread"
    @JvmField
    val PlayServiceHandlerThread = named(playServiceHandlerThread)

    const val playerCoroutineScope = "player_coroutineScope"
}