package com.harvest.musicplayer.repositories.updater

import com.harvest.musicplayer.MediaRepository

/**
 * @author liuzhongao
 * @since 2023/12/13 11:33 PM
 */
internal class RepositoryUpdaterWrapper : RepositoryUpdater {

    private val updateList: MutableList<RepositoryUpdater> = mutableListOf()

    operator fun plusAssign(repositoryUpdater: RepositoryUpdater) {
        this.updateList += repositoryUpdater
    }

    override suspend fun updateRepository(mediaRepository: MediaRepository) {
        this.updateList.forEach { it.updateRepository(mediaRepository) }
    }
}