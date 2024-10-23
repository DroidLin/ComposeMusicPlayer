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
}