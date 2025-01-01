package com.music.android.lin.player.metadata

import java.io.Serial

interface MutableMediaInfo : MediaInfo {
    override var mediaTitle: String
    override var mediaDescription: String
    override var coverUri: String?
}

fun MutableMediaInfo(mediaInfo: MediaInfo): MutableMediaInfo {
    return MutableMediaInfoImpl(mediaInfo = mediaInfo)
}

fun MutableMediaInfo.immutable(): MediaInfo {
    return MediaInfo(this)
}

fun MediaInfo.mutable(): MutableMediaInfo {
    return MutableMediaInfo(this)
}

private class MutableMediaInfoImpl(
    override val id: String,
    override var mediaTitle: String,
    override var mediaDescription: String,
    override var coverUri: String?,
    override val artists: List<Artist>,
    override val album: Album,
    override val mediaType: MediaType,
    override val sourceUri: String?,
    override val updateTimeStamp: Long,
    override val mediaExtras: MediaExtras,
) : MutableMediaInfo {

    constructor(mediaInfo: MediaInfo) : this(
        id = mediaInfo.id,
        mediaTitle = mediaInfo.mediaTitle,
        mediaDescription = mediaInfo.mediaDescription,
        coverUri = mediaInfo.coverUri,
        artists = mediaInfo.artists,
        album = mediaInfo.album,
        mediaType = mediaInfo.mediaType,
        sourceUri = mediaInfo.sourceUri,
        updateTimeStamp = mediaInfo.updateTimeStamp,
        mediaExtras = mediaInfo.mediaExtras,
    )

    companion object {
        @Serial
        private const val serialVersionUID: Long = -3349897129489117309L
    }
}