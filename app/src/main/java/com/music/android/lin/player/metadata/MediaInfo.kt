package com.music.android.lin.player.metadata

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/5 17:47
 */
interface MediaInfo : Serializable {

    val id: String

    val mediaTitle: String

    val mediaDescription: String

    val artists: List<Artist>

    val album: Album

    val mediaType: MediaType

    val coverUri: String?

    val sourceUri: String?

    val updateTimeStamp: Long

    val mediaExtras: MediaExtras

    companion object Empty : MediaInfo {
        private fun readResolve(): Any = Empty
        private const val serialVersionUID: Long = -8158499221684539250L
        override val id: String get() = "0"
        override val mediaTitle: String get() = ""
        override val mediaDescription: String get() = ""
        override val artists: List<Artist> get() = emptyList()
        override val album: Album get() = Album
        override val mediaType: MediaType get() = MediaType.Unsupported
        override val coverUri: String? get() = null
        override val sourceUri: String? get() = null
        override val updateTimeStamp: Long get() = 0L
        override val mediaExtras: MediaExtras get() = MediaExtras
    }
}

fun MediaInfo(
    id: String,
    mediaTitle: String,
    mediaDescription: String,
    artists: List<Artist>,
    album: Album,
    coverUri: String?,
    sourceUri: String?,
    updateTimeStamp: Long,
    mediaType: MediaType,
    mediaExtras: MediaExtras
): MediaInfo {
    return MediaInfoImpl(
        id = id,
        mediaTitle = mediaTitle,
        mediaDescription = mediaDescription,
        artists = artists,
        album = album,
        coverUri = coverUri,
        sourceUri = sourceUri,
        updateTimeStamp = updateTimeStamp,
        mediaType = mediaType,
        mediaExtras = mediaExtras
    )
}

private data class MediaInfoImpl(
    override val id: String,
    override val mediaTitle: String,
    override val mediaDescription: String,
    override val artists: List<Artist>,
    override val album: Album,
    override val coverUri: String?,
    override val sourceUri: String?,
    override val updateTimeStamp: Long,
    override val mediaType: MediaType,
    override val mediaExtras: MediaExtras
) : MediaInfo {
    companion object {
        private const val serialVersionUID: Long = -8145443343434315122L
    }
}