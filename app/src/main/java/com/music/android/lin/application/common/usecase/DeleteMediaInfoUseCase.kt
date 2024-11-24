package com.music.android.lin.application.common.usecase

import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.metadata.MediaInfo

class DeleteMediaInfoUseCase(private val repository: MediaRepository) {

    suspend fun deleteMediaInfo(mediaInfoId: String): Boolean {
        val mediaInfo = this.repository.fetchMediaInfo(mediaInfoId) ?: return false
        return deleteMediaInfo(mediaInfo)
    }

    suspend fun deleteMediaInfo(mediaInfo: MediaInfo): Boolean {
        return kotlin.runCatching {
            this.repository.deleteMediaInfo(mediaInfo)
        }.isSuccess
    }
}