package com.music.android.lin.player.metadata

import com.music.android.lin.player.interfaces.Album

/**
 * @author liuzhongao
 * @since 2023/10/6 14:26
 */
internal data class AlbumImpl(
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