package com.music.android.lin.player.repositories.updater

import com.music.android.lin.player.interfaces.MediaRepository
import java.io.File

/**
 * @author liuzhongao
 * @since 2023/10/16 2:19 PM
 */
internal class FileScannerRepositoryUpdater(
    private val includeFileDirectories: List<File>,
    private val filterDuration: Long,
) : RepositoryUpdater {

    override suspend fun updateRepository(mediaRepository: MediaRepository) {

    }
}