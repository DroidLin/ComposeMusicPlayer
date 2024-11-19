package com.music.android.lin.application.settings.usecase

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.repositories.DatabaseService
import org.koin.core.annotation.Factory

class SaveMediaInfoUseCase(private val databaseService: DatabaseService) {

    suspend fun saveMediaInfo(mediaInfoList: List<MediaInfo>) {
        this.databaseService.mediaRepository.insertMediaInfo(mediaInfoList)
    }
}