package com.music.android.lin.application.common.usecase

import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.metadata.MediaInfo
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
internal class FetchMediaInfoUseCase(
    private val mediaRepository: MediaRepository
) {

    fun observableMediaInfo(mediaId: String): Flow<MediaInfo?> {
        return this.mediaRepository.observableMediaInfo(mediaInfoId = mediaId)
    }
}