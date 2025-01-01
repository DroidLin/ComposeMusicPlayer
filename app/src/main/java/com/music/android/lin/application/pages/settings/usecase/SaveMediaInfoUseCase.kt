package com.music.android.lin.application.pages.settings.usecase

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.repositories.DatabaseService

class SaveMediaInfoUseCase(private val databaseService: DatabaseService) {

    suspend fun saveMediaInfo(mediaInfoList: List<MediaInfo>) {
        this.databaseService.mediaRepository.insertMediaInfo(mediaInfoList)
    }

    suspend fun upsertMediaInfo(mediaInfoList: List<MediaInfo>) {
        this.databaseService.mediaRepository.upsertMediaInfo(mediaInfoList)
    }
}