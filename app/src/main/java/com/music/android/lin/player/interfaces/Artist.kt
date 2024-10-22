package com.harvest.musicplayer

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
        private const val serialVersionUID: Long = 2627915481637693252L
        override val id: String get() = ""
        override val name: String get() = "未知歌手"
        override val description: String get() = ""
        override val numberOfAlbum: Int get() = 0
    }
}