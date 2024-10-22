package com.music.android.lin.player.metadata

import com.music.android.lin.player.interfaces.Artist

/**
 * @author liuzhongao
 * @since 2023/10/6 14:22
 */
internal data class ArtistImpl(
    override val id: String,
    override val name: String,
    override val description: String,
    override val numberOfAlbum: Int
) : Artist {
    companion object {
        private const val serialVersionUID: Long = -7143936669204194110L
    }
}