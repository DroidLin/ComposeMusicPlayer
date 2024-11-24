package com.music.android.lin.application.common.usecase.util

import android.content.Context
import com.music.android.lin.R
import com.music.android.lin.application.common.usecase.MediaQuality
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.player.metadata.MediaInfo

val MediaInfo.mediaQuality: MediaQuality
    get() = when (this.mediaExtras.bitRate) {
        in (0 until 128000) -> MediaQuality.NQ
        in (128000 until 320000) -> MediaQuality.HQ
        else -> MediaQuality.SQ
    }

fun MediaInfo.toMusicItem(context: Context): MusicItem {
    val mediaInfo = this
    return MusicItem(
        mediaId = mediaInfo.id,
        musicName = mediaInfo.mediaTitle,
        musicDescription = StringBuilder().also { builder ->
            val artist = mediaInfo.artists
            if (artist.isEmpty()) {
                val descriptions = context.getString(
                    R.string.string_album_placeholder,
                    mediaInfo.album.albumName
                )
                builder.append(descriptions)
            } else {
                builder.append(artist.joinToString(separator = "/") { it.name })
            }
        }.toString(),
        musicCover = mediaInfo.coverUri,
        mediaQuality = mediaInfo.mediaQuality
    )
}