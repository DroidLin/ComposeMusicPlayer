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