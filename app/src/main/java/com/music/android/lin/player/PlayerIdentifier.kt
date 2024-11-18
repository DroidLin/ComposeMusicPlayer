package com.music.android.lin.player

import org.koin.core.qualifier.named

/**
 * defines a set of qualifiers about player module.
 *
 * @author: liuzhongao
 * @since: 2024/10/22 22:56
 */
object PlayerIdentifier {

    /**
     * define a database access token qualifier in dependencies injection.
     */
    @JvmField
    val PlayerDatabaseAccessToken = named("PlayerAccessToken")

    @JvmField
    val PlayerLogger= named("player_logger")

    @JvmField
    val ExoPlayer3 = named("exo_player3")

    @JvmField
    val PlayService = named("play_service")

    @JvmField
    val PlayerControl = named("player_control")

    @JvmField
    val BizMediaController = named("biz_media_controller")

    @JvmField
    val ProxyMediaController = named("proxy_media_controller")

    @JvmField
    val PlayServiceHandlerThread = named("play_service_handler_thread")
}