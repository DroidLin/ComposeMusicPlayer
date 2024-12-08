package com.music.android.lin.application.common.usecase

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import com.music.android.lin.application.common.usecase.util.toMusicItem
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.metadata.MediaInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map

internal class PrepareMusicInfoUseCase(
    private val mediaRepository: MediaRepository,
    private val applicationContext: Context
) {

    val mediaItemList = this.mediaRepository.observableMusicInfoList()
        .map { musicInfoList ->
            val languageContext = ContextCompat.getContextForLanguage(this.applicationContext)
            val musicItemList = musicInfoList.map { mediaInfo ->
                mediaInfo.toMusicItem(languageContext)
            }
            MusicItemSnapshot(
                musicItemList = musicItemList.toImmutableList(),
                mediaInfoList = musicInfoList.toImmutableList()
            )
        }
}

@Stable
data class MusicItemSnapshot(
    val musicItemList: ImmutableList<MusicItem>,
    val mediaInfoList: ImmutableList<MediaInfo>,
)

@Stable
data class MusicItem(
    val mediaId: String,
    val musicName: String,
    val musicDescription: String,
    val musicCover: String?,
    val mediaQuality: MediaQuality
)

enum class MediaQuality {
    NQ,
    HQ,
    SQ,
}