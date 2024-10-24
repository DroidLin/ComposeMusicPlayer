package com.music.android.lin.player.repositories.updater

import com.music.android.lin.player.database.MediaRepository


/**
 * @author liuzhongao
 * @since 2023/10/15 8:23 PM
 */
internal interface RepositoryUpdater {

    suspend fun updateRepository(mediaRepository: MediaRepository)
}