package com.music.android.lin.player.metadata

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/5 23:13
 */
interface Album : Serializable {

    val id: String

    val albumName: String

    val albumDescription: String

    val publishDate: Long

    val mediaNumber: Int

    val coverUrl: String

    companion object Empty : Album {
        private const val serialVersionUID: Long = -181157103833628741L
        override val id: String get() = ""
        override val albumName: String get() = "默认专辑"
        override val albumDescription: String get() = ""
        override val publishDate: Long get() = 0
        override val mediaNumber: Int get() = 0
        override val coverUrl: String get() = ""
    }
}

fun Album(
    id: String,
    albumName: String,
    albumDescription: String,
    publishDate: Long,
    mediaNumber: Int,
    coverUrl: String
) : Album {
    return AlbumImpl(id, albumName, albumDescription, publishDate, mediaNumber, coverUrl)
}

private data class AlbumImpl(
    override val id: String,
    override val albumName: String,
    override val albumDescription: String,
    override val publishDate: Long,
    override val mediaNumber: Int,
    override val coverUrl: String
) : Album {
    companion object {
        private const val serialVersionUID: Long = 5812140679130939661L
    }
}