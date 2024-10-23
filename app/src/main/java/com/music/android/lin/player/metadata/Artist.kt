package com.music.android.lin.player.metadata

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/5 23:12
 */
interface Artist : Serializable {

    val id: String

    val name: String

    val description: String

    val numberOfAlbum: Int

    companion object EMPTY : Artist {
        private fun readResolve(): Any = EMPTY
        private const val serialVersionUID: Long = 2627915481637693252L
        override val id: String get() = ""
        override val name: String get() = "未知歌手"
        override val description: String get() = ""
        override val numberOfAlbum: Int get() = 0
    }
}

fun Artist(
    id: String,
    name: String,
    description: String,
    numberOfAlbum: Int
): Artist {
    return ArtistImpl(id, name, description, numberOfAlbum)
}

private data class ArtistImpl(
    override val id: String,
    override val name: String,
    override val description: String,
    override val numberOfAlbum: Int
) : Artist {
    companion object {
        private const val serialVersionUID: Long = -7143936669204194110L
    }
}