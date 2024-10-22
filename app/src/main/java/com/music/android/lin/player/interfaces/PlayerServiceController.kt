package com.music.android.lin.player.interfaces

/**
 * @author liuzhongao
 * @since 2024/1/27 23:37
 */
interface PlayerServiceController {

    suspend fun scanSystemMediaStorage(options: MediaStoreUpdateOptions)
}