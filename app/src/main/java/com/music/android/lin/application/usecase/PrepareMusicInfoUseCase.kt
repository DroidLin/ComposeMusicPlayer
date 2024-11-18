package com.music.android.lin.application.usecase

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import com.music.android.lin.R
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.metadata.MediaInfo
import kotlinx.coroutines.flow.map

private val MediaInfo.mediaQuality: MediaQuality
    get() = when (this.mediaExtras.bitRate) {
        in (0..192000) -> MediaQuality.NQ
        in (192000..320000) -> MediaQuality.HQ
        else -> MediaQuality.SQ
    }

internal class PrepareMusicInfoUseCase(
    private val mediaRepository: MediaRepository,
    private val applicationContext: Context
) {

    val mediaItemList = this.mediaRepository.observableMusicInfoList()
        .map { musicInfoList ->
            val languageContext = ContextCompat.getContextForLanguage(this.applicationContext)
            val musicItemList = musicInfoList.map { mediaInfo ->
                MusicItem(
                    mediaId = mediaInfo.id,
                    musicName = mediaInfo.mediaTitle,
                    musicDescription = StringBuilder().also { builder ->
                        val artist = mediaInfo.artists
                        if (artist.isEmpty()) {
                            val descriptions = languageContext.getString(R.string.string_album_placeholder, mediaInfo.album.albumName)
                            builder.append(descriptions)
                        } else {
                            builder.append(artist.joinToString(separator = "/") { it.name })
                        }
                    }.toString(),
                    musicCover = mediaInfo.coverUri,
                    mediaQuality = mediaInfo.mediaQuality
                )
            }
            MusicItemSnapshot(
                musicItemList = musicItemList,
                mediaInfoList = musicInfoList
            )
        }
}

@Stable
class MusicItemSnapshot(
    val musicItemList: List<MusicItem>,
    val mediaInfoList: List<MediaInfo>
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