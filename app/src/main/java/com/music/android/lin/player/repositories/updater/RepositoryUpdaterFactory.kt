package com.music.android.lin.player.repositories.updater

import com.harvest.musicplayer.MediaStoreUpdateOptions
import com.harvest.musicplayer.repositories.updater.FileScannerRepositoryUpdater
import com.harvest.musicplayer.repositories.updater.RepositoryUpdater
import com.harvest.musicplayer.repositories.updater.RepositoryUpdaterWrapper
import com.harvest.musicplayer.repositories.updater.VideoRepositoryUpdater
import com.music.android.lin.applicationContext

/**
 * @author liuzhongao
 * @since 2023/10/16 11:24
 */
internal object RepositoryUpdaterFactory {

    @JvmStatic
    fun create(mediaStoreUpdateOptions: MediaStoreUpdateOptions): RepositoryUpdater {
        val repositoryUpdaterWrapper = RepositoryUpdaterWrapper()

        val videoContentProviderOptions = mediaStoreUpdateOptions.videoContentProviderOptions
        if (videoContentProviderOptions != null) {
            repositoryUpdaterWrapper += VideoRepositoryUpdater(
                context = applicationContext,
                filterDuration = videoContentProviderOptions.filterDuration
            )
        }

        val updateContentProviderOptions = mediaStoreUpdateOptions.contentProviderOptions
        if (updateContentProviderOptions != null) {
            repositoryUpdaterWrapper += MusicRepositoryUpdater(
                filterDuration = updateContentProviderOptions.filterDuration,
                context = applicationContext
            )
        }

        val scanFileDirectoryOption = mediaStoreUpdateOptions.fileScannerOptions
        if (scanFileDirectoryOption != null) {
            repositoryUpdaterWrapper += FileScannerRepositoryUpdater(
                includeFileDirectories = scanFileDirectoryOption.includeFileDirectory,
                filterDuration = scanFileDirectoryOption.filterDuration
            )
        }
        return repositoryUpdaterWrapper
    }
}