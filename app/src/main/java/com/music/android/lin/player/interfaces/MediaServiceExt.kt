package com.music.android.lin.player.interfaces

/**
 * @author liuzhongao
 * @since 2023/10/16 3:01 PM
 */

suspend fun PlayerServiceController.scanMediaStore(scope: MediaStoreUpdateOptionsScope.() -> Unit) {
    val mediaStoreUpdateOptions = MediaStoreUpdateOptionsScope().also(scope).build()
    this.scanSystemMediaStorage(options = mediaStoreUpdateOptions)
}