package com.music.android.lin.player.service.controller

import com.harvest.musicplayer.MediaRepository
import com.harvest.musicplayer.MediaStoreUpdateOptions
import com.harvest.musicplayer.PlayerServiceController
import com.music.android.lin.player.repositories.updater.RepositoryUpdaterFactory

/**
 * @author liuzhongao
 * @since 2024/1/27 23:40
 */
class PlayerServiceControllerImpl(
    private val mediaRepository: MediaRepository
) : PlayerServiceController {

    override suspend fun scanSystemMediaStorage(options: MediaStoreUpdateOptions) {
        RepositoryUpdaterFactory.create(mediaStoreUpdateOptions = options).updateRepository(mediaRepository = this.mediaRepository)
    }
}